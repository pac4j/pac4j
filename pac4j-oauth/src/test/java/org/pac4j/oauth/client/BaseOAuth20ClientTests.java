package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuth20Credentials;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the OAuth credential retrieval in the {@link org.pac4j.oauth.client.BaseOAuth20Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuth20ClientTests implements TestsConstants {

    private BaseOAuth20Client getClient() {
        final GitHubClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testNoCode() throws HttpAction {
        try {
            getClient().getCredentials(MockWebContext.create());
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testOk() throws HttpAction {
        final OAuth20Credentials oauthCredential = (OAuth20Credentials) getClient()
                .getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE, CODE));
        assertNotNull(oauthCredential);
        assertEquals(CODE, oauthCredential.getCode());
    }

    @Test
    public void testState() throws MalformedURLException, HttpAction {
        BaseOAuth20StateClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setStateData("OK");
        URL url = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        assertTrue(url.getQuery().contains("state=OK"));
    }

    @Test
    public void testStateMatch() throws MalformedURLException, HttpAction, UnsupportedEncodingException {
        BaseOAuth20StateClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext mockWebContext = MockWebContext.create();
        URL url = new URL(client.getRedirectAction(mockWebContext).getLocation());
        final Map<String, String> stringMap = splitQuery(url);
        assertNotNull(stringMap.get("state"));
        try {
            client.getCredentials(MockWebContext.create());
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Missing state parameter"));
        }
        mockWebContext.addRequestParameter("state", stringMap.get("state"));
        mockWebContext.addRequestParameter("code", "mockcode");
        client.getCredentials(mockWebContext);
    }

    @Test
    public void testSetState() throws MalformedURLException, HttpAction, UnsupportedEncodingException {
        BaseOAuth20StateClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setStateData("oldstate");
        final MockWebContext mockWebContext = MockWebContext.create();
        URL url = new URL(client.getRedirectAction(mockWebContext).getLocation());
        final Map<String, String> stringMap = splitQuery(url);
        assertEquals(stringMap.get("state"), "oldstate");
        URL url2 = new URL(client.getRedirectAction(mockWebContext).getLocation());
        final Map<String, String> stringMap2 = splitQuery(url2);
        assertEquals(stringMap2.get("state"), "oldstate");
    }

    @Test
    public void testStateRandom() throws MalformedURLException, HttpAction, UnsupportedEncodingException {
        BaseOAuth20StateClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        URL url = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        final Map<String, String> stringMap = splitQuery(url);
        assertNotNull(stringMap.get("state"));

        URL url2 = new URL(client.getRedirectAction(MockWebContext.create()).getLocation());
        final Map<String, String> stringMap2 = splitQuery(url2);
        assertNotNull(stringMap2.get("state"));
        assertNotEquals(stringMap.get("state"), stringMap2.get("state"));
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Test
    public void testGetRedirectionGithub() throws HttpAction {
        String url = getClient().getRedirectAction(MockWebContext.create()).getLocation();
        assertTrue(url != null && !url.isEmpty());
    }
}
