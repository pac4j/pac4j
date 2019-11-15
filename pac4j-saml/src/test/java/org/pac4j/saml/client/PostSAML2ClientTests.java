package org.pac4j.saml.client;

import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
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
    public void testCustomSpEntityIdForPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        client.getConfiguration().setUseNameQualifier(true);
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = (OkAction) client.getRedirectionAction(context).get();
        
        // JDK8 and JDK11 do not produce the same XML (attributes in different order)
        // something like xmlunit would have been better but may be a bit overkill for just 2 failing tests
        final String issuerJdk8 = "<saml2:Issuer "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/cb\" "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/cb</saml2:Issuer>";
        final String issuerJdk11 = "<saml2:Issuer "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/cb\">http://localhost:8080/cb</saml2:Issuer>";
        final String decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk8) || decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testStandardSpEntityIdForPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = (OkAction) client.getRedirectionAction(context).get();

        final String issuerJdk8 = "<saml2:Issuer "
            + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
            + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/cb</saml2:Issuer>";
        final String issuerJdk11 = "<saml2:Issuer "
            + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
            + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://localhost:8080/cb</saml2:Issuer>";
        final String decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk8) || decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() {
        final SAML2Client client =  getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = (OkAction) client.getRedirectionAction(context).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final OkAction action = (OkAction) client.getRedirectionAction(context).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() {
        final SAML2Client client = getClient();
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.getSessionStore().set(context, SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final OkAction action = (OkAction) client.getRedirectionAction(context).get();
        assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getAuthnRequestBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }

    private static String getDecodedAuthnRequest(final String content) {
        assertTrue(content.contains("<form"));
        final String samlRequestField = CommonHelper.substringBetween(content, "SAMLRequest", "</div");
        final String value = CommonHelper.substringBetween(samlRequestField, "value=\"", "\"");
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
