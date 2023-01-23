package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
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
        val cookieClient = new CookieClient("testcookie", null);
        TestsHelper.expectException(() -> cookieClient.getCredentials(new CallContext(MockWebContext.create(), new MockSessionStore())),
                TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.setProfileCreator(null);
        TestsHelper.expectException(() -> cookieClient.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new TokenCredentials(TOKEN)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.init();
    }

    @Test(expected=Exception.class)
    public void testMissingCookieName() {
        val cookieClient = new CookieClient(null, new SimpleTestTokenAuthenticator());
        cookieClient.init();
    }

    @Test
    public void testAuthentication() {
        val client = new CookieClient(USERNAME, new SimpleTestTokenAuthenticator());
        val context = MockWebContext.create();

        val c = new Cookie(USERNAME, Base64.getEncoder().encodeToString(getClass().getName().getBytes(StandardCharsets.UTF_8)));
        context.getRequestCookies().add(c);
        val credentials = (TokenCredentials) client.getCredentials(new CallContext(context, new MockSessionStore())).get();
        val profile = (CommonProfile) client.getUserProfile(new CallContext(context, new MockSessionStore()), credentials).get();
        assertEquals(c.getValue(), profile.getId());
    }
}
