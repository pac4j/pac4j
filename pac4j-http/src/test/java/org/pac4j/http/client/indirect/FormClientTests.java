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
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link FormClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class FormClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final FormClient formClient = new FormClient(LOGIN_URL, null);
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(formClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.setProfileCreator(null);
        TestsHelper.initShouldFail(formClient, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.init(null);
    }

    @Test
    public void testMissingLoginUrl() {
        final FormClient formClient = new FormClient(null, new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }

    private FormClient getFormClient() {
        final FormClient client = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testRedirectionUrl() throws RequiresHttpAction {
        final FormClient formClient = getFormClient();
        MockWebContext context = MockWebContext.create();
        formClient.redirect(context);
        assertEquals(LOGIN_URL, context.getResponseLocation());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Username and password cannot be blank -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                    + FormClient.ERROR_PARAMETER + "=" + FormClient.MISSING_FIELD_ERROR, context.getResponseHeaders()
                    .get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Username and password cannot be blank -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=&" + FormClient.ERROR_PARAMETER + "="
                    + FormClient.MISSING_FIELD_ERROR, context.getResponseHeaders().get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetCredentials() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        try {
            formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                    .addRequestParameter(formClient.getPasswordParameter(), PASSWORD));
            fail("should fail");
        } catch (final RequiresHttpAction e) {
            assertEquals("Credentials validation fails -> return to the form with error", e.getMessage());
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                    + FormClient.ERROR_PARAMETER + "=" + CredentialsException.class.getSimpleName(), context
                    .getResponseHeaders().get(HttpConstants.LOCATION_HEADER));
        }
    }

    @Test
    public void testGetRightCredentials() throws RequiresHttpAction {
        final FormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final FormClient formClient = getFormClient();
        formClient.setProfileCreator(credentials -> {
            String username = credentials.getUsername();
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return profile;
        });
        final MockWebContext context = MockWebContext.create();
        final CommonProfile profile = formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME,
                formClient.getName()), context);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + UserProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
