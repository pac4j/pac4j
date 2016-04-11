package org.pac4j.oauth.client;

import com.github.scribejava.core.model.Token;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuthCredentials;

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
    public void testNoTokenNoVerifier() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create());
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testNoToken() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create().addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER,
                                                                                   VERIFIER));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testNoVerifier() throws RequiresHttpAction {
        try {
            getClient().getCredentials(MockWebContext.create()
                                           .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN));
            fail("should not get credentials");
        } catch (final TechnicalException e) {
            assertEquals("No credential found", e.getMessage());
        }
    }

    @Test
    public void testOk() throws RequiresHttpAction {
        final OAuthCredentials credentials = (OAuthCredentials) getClient()
            .getCredentials(MockWebContext
                                .create()
                                .addRequestParameter(BaseOAuth10Client.OAUTH_VERIFIER, VERIFIER)
                                .addRequestParameter(BaseOAuth10Client.OAUTH_TOKEN, TOKEN)
                                .addSessionAttribute(getClient().getName() + "#" + BaseOAuth10Client.REQUEST_TOKEN,
                                                     new Token(TOKEN, SECRET)));
        assertNotNull(credentials);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        final Token tokenRequest = credentials.getRequestToken();
        assertEquals(TOKEN, tokenRequest.getToken());
        assertEquals(SECRET, tokenRequest.getSecret());
    }
}
