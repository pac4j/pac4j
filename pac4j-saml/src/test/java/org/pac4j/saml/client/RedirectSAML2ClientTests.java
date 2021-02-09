package org.pac4j.saml.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
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
        final var client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");
        client.getConfiguration().setUseNameQualifier(true);

        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        final var inflated = getInflatedAuthnRequest(action.getLocation());

        final var issuerJdk11 = "<saml2:Issuer "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/callback\">http://localhost:8080/callback</saml2:Issuer>";
        assertTrue(inflated.contains(issuerJdk11));
    }

    @Test
    public void testStandardSpEntityIdForRedirectBinding() {
        final var client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/callback");

        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        final var inflated = getInflatedAuthnRequest(action.getLocation());

        final var issuerJdk11 = "<saml2:Issuer "
            + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
            + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://localhost:8080/callback</saml2:Issuer>";
        assertTrue(inflated.contains(issuerJdk11));
    }

    @Test
    public void testForceAuthIsSetForRedirectBinding() {
        final var client = getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithRedirectBinding() {
        final var client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testNameIdPolicyFormat() {
        final var client = getClient();
        client.getConfiguration().setNameIdPolicyFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        final var loc = action.getLocation();
        assertTrue(getInflatedAuthnRequest(loc).contains("<saml2p:NameIDPolicy AllowCreate=\"true\" " +
                "Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\"/></saml2p:AuthnRequest>"));
    }

    @Test
    public void testAuthnContextClassRef() {
        final var client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        client.getConfiguration()
            .setAuthnContextClassRefs(Arrays.asList("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport"));
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();

        final var checkClass = "<saml2p:RequestedAuthnContext Comparison=\"exact\"><saml2:AuthnContextClassRef " +
                "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef>" +
                "</saml2p:RequestedAuthnContext>";

        assertTrue(getInflatedAuthnRequest(action.getLocation()).contains(checkClass));
    }

    @Test
    public void testRelayState() {
        final var client = getClient();
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.set(context, SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final var action = (FoundAction) client.getRedirectionAction(context, sessionStore).get();
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
        final var pairs = URLEncodedUtils.parse(java.net.URI.create(location), StandardCharsets.UTF_8);
        final var inflater = new Inflater(true);
        final var decodedRequest = Base64.getDecoder().decode(pairs.get(0).getValue());
        final var is = new ByteArrayInputStream(decodedRequest);
        final var inputStream = new InflaterInputStream(is, inflater);
        final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        final var bldr = new StringBuilder();
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
