package org.pac4j.saml.logout.impl;

import lombok.val;
import org.junit.Test;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
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
        var sp = new SAML2ServiceProviderMetadataResolver(config);
        return new ExplicitSignatureTrustEngineProvider(idp, sp);
    }

    private static SAML2Configuration getSaml2Configuration() {
        val config = new SAML2Configuration();
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
        val context = new SAML2MessageContext(new CallContext(webContext, new MockSessionStore()));
        context.setSaml2Configuration(getSaml2Configuration());

        val samlMessage = new MessageContext();
        val samlResponse = (LogoutResponse) Configuration.deserializeSamlObject(xml).get();

        samlMessage.setMessage(samlResponse);
        context.setMessageContext(samlMessage);
        val entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);

        val spDescriptor = new SPSSODescriptorBuilder().buildObject();
        val logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        return context;
    }

    @Test
    public void verifyHostComparison() {
        val xml = "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
            "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
            "IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC)
            + "\" Destination=\"http://sp.example.com/demo1/logout?x=1000%26y=1234\" " +
            "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">" +
            "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>" +
            "  <samlp:Status>" +
            "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>" +
            "  </samlp:Status>" +
            "</samlp:LogoutResponse>";

        try {
            val webContext = getMockWebContext();
            val context = getSaml2MessageContext(webContext, xml);
            SAML2ResponseValidator validator = new SAML2LogoutValidator(
                getTrustEngine(),
                mock(Decrypter.class),
                mock(SessionLogoutHandler.class),
                mock(ReplayCacheProvider.class),
                new ExcludingParametersURIComparator()
            );
            validator.validate(context);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void verifyThatPartialLogoutAsSecondLevelStatusCodeIsAcceptedAsSuccess() throws Exception {

        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" "+
            "Destination=\"http://sp.example.com/demo1/logout\" " +
            "ID=\"_0a59a9e8-1885-4127-84c8-515354c7e29d\" InResponseTo=\"_1981f72063034b65b659cf5dc484e2f01698e96\" " +
            "IssueInstant=\""+ZonedDateTime.now(ZoneOffset.UTC)+"\" Version=\"2.0\">\n" +
            "    <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">urn:some:redacted:issuer</saml:Issuer>\n" +
            "    <samlp:Status>\n" +
            "        <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Responder\">\n" +
            "            <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:PartialLogout\"/>\n" +
            "        </samlp:StatusCode>\n" +
            "    </samlp:Status>\n" +
            "</samlp:LogoutResponse>";

        validateResponse(xml);
    }

    @Test
    public void verifyThatPartialLogoutAsTopLevelStatusCodeIsAcceptedAsSuccess() throws Exception {

        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" "+
            "Destination=\"http://sp.example.com/demo1/logout\" " +
            "ID=\"_0a59a9e8-1885-4127-84c8-515354c7e29d\" InResponseTo=\"_1981f72063034b65b659cf5dc484e2f01698e96\" " +
            "IssueInstant=\""+ZonedDateTime.now(ZoneOffset.UTC)+"\" Version=\"2.0\">\n" +
            "    <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">urn:some:redacted:issuer</saml:Issuer>\n" +
            "    <samlp:Status>\n" +
            "        <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:PartialLogout\"/>\n" +
            "    </samlp:Status>\n" +
            "</samlp:LogoutResponse>";

        validateResponse(xml);
    }

    private void validateResponse(String xml) {
        val webContext = getMockWebContext();
        val context = getSaml2MessageContext(webContext, xml);
        val validator = new SAML2LogoutValidator(
            getTrustEngine(),
            mock(Decrypter.class),
            mock(SessionLogoutHandler.class),
            mock(ReplayCacheProvider.class),
            new ExcludingParametersURIComparator()
        );

        try {
            validator.validate(context);
            fail("Validation should have failed, because response status PartialLogout is not accepted as success by default.");
        }
        catch(SAMLException expectedException) {
            assertNotNull(expectedException);
        }

        validator.setPartialLogoutTreatedAsSuccess(true);
        validator.validate(context);
    }
}
