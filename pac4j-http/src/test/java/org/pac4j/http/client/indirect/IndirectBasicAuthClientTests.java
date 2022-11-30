package org.pac4j.http.client.indirect;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class tests the {@link IndirectBasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class IndirectBasicAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        val basicAuthClient = new IndirectBasicAuthClient(NAME, null);
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> basicAuthClient.getCredentials(MockWebContext.create(), new MockSessionStore(),
                ProfileManagerFactory.DEFAULT), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val basicAuthClient = new IndirectBasicAuthClient(NAME, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.setProfileCreator(null);
        TestsHelper.expectException(() -> basicAuthClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testMissingRealm() {
        val basicAuthClient = new IndirectBasicAuthClient(null, new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(basicAuthClient, "realmName cannot be blank");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        basicAuthClient.init();
    }

    @Test
    public void testMissingCallbackUrl() {
        val basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(basicAuthClient,
            "callbackUrl cannot be blank: set it up either on this IndirectClient or on the global Config");
    }

    private IndirectBasicAuthClient getBasicAuthClient() {
        val basicAuthClient = new IndirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.setCallbackUrl(CALLBACK_URL);
        return basicAuthClient;
    }

    @Test
    public void testRedirectionUrl() {
        val basicAuthClient = getBasicAuthClient();
        var context = MockWebContext.create();
        val action = (FoundAction) basicAuthClient.getRedirectionAction(context, new MockSessionStore()).get();
        assertEquals(CALLBACK_URL + "?" + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=" + basicAuthClient.getName(),
            action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingHeader() {
        val basicAuthClient = getBasicAuthClient();
        val context = MockWebContext.create();
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsNotABasicHeader() {
        val basicAuthClient = getBasicAuthClient();
        val context = getContextWithAuthorizationHeader("fakeHeader");
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsBadFormatHeader() {
        val basicAuthClient = getBasicAuthClient();
        val context = getContextWithAuthorizationHeader("Basic fakeHeader");
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsMissingSemiColon() {
        val basicAuthClient = getBasicAuthClient();
        val context = getContextWithAuthorizationHeader(
                "Basic " + Base64.getEncoder().encodeToString("fake".getBytes(StandardCharsets.UTF_8)));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsBadCredentials() {
        val basicAuthClient = getBasicAuthClient();
        val header = USERNAME + ":" + PASSWORD;
        val context = getContextWithAuthorizationHeader("Basic "
                + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        verifyGetCredentialsFailsWithAuthenticationRequired(basicAuthClient, context);
    }

    @Test
    public void testGetCredentialsGoodCredentials() {
        val basicAuthClient = getBasicAuthClient();
        val header = USERNAME + ":" + USERNAME;
        val credentials = (UsernamePasswordCredentials) basicAuthClient
            .getCredentials(getContextWithAuthorizationHeader("Basic "
                + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))), new MockSessionStore(),
                ProfileManagerFactory.DEFAULT).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    private void verifyGetCredentialsFailsWithAuthenticationRequired(
            IndirectBasicAuthClient basicAuthClient,
            MockWebContext context) {
        try {
            basicAuthClient.getCredentials(context, new MockSessionStore(), ProfileManagerFactory.DEFAULT);
            fail("should throw HttpAction");
        } catch (final HttpAction e) {
            assertEquals(401, e.getCode());
            assertEquals("Basic realm=\"authentication required\"",
                    context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
        }
    }

    private MockWebContext getContextWithAuthorizationHeader(String value) {
        var context = MockWebContext.create();
        return context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, value);
    }
}
