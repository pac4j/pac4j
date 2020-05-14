package org.pac4j.saml.logout.impl;

import org.junit.Test;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SAML2LogoutMessageReceiverTest {

    @Test
    public void shouldAcceptLogoutResponse() {
        MockWebContext webContext = MockWebContext.create();
        webContext.setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        webContext.setRequestContent(
            "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
                "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
                "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
                "IssueInstant=\"2014-07-18T01:13:06Z\" Destination=\"http://sp.example.com/demo1/index.php?acs\" " +
                "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">\n" +
                "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>\n" +
                "  <samlp:Status>\n" +
                "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>\n" +
                "  </samlp:Status>\n" +
                "</samlp:LogoutResponse>");


        AssertionConsumerService acs = mock(AssertionConsumerService.class);

        SPSSODescriptor acsDescriptor = new SPSSODescriptorBuilder().buildObject();
        acsDescriptor.getAssertionConsumerServices().add(acs);

        SAML2MessageContext context = new SAML2MessageContext();

        SAMLMetadataContext metadataContext = context
            .getSAMLSelfEntityContext()
            .getSubcontext(SAMLMetadataContext.class, true);
        metadataContext.setRoleDescriptor(acsDescriptor);

        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityID()).thenReturn("idp.example.com");

        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);
        context.setWebContext(webContext);

        SAML2ResponseValidator validator = mock(SAML2ResponseValidator.class);
        when(validator.validate(any())).thenReturn(new MockCredentials());

        SAML2LogoutMessageReceiver unit = new SAML2LogoutMessageReceiver(validator);
        try {
            unit.receiveMessage(context);
            assertTrue("SAML2LogoutMessageReceiver processed the logout message successfully", true);
        } catch (SAMLException e) {
            e.printStackTrace();
            fail("Should not have thrown a SAML Exception");
        }

    }

}
