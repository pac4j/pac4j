package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link BaseOAuthClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuthClientTests implements TestsConstants {

    @Test
    public void testDefaultName10() {
        final BaseOAuth10Client client = new YahooClient();
        assertEquals("YahooClient", client.getName());
    }

    @Test
    public void testDefaultName20() {
        final BaseOAuth20Client client = new FacebookClient();
        assertEquals("FacebookClient", client.getName());
    }

    @Test
    public void testDefinedName() {
        final BaseOAuth20Client client = new FacebookClient();
        client.setName(TYPE);
        assertEquals(TYPE, client.getName());
    }

    @Test
    public void testGetCredentialOK() throws HttpAction {
        final BaseOAuthClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        assertTrue(client.getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE,
                                                                                     FAKE_VALUE)) instanceof OAuthCredentials);
    }

    @Test
    public void testGetCredentialError() throws HttpAction {
        final BaseOAuthClient client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE,
                                                                                   FAKE_VALUE);
        for (final String key : OAuthCredentialsException.ERROR_NAMES) {
            context.addRequestParameter(key, FAKE_VALUE);
        }
        try {
            client.getCredentials(context);
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("Failed to retrieve OAuth credentials, error parameters found", e.getMessage());
        }
    }

    private BaseOAuthClient getClient() {
        final FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testMissingKey() {
        final BaseOAuthClient client = (BaseOAuthClient) getClient();
        client.setKey(null);
        TestsHelper.initShouldFail(client, "key cannot be blank");
    }

    @Test
    public void testMissingSecret() {
        final BaseOAuthClient client = (BaseOAuthClient) getClient();
        client.setSecret(null);
        TestsHelper.initShouldFail(client, "secret cannot be blank");
    }
}
