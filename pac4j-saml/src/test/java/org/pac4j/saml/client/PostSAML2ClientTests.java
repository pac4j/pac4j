package org.pac4j.saml.client;

import lombok.val;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.metadata.SAML2MetadataContactPerson;
import org.pac4j.saml.metadata.SAML2MetadataUIInfo;
import org.pac4j.saml.state.SAML2StateGenerator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * POST tests on the {@link SAML2Client}.
 */
public final class PostSAML2ClientTests extends AbstractSAML2ClientTests {

    public PostSAML2ClientTests() {
        super();
    }

    @Test
    public void testCustomSpEntityIdForPostBinding() {
        val client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        client.getConfiguration().setUseNameQualifier(true);

        val person = new SAML2MetadataContactPerson();
        person.setCompanyName("Pac4j");
        person.setGivenName("Bob");
        person.setSurname("Smith");
        person.setType("technical");
        person.setEmailAddresses(Collections.singletonList("test@example.org"));
        person.setTelephoneNumbers(Collections.singletonList("+13476547689"));
        client.getConfiguration().getContactPersons().add(person);

        val uiInfo = new SAML2MetadataUIInfo();
        uiInfo.setDescriptions(Collections.singletonList("description1"));
        uiInfo.setDisplayNames(Collections.singletonList("displayName"));
        uiInfo.setPrivacyUrls(Collections.singletonList("https://pac4j.org"));
        uiInfo.setInformationUrls(Collections.singletonList("https://pac4j.org"));
        uiInfo.setKeywords(Collections.singletonList("keyword1,keyword2,keyword3"));
        uiInfo.setLogos(Collections.singletonList(new SAML2MetadataUIInfo.SAML2MetadataUILogo("https://pac4j.org/logo.png", 16, 16)));
        client.getConfiguration().getMetadataUIInfos().add(uiInfo);

        WithContentAction action = (OkAction) client.getRedirectionAction(
            new CallContext(MockWebContext.create(), new MockSessionStore())).get();

        val issuerJdk11 = "<saml2:Issuer "
                + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
                + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" "
                + "NameQualifier=\"http://localhost:8080/cb\">http://localhost:8080/cb</saml2:Issuer>";
        val decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testStandardSpEntityIdForPostBinding() {
        val client = getClient();
        client.getConfiguration().setServiceProviderEntityId("http://localhost:8080/cb");
        WithContentAction action = (OkAction) client.getRedirectionAction(
            new CallContext(MockWebContext.create(), new MockSessionStore())).get();

        val issuerJdk11 = "<saml2:Issuer "
            + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
            + "Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://localhost:8080/cb</saml2:Issuer>";
        val decodedAuthnRequest = getDecodedAuthnRequest(action.getContent());
        assertTrue(decodedAuthnRequest.contains(issuerJdk11));
    }

    @Test
    public void testForceAuthIsSetForPostBinding() {
        val client =  getClient();
        client.getConfiguration().setForceAuth(true);
        WithContentAction action = (OkAction) client.getRedirectionAction(
            new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("ForceAuthn=\"true\""));
    }

    @Test
    public void testSetComparisonTypeWithPostBinding() {
        val client = getClient();
        client.getConfiguration().setComparisonType(AuthnContextComparisonTypeEnumeration.EXACT.toString());
        WithContentAction action = (OkAction) client.getRedirectionAction(
            new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertTrue(getDecodedAuthnRequest(action.getContent()).contains("Comparison=\"exact\""));
    }

    @Test
    public void testRelayState() {
        val client = getClient();
        final WebContext context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.set(context, SAML2StateGenerator.SAML_RELAY_STATE_ATTRIBUTE, "relayState");
        WithContentAction action = (OkAction) client.getRedirectionAction(new CallContext(context, sessionStore)).get();
        assertTrue(action.getContent().contains("<input type=\"hidden\" name=\"RelayState\" value=\"relayState\"/>"));
    }

    @Test
    public void testPostDestinationBindingWithRequestSignedWorks() {
        val client = getClient();
        client.getConfiguration().setAuthnRequestBindingType(SAMLConstants.SAML2_POST_BINDING_URI);
        client.getConfiguration().setAuthnRequestSigned(true);

        WithContentAction action = (OkAction) client.getRedirectionAction(
            new CallContext(MockWebContext.create(), new MockSessionStore())).get();
        assertFalse(getDecodedAuthnRequest(action.getContent()).isBlank());
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
        val samlRequestField = CommonHelper.substringBetween(content, "SAMLRequest", "</div");
        val value = CommonHelper.substringBetween(samlRequestField, "value=\"", "\"");
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
