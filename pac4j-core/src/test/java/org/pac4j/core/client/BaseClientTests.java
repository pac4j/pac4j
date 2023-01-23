package org.pac4j.core.client;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests the {@link BaseClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class BaseClientTests implements TestsConstants {

    @Test
    public void testDirectClient() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        val action = (FoundAction) client.getRedirectionAction(new CallContext(context, sessionStore)).get();
        val redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        val credentials = client.getCredentials(new CallContext(context, sessionStore));
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testIndirectClientWithImmediate() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        val action = (FoundAction) client.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        val redirectionUrl = action.getLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    @Test
    public void testNullCredentials() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertFalse(client.getUserProfile(new CallContext(context, null), null).isPresent());
    }

    @Test
    public void testNullCredentialsButForceAnonymous() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setProfileFactoryWhenNotAuthenticated(p -> AnonymousProfile.INSTANCE);
        client.setProfileFactoryWhenNotAuthenticated(p -> AnonymousProfile.INSTANCE);
        val context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertEquals(AnonymousProfile.INSTANCE, client.getUserProfile(new CallContext(context, null), null).get());
    }

    @Test
    public void testAjaxRequest() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create()
                                        .addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        val e = (HttpAction) TestsHelper.expectException(() ->
            client.getRedirectionAction(new CallContext(context, new MockSessionStore())));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testAlreadyTried() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.set(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        val e = (HttpAction) TestsHelper.expectException(() -> client.getRedirectionAction(new CallContext(context, sessionStore)));
        assertEquals(401, e.getCode());
    }

    @Test
    public void testSaveAlreadyTried() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        client.getCredentials(new CallContext(context, sessionStore));
        assertEquals("true", sessionStore.get(context, client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX).get());
    }

    @Test
    public void testStateParameter() {
        val client =
            new MockIndirectClient(TYPE, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val context = MockWebContext.create();
        TestsHelper.expectException(() -> client.getRedirectionAction(new CallContext(context, null)));
    }
}
