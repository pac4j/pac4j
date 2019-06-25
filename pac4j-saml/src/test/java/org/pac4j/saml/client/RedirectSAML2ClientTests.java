package org.pac4j.saml.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static org.junit.Assert.*;

/**
 * Redirection tests on the {@link SAML2Client}.
 */
public final class RedirectSAML2ClientTests extends AbstractSAML2ClientTests {

    public RedirectSAML2ClientTests() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        client.getConfiguration().setUseNameQualifier(true);

        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();
        final String inflated = getInflatedAuthnRequest(action.getLocation());

        // JDK8 and JDK11 do not produce the same XML (attributes in different order)
        // something like xmlunit would have been better but may be a bit overkill for just 2 failing tests
        final String issuerJdk8 = "<saml2:Issuer "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/callback\" "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>";
        final String issuerJdk11 = "<saml2:Issuer "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/callback\">http://localhost:8080/callback</saml2:Issuer>";
        assertTrue(inflated.contains(issuerJdk8) || inflated.contains(issuerJdk11));
    }

    @Test
    public void testStandardSpEntityIdForRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");

        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();
        final String inflated = getInflatedAuthnRequest(action.getLocation());

        assertTrue(inflated.contains(
                "<saml2:Issuer "
                        + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                        + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">http://localhost:8080/callback</saml2:Issuer>"));
    }

    @Test
    public void testForceAuthIsSetForRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithRedirectBinding() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testNameIdPolicyFormat() {
        final SAML2Client client = getClient();
        client.getConfiguration().setNameIdPolicyFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();
        final String loc = action.getLocation();
        assertTrue(getInflatedAuthnRequest(loc).contains("<saml2p:NameIDPolicy AllowCreate=\"true\" " +
                "Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\"/></saml2p:AuthnRequest>"));
    }

    @Test
    public void testAuthnContextClassRef() {
        final SAML2Client client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        client.getConfiguration()
            .setAuthnContextClassRefs(Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"));
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final FoundAction action = (FoundAction) client.redirect(context).get();

        final String checkClass = "<saml2p:RequestedAuthnContext Comparison=\"exact\"><saml2:AuthnContextClassRef " +
                "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>" +
                "</saml2p:RequestedAuthnContext>";

        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains(checkClass));
    }

    @Test
    public void testRelayState() {
        final SAML2Client client = getClient();
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        context.getSessionStore().set(context, SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final FoundAction action = (FoundAction) client.redirect(context).get();
        assertTrue(action.getLocation().contains("RelayState=relayState"));
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getAuthnRequestBindingType() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }

    private String getInflatedAuthnRequest(final String location) {
        final List<NameValuePair> pairs = URLEncodedUtils.parse(java.net.URI.create(location), "UTF-8");
        final Inflater inflater = new Inflater(true);
        final byte[] decodedRequest = Base64.getDecoder().decode(pairs.get(0).getValue());
        final ByteArrayInputStream is = new ByteArrayInputStream(decodedRequest);
        final InflaterInputStream inputStream = new InflaterInputStream(is, inflater);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        final StringBuilder bldr = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                bldr.append(line);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return bldr.toString();
    }
}
