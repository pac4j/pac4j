package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;

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
        final OAuth10Client client = new YahooClient();
        assertEquals("YahooClient", client.getName());
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
    public void testGetCredentialError() throws HttpAction {
        final OAuth20Client client = new GitHubClient();
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

    private OAuth20Client getClient() {
        final FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
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
}
