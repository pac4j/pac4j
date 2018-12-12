package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.state.StaticOrRandomStateGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the {@link OAuth20Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuth20ClientTests implements TestsConstants {

    private OAuth20Client getClient() {
        final GitHubClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testState() throws MalformedURLException {
        FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        ((StaticOrRandomStateGenerator) client.getConfiguration().getStateGenerator()).setStateData("OK");
        URL url = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        assertTrue(url.getQuery().contains("state=OK"));
    }

    @Test
    public void testSetState() throws MalformedURLException {
        FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        ((StaticOrRandomStateGenerator) client.getConfiguration().getStateGenerator()).setStateData("oldstate");
        final MockWebContext mockWebContext = MockWebContext.create();
        URL url = new URL(client.getRedirectAction(mockWebContext).getLocation());
        final Map<String, String> stringMap = splitQuery(url);
        assertEquals(stringMap.get("state"), "oldstate");
        URL url2 = new URL(client.getRedirectAction(mockWebContext).getLocation());
        final Map<String, String> stringMap2 = splitQuery(url2);
        assertEquals(stringMap2.get("state"), "oldstate");
    }

    @Test
    public void testStateRandom() throws MalformedURLException {
        OAuth20Client client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        URL url = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        final Map<String, String> stringMap = splitQuery(url);
        assertNotNull(stringMap.get("state"));

        URL url2 = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        final Map<String, String> stringMap2 = splitQuery(url2);
        assertNotNull(stringMap2.get("state"));
        assertNotEquals(stringMap.get("state"), stringMap2.get("state"));
    }

    public static Map<String, String> splitQuery(URL url) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&", -1);
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(CommonHelper.urlEncode(pair.substring(0, idx)), CommonHelper.urlEncode(pair.substring(idx + 1)));
        }
        return query_pairs;
    }

    @Test
    public void testGetRedirectionGithub() {
        String url = getClient().getRedirectAction(MockWebContext.create()).getLocation();
        assertTrue(url != null && !url.isEmpty());
    }

    @Test
    public void testDefaultName20() {
        final OAuth20Client client = new FacebookClient();
        assertEquals("FacebookClient", client.getName());
    }

    @Test
    public void testDefinedName() {
        final OAuth20Client client = new FacebookClient();
        client.setName(TYPE);
        assertEquals(TYPE, client.getName());
    }

    @Test
    public void testMissingKey() {
        final OAuth20Client client = getClient();
        client.setKey(null);
        TestsHelper.expectException(() -> client.redirect(MockWebContext.create()), TechnicalException.class, "key cannot be blank");
    }

    @Test
    public void testMissingSecret() {
        final OAuth20Client client = getClient();
        client.setSecret(null);
        TestsHelper.expectException(() -> client.redirect(MockWebContext.create()), TechnicalException.class, "secret cannot be blank");
    }

    @Test
    public void testMissingFieldsFacebook() {
        final FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    private Google2Client getGoogleClient() {
        final Google2Client google2Client = new Google2Client(KEY, SECRET);
        google2Client.setCallbackUrl(CALLBACK_URL);
        return google2Client;
    }

    @Test
    public void testMissingScopeGoogle() {
        final Google2Client client = getGoogleClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    @Test
    public void testDefaultScopeGoogle() {
        getGoogleClient().redirect(MockWebContext.create());
    }

    @Test
    public void testMissingFieldsOk() {
        final OkClient client = new OkClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "publicKey cannot be blank");
    }

    private LinkedIn2Client getLinkedInClient() {
        final LinkedIn2Client client = new LinkedIn2Client(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testMissingScopeLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }

    @Test
    public void testMissingFieldsLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    @Test
    public void testMissingFieldsPaypal() {
        final PayPalClient client = new PayPalClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
}
