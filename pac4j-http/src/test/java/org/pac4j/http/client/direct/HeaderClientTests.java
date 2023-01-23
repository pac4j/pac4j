package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.AuthenticationCredentials;
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
 * This class tests the {@link HeaderClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class HeaderClientTests implements TestsConstants {

    @Test
    public void testMissingTokendAuthenticator() {
        val client = new HeaderClient(VALUE, (Authenticator) null);
        TestsHelper.expectException(() -> client.getCredentials(new CallContext(MockWebContext.create(), new MockSessionStore())),
                TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val client = new HeaderClient(NAME, new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.expectException(() -> client.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new TokenCredentials(TOKEN)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val client = new HeaderClient(HEADER_NAME, new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testMissingHeaderName() {
        val client = new HeaderClient(null, new SimpleTestTokenAuthenticator());
        TestsHelper.initShouldFail(client, "headerName cannot be blank");
    }

    @Test
    public void testAuthentication() {
        val client = new HeaderClient(HEADER_NAME, PREFIX_HEADER, new SimpleTestTokenAuthenticator());
        val context = MockWebContext.create();
        context.addRequestHeader(HEADER_NAME, PREFIX_HEADER + VALUE);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = (TokenCredentials) client.getCredentials(ctx).get();
        assertEquals(VALUE, credentials.getToken());
        val authnCredentials = (AuthenticationCredentials) client.validateCredentials(ctx, credentials).get();
        val profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(VALUE, profile.getId());
    }

    @Test
    public void testProfileCreation() {
        val client = new HeaderClient(HEADER_NAME, PREFIX_HEADER, new ProfileCreator() {
            @Override
            public Optional<UserProfile> create(final CallContext ctx, final AuthenticationCredentials credentials) {
                val profile = new CommonProfile();
                profile.setId(KEY);
                return Optional.of(profile);
            }
        });
        val context = MockWebContext.create();
        context.addRequestHeader(HEADER_NAME, PREFIX_HEADER + VALUE);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = (TokenCredentials) client.getCredentials(ctx).get();
        assertEquals(VALUE, credentials.getToken());
        val authnCredentials = (AuthenticationCredentials) client.validateCredentials(ctx, credentials).get();
        val profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(KEY, profile.getId());
    }
}
