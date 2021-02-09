package org.pac4j.http.client.direct;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

/**
 * This class tests the {@link DirectBearerAuthClient} class.
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
public final class DirectBearerAuthClientTests implements TestsConstants {

    @Test
    public void testMissingTokenAuthenticator() {
        final var bearerAuthClient = new DirectBearerAuthClient(null);
        TestsHelper.expectException(() -> bearerAuthClient.getCredentials(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final var bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> bearerAuthClient.getUserProfile(new TokenCredentials(TOKEN),
            MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final var bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        bearerAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        final var client = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        final var context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                HttpConstants.BEARER_HEADER_PREFIX + TOKEN);
        final var credentials = (TokenCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (CommonProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals(TOKEN, profile.getId());
    }
}
