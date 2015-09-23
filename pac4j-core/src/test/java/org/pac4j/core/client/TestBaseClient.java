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
package org.pac4j.core.client;

import junit.framework.TestCase;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link BaseClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestBaseClient extends TestCase implements TestsConstants {

    public void testClone() {
        final IndirectClient<Credentials, CommonProfile> oldClient = new MockBaseClient<Credentials>(TYPE);
        oldClient.setCallbackUrl(CALLBACK_URL);
        final IndirectClient<Credentials, CommonProfile> newClient = (IndirectClient<Credentials, CommonProfile>) oldClient.clone();
        assertEquals(oldClient.getName(), newClient.getName());
        assertEquals(oldClient.getCallbackUrl(), newClient.getCallbackUrl());
        assertEquals(oldClient.getAuthorizationGenerators(), newClient.getAuthorizationGenerators());
    }

    public void testDirectClient() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context, false);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
        final Credentials credentials = client.getCredentials(context);
        assertNull(credentials);
    }

    public void testIndirectClient() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context, false);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(CommonHelper.addParameter(CALLBACK_URL, IndirectClient.NEEDS_CLIENT_REDIRECTION_PARAMETER, "true"),
                redirectionUrl);
        context.addRequestParameter(IndirectClient.NEEDS_CLIENT_REDIRECTION_PARAMETER, "true");
        try {
            client.getCredentials(context);
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(302, context.getResponseStatus());
            assertEquals(LOGIN_URL, context.getResponseHeaders().get("Location"));
            assertEquals("Needs client redirection", e.getMessage());
        }
    }

    public void testIndirectClientWithImmediate() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.redirect(context, true);
        final String redirectionUrl = context.getResponseLocation();
        assertEquals(LOGIN_URL, redirectionUrl);
    }

    public void testNullCredentials() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        final MockWebContext context = MockWebContext.create();
        client.setCallbackUrl(CALLBACK_URL);
        assertNull(client.getUserProfile(null, context));
    }

    public void testPrependHostToUrlIfNotPresent_whenHostIsNotPresent() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setEnableContextualRedirects(true);

        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");

        String result = client.prependHostToUrlIfNotPresent("/cas/login", context);

        assertEquals("http://pac4j.com/cas/login", result);
    }

    public void testPrependHostToUrlIfNotPresent_whenHostIsPresent() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setEnableContextualRedirects(true);

        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");

        String result = client.prependHostToUrlIfNotPresent("http://cashost.com/cas/login", context);

        assertEquals("http://cashost.com/cas/login", result);
    }

    public void testPrependHostToUrlIfNotPresent_whenServerIsNotUsingDefaultHttpPort() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setEnableContextualRedirects(true);

        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");
        context.setServerPort(8080);

        String result = client.prependHostToUrlIfNotPresent("/cas/login", context);

        assertEquals("http://pac4j.com:8080/cas/login", result);
    }

    public void testPrependHostToUrlIfNotPresent_whenRequestIsSecure() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setEnableContextualRedirects(true);

        final MockWebContext context = MockWebContext.create();
        context.setScheme("https");

        String result = client.prependHostToUrlIfNotPresent("/cas/login", context);

        assertEquals("https://localhost/cas/login", result);
    }

    public void testPrependHostToUrlIfNotPresent_whenContextualRedirectsAreDisabled() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE, false);
        client.setEnableContextualRedirects(false);

        final MockWebContext context = MockWebContext.create();

        String result = client.prependHostToUrlIfNotPresent("/cas/login", context);

        assertEquals("/cas/login", result);
    }

    public void testAjaxRequest() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create().addRequestHeader(HttpConstants.AJAX_HEADER_NAME, HttpConstants.AJAX_HEADER_VALUE);
        try {
            client.redirect(context, false);
            fail("should fail");
        } catch (RequiresHttpAction e) {
            assertEquals(401, e.getCode());
            assertEquals(401, context.getResponseStatus());
        }
    }

    public void testAlreadyTried() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        context.setSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        try {
            client.redirect(context, true);
            fail("should fail");
        } catch (RequiresHttpAction e) {
            assertEquals(403, e.getCode());
            assertEquals(403, context.getResponseStatus());
        }
    }

    public void testSaveAlreadyTried() throws RequiresHttpAction {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        client.getCredentials(context);
        assertEquals("true",
                (String) context.getSessionAttribute(client.getName() + IndirectClient.ATTEMPTED_AUTHENTICATION_SUFFIX));
    }

    public void testStateParameter() {
        final MockBaseClient<Credentials> client = new MockBaseClient<Credentials>(TYPE);
        final MockWebContext context = MockWebContext.create();
        try {
            client.getStateParameter(context);
            fail("should fail");
        } catch (UnsupportedOperationException e) {

        }
    }
}
