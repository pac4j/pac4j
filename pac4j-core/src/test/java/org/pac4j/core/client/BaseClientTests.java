package org.pac4j.core.client;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This class tests the {@link BaseClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class BaseClientTests implements TestsConstants {

    @Test
    public void testDirectClient() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        final var action = (FoundAction) client.getRedirectionAction(context, sessionStore).get();
        final var redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        final var credentials = client.getCredentials(context, sessionStore);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testIndirectClientWithImmediate() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final var action = (FoundAction) client.getRedirectionAction(context, new MockSessionStore()).get();
        final var redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertFalse(client.getUserProfile(null, context, null).isPresent());
    }

    @Test
    public void testNullCredentialsButForceAnonymous() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setProfileFactoryWhenNotAuthenticated(p -> AnonymousProfile.INSTANCE);
        client.setProfileFactoryWhenNotAuthenticated(p -> AnonymousProfile.INSTANCE);
        final var context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertEquals(AnonymousProfile.INSTANCE, client.getUserProfile(null, context, null).get());
    }

    @Test
    public void testAjaxRequest() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create()
                                        .addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        final var e = (HttpAction) TestsHelper.expectException(() -> client.getRedirectionAction(context, new MockSessionStore()));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testAlreadyTried() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.set(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        final var e = (HttpAction) TestsHelper.expectException(() -> client.getRedirectionAction(context, sessionStore));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testSaveAlreadyTried() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        client.getCredentials(context, sessionStore);
        assertEquals("true", sessionStore.get(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX).get());
    }

    @Test
    public void testStateParameter() {
        final var client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var context = MockWebContext.create();
        TestsHelper.expectException(() -> client.getRedirectionAction(context, null));
    }
}
