package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the OAuth credential retrieval in the {@link org.pac4j.oauth.client.BaseOAuth10Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class BaseOAuth10ClientTests implements TestsConstants {

    private OAuth10Client getClient() {
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
}
