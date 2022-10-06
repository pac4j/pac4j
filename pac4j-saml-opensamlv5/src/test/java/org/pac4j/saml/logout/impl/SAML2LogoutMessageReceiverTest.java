package org.pac4j.saml.logout.impl;

import net.shibboleth.utilities.java.support.net.impl.BasicURLComparator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.store.HttpSessionStoreFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class SAML2LogoutMessageReceiverTest {

    @Test
    public void shouldAcceptLogoutResponse() {
        var webContext = getMockWebContext();
        var context = getSaml2MessageContext(webContext);
        SAML2ResponseValidator validator = getLogoutValidator("/logoutUrl");

        var unit = new SAML2LogoutMessageReceiver(validator, context.getSAML2Configuration());
        try {
            unit.receiveMessage(context);
            fail("Should have thrown a FoundAction");
        } catch (SAMLException e) {
            fail(e.getMessage());
        } catch (FoundAction e) {
            assertTrue("SAML2LogoutMessageReceiver processed the logout message successfully", true);
            MatcherAssert.assertThat(e.getLocation(), is("/logoutUrl"));
        }
    }

    @Test
    public void shouldAcceptLogoutResponseWithNoRedirect() {
        var webContext = getMockWebContext();
        var context = getSaml2MessageContext(webContext);
        SAML2ResponseValidator validator = getLogoutValidator(Pac4jConstants.EMPTY_STRING);

        var unit = new SAML2LogoutMessageReceiver(validator, context.getSAML2Configuration());
        try {
            unit.receiveMessage(context);
            fail("Should have thrown a FoundAction");
        } catch (SAMLException e) {
            fail(e.getMessage());
        } catch (OkAction e) {
            assertTrue("SAML2LogoutMessageReceiver processed the logout message successfully", true);
            MatcherAssert.assertThat(e.getContent(), is(Pac4jConstants.EMPTY_STRING));
        }
    }

    @Test
    public void shouldAcceptLogoutResponseWithNoActionOnSuccess() {
        var webContext = getMockWebContext();
        var context = getSaml2MessageContext(webContext);
        var validator = getLogoutValidator(Pac4jConstants.EMPTY_STRING);
        validator.setActionOnSuccess(false);

        var unit = new SAML2LogoutMessageReceiver(validator, getSaml2Configuration());
        try {
            unit.receiveMessage(context);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private SAML2LogoutValidator getLogoutValidator(final String postLogoutUrl) {
        var config = new SAML2Configuration();
        config.setForceKeystoreGeneration(true);
        config.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        config.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        config.setForceServiceProviderMetadataGeneration(true);
        config.setKeystorePath("target/keystore.jks");
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.init();

        var idp = new SAML2IdentityProviderMetadataResolver(config);
        idp.init();

        var sp = new SAML2ServiceProviderMetadataResolver(config);

        SAML2SignatureTrustEngineProvider engine = new ExplicitSignatureTrustEngineProvider(idp, sp);

        var validator = new SAML2LogoutValidator(
            engine,
            mock(Decrypter.class),
            mock(LogoutHandler.class),
            postLogoutUrl,
            mock(ReplayCacheProvider.class),
            new BasicURLComparator()
        );
        return validator;
    }

    private MockWebContext getMockWebContext() {
        var webContext = MockWebContext.create();
        webContext.setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        webContext.setRequestContent(
            String.format(
                "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
                    "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
                    "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
                    "IssueInstant=\"%s\" Destination=\"http://sp.example.com/demo1/logout\" " +
                    "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">%n" +
                    "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>%n" +
                    "  <samlp:Status>%n" +
                    "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>%n" +
                    "  </samlp:Status>%n" +
                    "</samlp:LogoutResponse>",
                ZonedDateTime.now(ZoneOffset.UTC)
            )
        );
        return webContext;
    }

    private SAML2MessageContext getSaml2MessageContext(MockWebContext webContext) {
        var context = new SAML2MessageContext();
        context.setSaml2Configuration(getSaml2Configuration());
        var entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);
        context.setWebContext(webContext);
        context.setSessionStore(new MockSessionStore());

        var spDescriptor = new SPSSODescriptorBuilder().buildObject();
        var logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        return context;
    }

    protected SAML2Configuration getSaml2Configuration() {
        final var cfg = new SAML2Configuration(new FileSystemResource("target/samlKeystore.jks"),
            "pac4j-demo-passwd",
            "pac4j-demo-passwd",
            new ClassPathResource("testshib-providers.xml"));

        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setForceServiceProviderMetadataGeneration(true);
        cfg.setForceKeystoreGeneration(true);
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));
        cfg.setSamlMessageStoreFactory(new HttpSessionStoreFactory());
        return cfg;
    }
}
