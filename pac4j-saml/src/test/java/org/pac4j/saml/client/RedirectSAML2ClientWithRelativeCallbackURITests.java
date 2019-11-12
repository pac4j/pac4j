package org.pac4j.saml.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RedirectSAML2ClientWithRelativeCallbackURITests extends AbstractSAML2ClientTests {

    public static final String RELATIVE_CALLBACK_URI = "/callback?client_name=" + SAML2Client.class.getSimpleName();
    
    @Override
    protected String getCallbackUrl() {
        return RELATIVE_CALLBACK_URI;
    }

    @Override
    protected String getAuthnRequestBindingType() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }

    @Test
    public void testRedirectLocationIsAppropriatelyResolved() {
        final SAML2Client client = getClient();
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.getRedirectionAction(context).get();
        String authnRequest = AuthnRequestInflator.getInflatedAuthnRequest(action.getLocation());
        assertTrue(authnRequest.contains("AssertionConsumerServiceURL=\"http://localhost"+RELATIVE_CALLBACK_URI+"\""));
    }
}
