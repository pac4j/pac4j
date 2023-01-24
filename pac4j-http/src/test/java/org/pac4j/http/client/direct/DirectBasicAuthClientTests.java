package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link DirectBasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DirectBasicAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        val basicAuthClient = new DirectBasicAuthClient(null);
        TestsHelper.expectException(() -> basicAuthClient.getCredentials(new CallContext(MockWebContext.create(), new MockSessionStore())),
                TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.expectException(() -> basicAuthClient.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new UsernamePasswordCredentials(USERNAME, PASSWORD)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        val client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        val context = MockWebContext.create();
        val header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
            "Basic " + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials =
            (UsernamePasswordCredentials) client.getCredentials(ctx).get();
        val authnCredentials = client.validateCredentials(ctx, credentials).get();
        val profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(USERNAME, profile.getId());
    }

    @Test
    public void testAuthenticationLowercase() {
        val client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        val context = MockWebContext.create();
        val header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER.toLowerCase(),
            "Basic " + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials =
            (UsernamePasswordCredentials) client.getCredentials(ctx).get();
        val authnCredentials = client.validateCredentials(ctx, credentials).get();
        val profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(USERNAME, profile.getId());
    }
}
