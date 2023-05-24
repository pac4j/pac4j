package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link DirectBearerAuthClient} class.
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
public final class DirectBearerAuthClientTests implements TestsConstants {

    @Test
    public void testMissingTokenAuthenticator() {
        val bearerAuthClient = new DirectBearerAuthClient((Authenticator) null);
        TestsHelper.expectException(() -> bearerAuthClient.getCredentials(new CallContext(MockWebContext.create(),
                new MockSessionStore())), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val bearerAuthClient = new DirectBearerAuthClient((ProfileCreator) null);
        TestsHelper.expectException(() -> bearerAuthClient.getCredentials(new CallContext(MockWebContext.create(),
                new MockSessionStore())), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testMissingProfileCreator2() {
        val bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> bearerAuthClient.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new TokenCredentials(TOKEN)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val bearerAuthClient = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        bearerAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        val client = new DirectBearerAuthClient(new SimpleTestTokenAuthenticator());
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                HttpConstants.BEARER_HEADER_PREFIX + TOKEN);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = (TokenCredentials) client.getCredentials(ctx).get();
        assertEquals(TOKEN, credentials.getToken());
        val authnCredentials = client.validateCredentials(ctx, credentials).get();
        UserProfile profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(TOKEN, profile.getId());
    }

    @Test
    public void testProfileCreation() {
        val client = new DirectBearerAuthClient(new ProfileCreator() {
            @Override
            public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
                UserProfile profile = new CommonProfile();
                profile.setId(KEY);
                return Optional.of(profile);
            }
        });
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
            HttpConstants.BEARER_HEADER_PREFIX + TOKEN);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = (TokenCredentials) client.getCredentials(ctx).get();
        assertEquals(TOKEN, credentials.getToken());
        val authnCredentials = client.validateCredentials(ctx, credentials).get();
        UserProfile profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(KEY, profile.getId());
    }
}
