/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpClientTests implements TestsConstants {

    private final static String IP = "127.0.0.2";

    @Test
    public void testClone() {
        final IpClient oldClient = new IpClient();
        oldClient.setName(TYPE);
        oldClient.setProfileCreator(new AuthenticatorProfileCreator<TokenCredentials, HttpProfile>());
        final TokenAuthenticator authenticator = new SimpleTestTokenAuthenticator();
        oldClient.setAuthenticator(authenticator);
        final IpClient client = (IpClient) oldClient.clone();
        assertEquals(oldClient.getName(), client.getName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getAuthenticator(), client.getAuthenticator());
    }

    @Test
    public void testMissingTokendAuthenticator() {
        final IpClient client = new IpClient(null);
        TestsHelper.initShouldFail(client, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.initShouldFail(client, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();
        context.setRemoteAddress(IP);
        final TokenCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(IP, profile.getId());
    }
}
