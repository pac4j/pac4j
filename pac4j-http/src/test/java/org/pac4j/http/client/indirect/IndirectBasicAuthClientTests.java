package org.pac4j.http.client.indirect;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IndirectBasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class IndirectBasicAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(NAME, null);
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(NAME, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.setProfileCreator(null);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }

    @Test
    public void testMissingRealm() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(null, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "realmName cannot be blank");
    }

    @Test
    public void testBadAuthenticatorType() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(NAME, new SimpleTestTokenAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "Unsupported authenticator type: class org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.init(null);
    }

    @Test
    public void testMissingLoginUrl() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(basicAuthClient, "callbackUrl cannot be blank");
    }

    private IndirectBasicAuthClient getBasicAuthClient() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        return basicAuthClient;
    }

    @Test
    public void testRedirectionUrl() throws RequiresHttpAction {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        MockWebContext context = MockWebContext.create();
        basicAuthClient.redirect(context);
        assertEquals(CALLBACK_URL, context.getResponseLocation());
    }

    @Test
    public void testGetCredentialsMissingHeader() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = MockWebContext.create();
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsNotABasicHeader() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = getContextWithAuthorizationHeader("fakeHeader");
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsBadFormatHeader() throws RequiresHttpAction {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = getContextWithAuthorizationHeader("Basic fakeHeader");
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsMissingSemiColon() throws RequiresHttpAction {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = getContextWithAuthorizationHeader(
                "Basic " + Base64.getEncoder().encodeToString("fake".getBytes()));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsBadCredentials() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + PASSWORD;
        final MockWebContext context = getContextWithAuthorizationHeader("Basic "
                + Base64.getEncoder().encodeToString(header.getBytes()));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsGoodCredentials() throws RequiresHttpAction {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + USERNAME;
        final UsernamePasswordCredentials credentials = basicAuthClient.getCredentials(
                getContextWithAuthorizationHeader(
                        "Basic " + Base64.getEncoder().encodeToString(header.getBytes())));
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    private void verifyGetCredentialsFailsWithAuthenticationRequired(
            IndirectBasicAuthClient basicAuthClient,
            MockWebContext context) {
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

    private MockWebContext getContextWithAuthorizationHeader(String value) {
        MockWebContext context = MockWebContext.create();
        return context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, value);
    }
}
