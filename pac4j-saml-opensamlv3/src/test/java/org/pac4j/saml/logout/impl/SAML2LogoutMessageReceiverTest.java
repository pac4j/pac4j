package org.pac4j.saml.logout.impl;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
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
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SAML2LogoutMessageReceiverTest {

    @Test
    public void shouldAcceptLogoutResponse() {
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

        SAML2MessageContext context = new SAML2MessageContext();

        EntityDescriptor entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);
        context.setWebContext(webContext);

        SPSSODescriptor spDescriptor = new SPSSODescriptorBuilder().buildObject();
        SingleLogoutService logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);

        ChainingMetadataResolver metadataResolver = new ChainingMetadataResolver();
        SAML2SignatureTrustEngineProvider engine = new ExplicitSignatureTrustEngineProvider(metadataResolver);

        SAML2ResponseValidator validator = new SAML2LogoutValidator(
            engine,
            mock(Decrypter.class),
            mock(LogoutHandler.class),
            "/logoutUrl",
            mock(ReplayCacheProvider.class)
        );

        SAML2LogoutMessageReceiver unit = new SAML2LogoutMessageReceiver(validator);
        try {
            unit.receiveMessage(context);
            fail("Should have thrown a FoundAction");
        } catch (SAMLException e) {
            e.printStackTrace();
            fail("Should not have thrown a SAML Exception");
        } catch (FoundAction e) {
            assertTrue("SAML2LogoutMessageReceiver processed the logout message successfully", true);
            MatcherAssert.assertThat(e.getLocation(), is("/logoutUrl"));
        }
    }
}
