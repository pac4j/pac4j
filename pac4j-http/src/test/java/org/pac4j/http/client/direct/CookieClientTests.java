/*
 *    Copyright 2012 - 2015 pac4j organization
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

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
        final CookieClient cookieClient = new CookieClient(null, null);
        cookieClient.setCookieName("testcookie");
        TestsHelper.initShouldFail(cookieClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final CookieClient cookieClient = new CookieClient("testcookie", new SimpleTestTokenAuthenticator());
        cookieClient.setProfileCreator(null);
        TestsHelper.initShouldFail(cookieClient, "profileCreator cannot be null");
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
    public void testAuthentication() throws RequiresHttpAction {
        final CookieClient client = new CookieClient(USERNAME, new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();

        final Cookie c = new Cookie(USERNAME, Base64.getEncoder().encodeToString(getClass().getName().getBytes()));
        context.getRequestCookies().add(c);
        final TokenCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(c.getValue(), profile.getId());
    }
}
