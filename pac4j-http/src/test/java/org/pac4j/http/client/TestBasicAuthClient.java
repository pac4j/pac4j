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
 */package org.pac4j.http.client;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.profile.creator.test.SimpleTestUsernameProfileCreator;

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
        oldClient.setName(TYPE);
        final SimpleTestUsernameProfileCreator profileCreator = new SimpleTestUsernameProfileCreator();
        oldClient.setProfileCreator(profileCreator);
        final UsernamePasswordAuthenticator usernamePasswordAuthenticator = new SimpleTestUsernamePasswordAuthenticator();
        oldClient.setAuthenticator(usernamePasswordAuthenticator);
        final BasicAuthClient client = (BasicAuthClient) oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getName(), client.getName());
        assertEquals(oldClient.getProfileCreator(), client.getProfileCreator());
        assertEquals(oldClient.getAuthenticator(), client.getAuthenticator());
    }

    public void testMissingUsernamePasswordAuthenticator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(null, new SimpleTestUsernameProfileCreator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "authenticator cannot be null");
    }

    public void testMissingProfileCreator() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }

    public void testMissingLoginUrl() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(),
                new SimpleTestUsernameProfileCreator());
        TestsHelper.initShouldFail(basicAuthClient, "callbackUrl cannot be blank");
    }

    private BasicAuthClient getBasicAuthClient() {
        final BasicAuthClient basicAuthClient = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(),
                new SimpleTestUsernameProfileCreator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        return basicAuthClient;
    }

    public void testRedirectionUrl() throws RequiresHttpAction {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        MockWebContext context = MockWebContext.create();
        basicAuthClient.redirect(context, false, false);
        assertEquals(CALLBACK_URL, context.getResponseLocation());
    }

    public void testGetCredentialsMissingHeader() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = MockWebContext.create();
        try {
            basicAuthClient.getCredentials(context);
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(401, context.getResponseStatus());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals("Requires authentication", e.getMessage());
        }
    }

    public void testGetCredentialsNotABasicHeader() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = MockWebContext.create();
        try {
            basicAuthClient.getCredentials(context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "fakeHeader"));
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(401, context.getResponseStatus());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals("Requires authentication", e.getMessage());
        }
    }

    public void testGetCredentialsBadFormatHeader() throws RequiresHttpAction {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = MockWebContext.create();
        try {
            basicAuthClient.getCredentials(context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                    "Basic fakeHeader"));
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(401, context.getResponseStatus());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals("Requires authentication", e.getMessage());
        }
    }

    public void testGetCredentialsMissingSemiColon() throws RequiresHttpAction {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = MockWebContext.create();
        try {
            basicAuthClient.getCredentials(context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                    "Basic " + Base64.encodeBase64String("fake".getBytes())));
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(401, context.getResponseStatus());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals("Requires authentication", e.getMessage());
        }
    }

    public void testGetCredentialsBadCredentials() {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + PASSWORD;
        final MockWebContext context = MockWebContext.create();
        try {
            basicAuthClient.getCredentials(context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, "Basic "
                    + Base64.encodeBase64String(header.getBytes())));
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(401, context.getResponseStatus());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
            assertEquals("Requires authentication", e.getMessage());
        }
    }

    public void testGetCredentialsGoodCredentials() throws RequiresHttpAction {
        final BasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + USERNAME;
        final UsernamePasswordCredentials credentials = basicAuthClient.getCredentials(MockWebContext.create()
                .addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                        "Basic " + Base64.encodeBase64String(header.getBytes())));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }
}
