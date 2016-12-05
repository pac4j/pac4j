package org.pac4j.saml.client;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * POST tests on the {@link SAML2Client}.
 */
public final class PostSAML2ClientTests extends AbstractSAML2ClientTests {

    public PostSAML2ClientTests() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForPostBinding() throws Exception {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context);
        assertTrue(getDecodedAuthnRequest(action.getContent())
                .contains(
                        "<saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>"));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() throws Exception {
        final SAML2Client client =  getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() throws Exception {
        final SAML2Client client =  getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final RedirectAction action = client.getRedirectAction(context);
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() throws HttpAction {
        final SAML2Client client = getClient();
        final WebContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.setSessionAttribute(SAML2Client.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final RedirectAction action = client.getRedirectAction(context);
        assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getDestinationBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    private String getDecodedAuthnRequest(final String content) throws Exception {
        assertTrue(content.contains("<form"));
        final String samlRequestField = StringUtils.substringBetween(content, "SAMLRequest", "</div");
        final String value = StringUtils.substringBetween(samlRequestField, "value=\"", "\"");
        return new String(Base64.getDecoder().decode(value), HttpConstants.UTF8_ENCODING);
    }
}
