package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.io.UnsupportedEncodingException;
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
        final CookieClient cookieClient = new CookieClient("testcookie", null);
        TestsHelper.initShouldFail(cookieClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final CookieClient cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.setProfileCreator(null);
        TestsHelper.initShouldFail(cookieClient, "profileCreator cannot be null");
    }

    @Test
    public void testBadAuthenticatorType() {
        final CookieClient cookieClient = new CookieClient("testcookie", new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(cookieClient, "Unsupported authenticator type: class org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final CookieClient cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.init(null);
    }

    @Test(expected=Exception.class)
    public void testMissingCookieName() {
        final CookieClient cookieClient = new CookieClient(null, new SimpleTestTokenAuthenticator());
        cookieClient.init(null);
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction, UnsupportedEncodingException {
        final CookieClient client = new CookieClient(USERNAME, new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();

        final Cookie c = new Cookie(USERNAME, Base64.getEncoder().encodeToString(getClass().getName().getBytes(HttpConstants.UTF8_ENCODING)));
        context.getRequestCookies().add(c);
        final TokenCredentials credentials = client.getCredentials(context);
        final CommonProfile profile = client.getUserProfile(credentials, context);
        assertEquals(c.getValue(), profile.getId());
    }
}
