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

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link CookieClient} class.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieClientTests implements TestsConstants {

    @Test
    public void testClone() {
        final CookieClient oldClient = new CookieClient();
        oldClient.setName(TYPE);
        final AuthenticatorProfileCreator profileCreator = new AuthenticatorProfileCreator();
        oldClient.setProfileCreator(profileCreator);
        final TokenAuthenticator authN = new SimpleTestTokenAuthenticator();
        oldClient.setAuthenticator(authN);
        final CookieClient client = (CookieClient) oldClient.clone();
        assertEquals(oldClient.getName(), client.getName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getAuthenticator(), client.getAuthenticator());
    }

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final CookieClient cookieClient = new CookieClient(null, new AuthenticatorProfileCreator());
        cookieClient.setCookieName("testcookie");
        TestsHelper.initShouldFail(cookieClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final CookieClient cookieClient = new CookieClient(new SimpleTestTokenAuthenticator(), null);
        cookieClient.setCookieName("testcookie");
        TestsHelper.initShouldFail(cookieClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final CookieClient cookieClient = new CookieClient(new SimpleTestTokenAuthenticator());
        cookieClient.setCookieName("testcookie");
        cookieClient.init();
    }

    @Test(expected=Exception.class)
    public void testMissingCookieName() {
        final CookieClient cookieClient = new CookieClient(new SimpleTestTokenAuthenticator());
        cookieClient.init();
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction {
        final CookieClient client = new CookieClient(new SimpleTestTokenAuthenticator(),
                new AuthenticatorProfileCreator());
        client.setCookieName(USERNAME);
        final MockWebContext context = MockWebContext.create();

        final Cookie c = new Cookie(USERNAME, Base64.encodeBase64String(getClass().getName().getBytes()));
        context.getRequestCookies().add(c);
        final TokenCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(c.getValue(), profile.getId());
    }
}
