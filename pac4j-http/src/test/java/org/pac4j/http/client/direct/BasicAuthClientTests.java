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

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.profile.creator.test.SimpleTestUsernameProfileCreator;

/**
 * This class tests the {@link BasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class BasicAuthClientTests extends TestCase implements TestsConstants {

    public void testClone() {
        final BasicAuthClient oldClient = new BasicAuthClient();
        oldClient.setName(TYPE);
        final SimpleTestUsernameProfileCreator profileCreator = new SimpleTestUsernameProfileCreator();
        oldClient.setProfileCreator(profileCreator);
        final UsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        oldClient.setAuthenticator(usernamePasswordAuthenticator);
        final BasicAuthClient client = (BasicAuthClient) oldClient.clone();
        assertEquals(oldClient.getName(), client.getName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getAuthenticator(), client.getAuthenticator());
    }

    public void testMissingUsernamePasswordAuthenticator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(null, new SimpleTestUsernameProfileCreator());
        TestsHelper.initShouldFail(basicAuthClient, "authenticator cannot be null");
    }

    public void testMissingProfileCreator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }

    public void testAuthentication() throws RequiresHttpAction {
        final BasicAuthClient client = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), new SimpleTestUsernameProfileCreator());
        final MockWebContext context = MockWebContext.create();
        final String header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Basic " + Base64.encodeBase64String(header.getBytes()));
        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
    }
}
