package org.pac4j.saml.logout.impl;

import org.junit.Test;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.ExcludingParametersURIComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * This is {@link SAML2LogoutValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SAML2LogoutValidatorTests {
    private static ExplicitSignatureTrustEngineProvider getTrustEngine() {
        var config = getSaml2Configuration();

        var idp = new SAML2IdentityProviderMetadataResolver(config);
        idp.init();
        var sp = new SAML2ServiceProviderMetadataResolver(config);
        return new ExplicitSignatureTrustEngineProvider(idp, sp);
    }

    private static SAML2Configuration getSaml2Configuration() {
        final var config = new SAML2Configuration();
        config.setForceKeystoreGeneration(true);
        config.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        config.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        config.setForceServiceProviderMetadataGeneration(true);
        config.setKeystorePath("target/keystore.jks");
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.init();
        return config;
    }

    private static MockWebContext getMockWebContext() {
        return MockWebContext.create();
    }

    private static SAML2MessageContext getSaml2MessageContext(final MockWebContext webContext, final String xml) {
        final var context = new SAML2MessageContext();
        context.setSaml2Configuration(getSaml2Configuration());

        final var samlMessage = new MessageContext();
        final var samlResponse = (LogoutResponse) Configuration.deserializeSamlObject(xml).get();

        samlMessage.setMessage(samlResponse);
        context.setMessageContext(samlMessage);
        final var entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);
        context.setWebContext(webContext);
        context.setSessionStore(new MockSessionStore());

        final var spDescriptor = new SPSSODescriptorBuilder().buildObject();
        final var logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        return context;
    }

    @Test
    public void verifyHostComparison() {

        final var xml = "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
            "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
            "IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC)
            + "\" Destination=\"http://sp.example.com/demo1/logout?x=1000%26y=1234\" " +
            "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">%n" +
            "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>%n" +
            "  <samlp:Status>%n" +
            "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>%n" +
            "  </samlp:Status>%n" +
            "</samlp:LogoutResponse>";

        try {
            final var webContext = getMockWebContext();
            final var context = getSaml2MessageContext(webContext, xml);
            final var validator = new SAML2LogoutValidator(
                getTrustEngine(),
                mock(Decrypter.class),
                mock(LogoutHandler.class),
                null,
                mock(ReplayCacheProvider.class),
                new ExcludingParametersURIComparator()
            );
            validator.setActionOnSuccess(false);
            validator.validate(context);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void verifyThatPartialLogoutIsAcceptedAsSuccess() throws Exception {

        final var xml = "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
            "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
            "IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC)
            + "\" Destination=\"http://sp.example.com/demo1/logout?x=1000%26y=1234\" " +
            "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">%n" +
            "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>%n" +
            "  <samlp:Status>%n" +
            "    <samlp:StatusCode Value="+
            "\"urn:oasis:names:tc:SAML:2.0:status:Responder / urn:oasis:names:tc:SAML:2.0:status:PartialLogout\"/>%n" +
            "  </samlp:Status>%n" +
            "</samlp:LogoutResponse>";

        final var webContext = getMockWebContext();
        final var context = getSaml2MessageContext(webContext, xml);
        final var validator = new SAML2LogoutValidator(
            getTrustEngine(),
            mock(Decrypter.class),
            mock(LogoutHandler.class),
            null,
            mock(ReplayCacheProvider.class),
            new ExcludingParametersURIComparator()
        );
        validator.setActionOnSuccess(false);

        try {
            validator.validate(context);
            fail("Validation should have failed, because response status PartialLogout is not accepted as success by default.");
        }
        catch(SAMLException expectedException) {
            assertNotNull(expectedException);
        }

        validator.setIsPartialLogoutTreatedAsSuccess(true);
        validator.validate(context);
    }
}
