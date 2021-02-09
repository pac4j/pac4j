package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link CookieClient} class.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CookieClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final var cookieClient = new CookieClient("testcookie", null);
        TestsHelper.expectException(() -> cookieClient.getCredentials(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final var cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.setProfileCreator(null);
        TestsHelper.expectException(() -> cookieClient.getUserProfile(new TokenCredentials(TOKEN), MockWebContext.create(),
            new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final var cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.init();
    }

    @Test(expected=Exception.class)
    public void testMissingCookieName() {
        final var cookieClient = new CookieClient(null, new SimpleTestTokenAuthenticator());
        cookieClient.init();
    }

    @Test
    public void testAuthentication() {
        final var client = new CookieClient(USERNAME, new SimpleTestTokenAuthenticator());
        final var context = MockWebContext.create();

        final var c = new Cookie(USERNAME, Base64.getEncoder().encodeToString(getClass().getName().getBytes(StandardCharsets.UTF_8)));
        context.getRequestCookies().add(c);
        final var credentials = (TokenCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (CommonProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals(c.getValue(), profile.getId());
    }
}
