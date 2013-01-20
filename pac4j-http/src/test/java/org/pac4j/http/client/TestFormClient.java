/*
  Copyright 2012 - 2013 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */package org.pac4j.http.client;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.HttpProfile;
import org.pac4j.http.profile.ProfileCreator;
import org.pac4j.http.profile.UsernameProfileCreator;

/**
 * This class tests the {@link FormClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestFormClient extends TestCase implements TestsConstants {
    
    public void testClone() {
        final FormClient oldClient = new FormClient();
        oldClient.setCallbackUrl(CALLBACK_URL);
        oldClient.setFailureUrl(FAILURE_URL);
        oldClient.setType(TYPE);
        oldClient.setPasswordParameter(PASSWORD);
        oldClient.setUsernameParameter(USERNAME);
        ProfileCreator profileCreator = new UsernameProfileCreator();
        oldClient.setProfileCreator(profileCreator);
        UsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        oldClient.setUsernamePasswordAuthenticator(usernamePasswordAuthenticator);
        final FormClient client = (FormClient) oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getFailureUrl(), client.getFailureUrl());
        assertEquals(oldClient.getType(), client.getType());
        assertEquals(oldClient.getUsernameParameter(), client.getUsernameParameter());
        assertEquals(oldClient.getPasswordParameter(), client.getPasswordParameter());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getUsernamePasswordAuthenticator(), client.getUsernamePasswordAuthenticator());
    }
    
    public void testMissingUsernamePasswordAuthenticator() {
        final FormClient formClient = new FormClient(LOGIN_URL, null, new UsernameProfileCreator());
        TestsHelper.initShouldFail(formClient, "usernamePasswordAuthenticator cannot be null");
    }
    
    public void testMissingProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(formClient, "profileCreator cannot be null");
    }
    
    public void testMissingLoginUrl() {
        final FormClient formClient = new FormClient(null, new SimpleTestUsernamePasswordAuthenticator(),
                                                     new UsernameProfileCreator());
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }
    
    private FormClient getFormClient() {
        return new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator(), new UsernameProfileCreator());
    }
    
    public void testRedirectionUrl() throws ClientException {
        final FormClient formClient = getFormClient();
        assertEquals(LOGIN_URL, formClient.getRedirectionUrl(MockWebContext.create()));
    }
    
    public void testGetCredentialsMissingUsername() {
        final FormClient formClient = getFormClient();
        try {
            formClient.getCredentials(MockWebContext.create().addRequestParameter(formClient.getPasswordParameter(),
                                                                                  PASSWORD));
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("Username and password cannot be blank", e.getMessage());
        }
    }
    
    public void testGetCredentialsMissingPassword() {
        final FormClient formClient = getFormClient();
        try {
            formClient.getCredentials(MockWebContext.create().addRequestParameter(formClient.getUsernameParameter(),
                                                                                  USERNAME));
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("Username and password cannot be blank", e.getMessage());
        }
    }
    
    public void testGetCredentials() throws ClientException {
        final FormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
            .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
            .addRequestParameter(formClient.getPasswordParameter(), PASSWORD));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(PASSWORD, credentials.getPassword());
    }
    
    public void testGetUserProfileNoCredential() {
        final FormClient formClient = getFormClient();
        try {
            formClient.getUserProfile(null);
            fail("should fail");
        } catch (final ClientException e) {
            assertEquals("No credential", e.getMessage());
        }
    }
    
    public void testGetUserProfileBadCredentials() {
        final FormClient formClient = getFormClient();
        try {
            formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD, formClient.getType()));
            fail("should fail");
        } catch (final ClientException e) {
            assertEquals("Username : '" + USERNAME + "' does not match password", e.getMessage());
        }
    }
    
    public void testGetUserProfileGoodCredentials() throws ClientException {
        final FormClient formClient = getFormClient();
        final HttpProfile profile = formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME,
                                                                                              formClient.getType()));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
