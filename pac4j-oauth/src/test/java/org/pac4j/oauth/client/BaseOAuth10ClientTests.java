package org.pac4j.oauth.client;

import com.github.scribejava.core.model.OAuth1RequestToken;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuth10Credentials;

import static org.junit.Assert.*;

/**
 * This class tests the OAuth credential retrieval in the {@link org.pac4j.oauth.client.BaseOAuth10Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuth10ClientTests implements TestsConstants {

    private BaseOAuth10Client getClient() {
        final YahooClient client = new YahooClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testNoTokenNoVerifier() throws HttpAction {
        try {
            getClient().getCredentials(MockWebContext.create());
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testNoToken() throws HttpAction {
        try {
            getClient().getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER,
                    VERIFIER));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testNoVerifier() throws HttpAction {
        try {
            getClient().getCredentials(MockWebContext.create()
                    .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testOk() throws HttpAction {
        final OAuth10Credentials credentials = (OAuth10Credentials) getClient()
                .getCredentials(MockWebContext
                        .create()
                        .addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER, VERIFIER)
                        .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN)
                        .addSessionAttribute(getClient().getName() + "#" + BaseOAuth10Client.REQUEST_TOKEN,
                                new OAuth1RequestToken(TOKEN, SECRET)));
        assertNotNull(credentials);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        final OAuth1RequestToken tokenRequest = (OAuth1RequestToken) credentials.getRequestToken();
        assertEquals(TOKEN, tokenRequest.getToken());
        assertEquals(SECRET, tokenRequest.getTokenSecret());
    }
}
