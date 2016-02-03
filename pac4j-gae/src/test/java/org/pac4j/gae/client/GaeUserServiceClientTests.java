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
package org.pac4j.gae.client;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.pac4j.core.client.RedirectAction.*;

/**
 * Tests the {@link GaeUserServiceClient}.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class GaeUserServiceClientTests implements TestsConstants {

    private final static Logger logger = LoggerFactory.getLogger(GaeUserServiceClientTests.class);

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalUserServiceTestConfig())
                    .setEnvIsAdmin(true).setEnvIsLoggedIn(true).setEnvEmail(EMAIL).setEnvAuthDomain("");

    private GaeUserServiceClient client;

    private MockWebContext context;

    @Before
    public void setUp() throws Exception {
        client = new GaeUserServiceClient();
        client.setCallbackUrl(CALLBACK_URL);
        context = MockWebContext.create();
        helper.setUp();
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test(expected = TechnicalException.class)
    public void testCallbackMandatory() throws RequiresHttpAction {
        final GaeUserServiceClient localClient = new GaeUserServiceClient();
        localClient.redirect(context);
    }

    @Test
    public void testRedirect() throws RequiresHttpAction {
        final RedirectAction redirectAction = client.getRedirectAction(context);
        assertEquals(RedirectType.REDIRECT, redirectAction.getType());
        assertEquals("/_ah/login?continue=" + CommonHelper.urlEncode(CALLBACK_URL), redirectAction.getLocation());
    }

    @Test
    public void testGetCredentialsUserProfile() throws RequiresHttpAction {
        final GaeUserCredentials credentials = client.getCredentials(context);
        final User user = credentials.getUser();
        assertEquals(EMAIL, user.getEmail());
        assertEquals("", user.getAuthDomain());
        final GaeUserServiceProfile profile = client.getUserProfile(credentials, context);
        logger.debug("userProfile: {}", profile);
        assertEquals(EMAIL, profile.getId());
        assertEquals(GaeUserServiceProfile.class.getName() + UserProfile.SEPARATOR + EMAIL, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GaeUserServiceProfile.class));
        assertEquals("test", profile.getDisplayName());
        assertEquals("GLOBAL_ADMIN", profile.getRoles().get(0));
        assertEquals(2, profile.getAttributes().size());
    }
}
