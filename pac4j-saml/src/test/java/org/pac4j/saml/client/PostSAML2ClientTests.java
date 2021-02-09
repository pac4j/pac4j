package org.pac4j.saml.client;

import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.metadata.SAML2MetadataContactPerson;
import org.pac4j.saml.metadata.SAML2MetadataUIInfo;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

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
        final var client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        client.getConfiguration().setUseNameQualifier(true);

        final var person = new SAML2MetadataContactPerson();
        person.setCompanyName("Pac4j");
        person.setGivenName("Bob");
        person.setSurname("Smith");
        person.setType("technical");
        person.setEmailAddresses(Collections.singletonList("test@example.org"));
        person.setTelephoneNumbers(Collections.singletonList("+13476547689"));
        client.getConfiguration().getContactPersons().add(person);

        final var uiInfo = new SAML2MetadataUIInfo();
        uiInfo.setDescriptions(Collections.singletonList("description1"));
        uiInfo.setDisplayNames(Collections.singletonList("displayName"));
        uiInfo.setPrivacyUrls(Collections.singletonList("https://pac4j.org"));
        uiInfo.setInformationUrls(Collections.singletonList("https://pac4j.org"));
        uiInfo.setKeywords(Collections.singletonList("keyword1,keyword2,keyword3"));
        uiInfo.setLogos(Collections.singletonList(new SAML2MetadataUIInfo.SAML2MetadataUILogo("https://pac4j.org/logo.png", 16, 16)));
        client.getConfiguration().getMetadataUIInfos().add(uiInfo);

        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (OkAction) client.getRedirectionAction(context, new MockSessionStore()).get();

        final var issuerJdk11 = "<saml2:Issuer "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/cb\">http://localhost:8080/cb</saml2:Issuer>";
        final var decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testStandardSpEntityIdForPostBinding() {
        final var client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (OkAction) client.getRedirectionAction(context, new MockSessionStore()).get();

        final var issuerJdk11 = "<saml2:Issuer "
            + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
            + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://localhost:8080/cb</saml2:Issuer>";
        final var decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() {
        final var client =  getClient();
        client.getConfiguration().setForceAuth(true);
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (OkAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() {
        final var client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final var action = (OkAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() {
        final var client = getClient();
        final WebContext context = new JEEContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.set(context, SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        final var action = (OkAction) client.getRedirectionAction(context, sessionStore).get();
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
        final var samlRequestField = CommonHelper.substringBetween(content, "SAMLRequest", "</div");
        final var value = CommonHelper.substringBetween(samlRequestField, "value=\"", "\"");
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
