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
package org.pac4j.http.client.indirect;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IndirectFormClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class IndirectFormClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final IndirectFormClient formClient = new IndirectFormClient(LOGIN_URL, null);
        TestsHelper.initShouldFail(formClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final IndirectFormClient formClient = new IndirectFormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(formClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final IndirectFormClient formClient = new IndirectFormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.init(null);
    }

    @Test
    public void testMissingLoginUrl() {
        final IndirectFormClient formClient = new IndirectFormClient(null, new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }

    private IndirectFormClient getFormClient() {
        return new IndirectFormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
    }

    @Test
    public void testRedirectionUrl() throws RequiresHttpAction {
        final IndirectFormClient formClient = getFormClient();
        MockWebContext context = MockWebContext.create();
        formClient.redirect(context);
        assertEquals(LOGIN_URL, context.getResponseLocation());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final IndirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Username and password cannot be blank -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                    + IndirectFormClient.ERROR_PARAMETER + "=" + IndirectFormClient.MISSING_FIELD_ERROR, context.getResponseHeaders()
                    .get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final IndirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Username and password cannot be blank -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=&" + IndirectFormClient.ERROR_PARAMETER + "="
                    + IndirectFormClient.MISSING_FIELD_ERROR, context.getResponseHeaders().get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetCredentials() {
        final IndirectFormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                    .addRequestParameter(formClient.getPasswordParameter(), PASSWORD));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Credentials validation fails -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                    + IndirectFormClient.ERROR_PARAMETER + "=" + CredentialsException.class.getSimpleName(), context
                    .getResponseHeaders().get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetRightCredentials() throws RequiresHttpAction {
        final IndirectFormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final IndirectFormClient formClient = getFormClient();
        formClient.setProfileCreator(credentials -> {
            String username = credentials.getUsername();
            final HttpProfile profile = new HttpProfile();
            profile.setId(username);
            profile.addAttribute(CommonProfile.USERNAME, username);
            return profile;
        });
        final MockWebContext context = MockWebContext.create();
        final HttpProfile profile = formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME,
                formClient.getName()), context);
        assertEquals(USERNAME, profile.getId());
        assertEquals(HttpProfile.class.getName() + UserProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), HttpProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
