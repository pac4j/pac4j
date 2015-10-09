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
 */package org.pac4j.http.client.direct;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.creator.AuthenticatorProfileCreator;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DirectBasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DirectBasicAuthClientTests implements TestsConstants {

    @Test
    public void testClone() {
        final DirectBasicAuthClient oldClient = new DirectBasicAuthClient();
        oldClient.setName(TYPE);
        oldClient.setProfileCreator(new AuthenticatorProfileCreator<UsernamePasswordCredentials, HttpProfile>());
        final UsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        oldClient.setAuthenticator(usernamePasswordAuthenticator);
        final DirectBasicAuthClient client = (DirectBasicAuthClient) oldClient.clone();
        assertEquals(oldClient.getName(), client.getName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getAuthenticator(), client.getAuthenticator());
    }

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(null);
        TestsHelper.initShouldFail(basicAuthClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.init();
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction {
        final DirectBasicAuthClient client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final MockWebContext context = MockWebContext.create();
        final String header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Basic " + Base64.encodeBase64String(header.getBytes()));
        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
    }
}
