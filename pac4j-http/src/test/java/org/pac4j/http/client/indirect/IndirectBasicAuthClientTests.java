package org.pac4j.http.client.indirect;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.nio.charset.StandardCharsets;
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
        TestsHelper.expectException(() -> basicAuthClient.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(NAME, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.setProfileCreator(null);
        TestsHelper.expectException(() -> basicAuthClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testMissingRealm() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(null, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "realmName cannot be blank");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.init();
    }

    @Test
    public void testMissingCallbackUrl() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(basicAuthClient,
            "callbackUrl cannot be blank: set it up either on this IndirectClient or on the global Config");
    }

    private IndirectBasicAuthClient getBasicAuthClient() {
        final IndirectBasicAuthClient basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        return basicAuthClient;
    }

    @Test
    public void testRedirectionUrl() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) basicAuthClient.getRedirectionAction(context).get();
        assertEquals(CALLBACK_URL + "?" + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=" + basicAuthClient.getName(),
            action.getLocation());
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
    public void testGetCredentialsBadFormatHeader() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = getContextWithAuthorizationHeader("Basic fakeHeader");
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsMissingSemiColon() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final MockWebContext context = getContextWithAuthorizationHeader(
                "Basic " + Base64.getEncoder().encodeToString("fake".getBytes(StandardCharsets.UTF_8)));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsBadCredentials() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + PASSWORD;
        final MockWebContext context = getContextWithAuthorizationHeader("Basic "
                + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsGoodCredentials() {
        final IndirectBasicAuthClient basicAuthClient = getBasicAuthClient();
        final String header = USERNAME + ":" + USERNAME;
        final UsernamePasswordCredentials credentials= basicAuthClient.getCredentials(getContextWithAuthorizationHeader(
                "Basic " + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)))).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    private void verifyGetCredentialsFailsWithAuthenticationRequired(
            IndirectBasicAuthClient basicAuthClient,
            MockWebContext context) {
        try {
            basicAuthClient.getCredentials(context);
            fail("should throw HttpAction");
        } catch (final HttpAction e) {
            assertEquals(401, e.getCode());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
        }
    }

    private MockWebContext getContextWithAuthorizationHeader(String value) {
        MockWebContext context = MockWebContext.create();
        return context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, value);
    }
}
