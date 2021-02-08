package org.pac4j.saml.logout.impl;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class SAML2LogoutMessageReceiverTest {

    @Test
    public void shouldAcceptLogoutResponse() {
        MockWebContext webContext = getMockWebContext();
        SAML2MessageContext context = getSaml2MessageContext(webContext);
        SAML2ResponseValidator validator = getLogoutValidator("/logoutUrl");

        SAML2LogoutMessageReceiver unit = new SAML2LogoutMessageReceiver(validator);
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
        MockWebContext webContext = getMockWebContext();
        SAML2MessageContext context = getSaml2MessageContext(webContext);
        SAML2ResponseValidator validator = getLogoutValidator("");

        SAML2LogoutMessageReceiver unit = new SAML2LogoutMessageReceiver(validator);
        try {
            unit.receiveMessage(context);
            fail("Should have thrown a FoundAction");
        } catch (SAMLException e) {
            fail(e.getMessage());
        } catch (OkAction e) {
            assertTrue("SAML2LogoutMessageReceiver processed the logout message successfully", true);
            MatcherAssert.assertThat(e.getContent(), is(""));
        }
    }

    @Test
    public void shouldAcceptLogoutResponseWithNoActionOnSuccess() {
        MockWebContext webContext = getMockWebContext();
        SAML2MessageContext context = getSaml2MessageContext(webContext);
        SAML2LogoutValidator validator = getLogoutValidator("");
        validator.setActionOnSuccess(false);

        SAML2LogoutMessageReceiver unit = new SAML2LogoutMessageReceiver(validator);
        try {
            unit.receiveMessage(context);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private SAML2LogoutValidator getLogoutValidator(final String postLogoutUrl) {
        SAML2Configuration config = new SAML2Configuration();
        config.setForceKeystoreGeneration(true);
        config.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        config.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        config.setForceServiceProviderMetadataGeneration(true);
        config.setKeystorePath("target/keystore.jks");
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.init();

        SAML2IdentityProviderMetadataResolver idp = new SAML2IdentityProviderMetadataResolver(config);
        idp.init();

        SAML2ServiceProviderMetadataResolver sp = new SAML2ServiceProviderMetadataResolver(config);

        SAML2SignatureTrustEngineProvider engine = new ExplicitSignatureTrustEngineProvider(idp, sp);

        SAML2LogoutValidator validator = new SAML2LogoutValidator(
            engine,
            mock(Decrypter.class),
            mock(LogoutHandler.class),
            postLogoutUrl,
            mock(ReplayCacheProvider.class)
        );
        return validator;
    }

    private MockWebContext getMockWebContext() {
        MockWebContext webContext = MockWebContext.create();
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
        SAML2MessageContext context = new SAML2MessageContext();

        EntityDescriptor entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);
        context.setWebContext(webContext);

        SPSSODescriptor spDescriptor = new SPSSODescriptorBuilder().buildObject();
        SingleLogoutService logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        return context;
    }
}
