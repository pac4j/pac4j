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

import org.apache.commons.codec.binary.Base64;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.ProfileCreator;
import org.pac4j.http.profile.UsernameProfileCreator;

/**
 * This class tests the {@link BasicAuthClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestBasicAuthClient extends TestCase implements TestsConstants {
    
    public void testClone() {
        final BasicAuthClient oldClient = new BasicAuthClient();
        oldClient.setCallbackUrl(CALLBACK_URL);
        oldClient.setFailureUrl(FAILURE_URL);
        oldClient.setType(TYPE);
        oldClient.setRealmName(REALM_NAME);
        ProfileCreator profileCreator = new UsernameProfileCreator();
        oldClient.setProfileCreator(profileCreator);
        UsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        oldClient.setUsernamePasswordAuthenticator(usernamePasswordAuthenticator);
        final BasicAuthClient client = (BasicAuthClient) oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getFailureUrl(), client.getFailureUrl());
        assertEquals(oldClient.getType(), client.getType());
        assertEquals(oldClient.getRealmName(), client.getRealmName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getUsernamePasswordAuthenticator(), client.getUsernamePasswordAuthenticator());
    }
    
    public void testMissingUsernamePasswordAuthenticator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(null, new UsernameProfileCreator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "usernamePasswordAuthenticator cannot be null");
    }
    
    public void testMissingProfileCreator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }
    
    public void testMissingLoginUrl() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(),
                                                                    new UsernameProfileCreator());
        TestsHelper.initShouldFail(basicAuthClient, "callbackUrl cannot be blank");
    }
    
    private BasicAuthClient getBasicAuthClient() {
        BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(),
                                                              new UsernameProfileCreator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        return basicAuthClient;
    }
    
    public void testRedirectionUrl() throws ClientException {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        assertEquals(CALLBACK_URL, basicAuthClient.getRedirectionUrl(MockWebContext.create()));
    }
    
    public void testGetCredentialsMissingHeader() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        try {
            basicAuthClient.getCredentials(MockWebContext.create());
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("No basic auth header found", e.getMessage());
        }
    }
    
    public void testGetCredentialsNotABasicHeader() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        try {
            basicAuthClient.getCredentials(MockWebContext.create()
                .addRequestHeader(BasicAuthClient.BASICAUTH_HEADER_NAME, "fakeHeader"));
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("No basic auth header found", e.getMessage());
        }
    }
    
    public void testGetCredentialsBadFormatHeader() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        try {
            basicAuthClient.getCredentials(MockWebContext.create()
                .addRequestHeader(BasicAuthClient.BASICAUTH_HEADER_NAME, "Basic fakeHeader"));
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("Bad format of the basic auth header", e.getMessage());
        }
    }
    
    public void testGetCredentialsMissingSemiColon() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        try {
            basicAuthClient.getCredentials(MockWebContext.create()
                .addRequestHeader(BasicAuthClient.BASICAUTH_HEADER_NAME,
                                  "Basic " + Base64.encodeBase64String("fake".getBytes())));
            fail("should fail");
        } catch (final Exception e) {
            assertEquals("Bad format of the basic auth header", e.getMessage());
        }
    }
}
