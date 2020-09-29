package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DirectBasicAuthClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DirectBasicAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(null);
        TestsHelper.expectException(() -> basicAuthClient.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.expectException(() -> basicAuthClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
            MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        final DirectBasicAuthClient client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final MockWebContext context = MockWebContext.create();
        final String header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
            "Basic " + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) client.getCredentials(context).get();
        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context).get();
        assertEquals(USERNAME, profile.getId());
    }

    @Test
    public void testAuthenticationLowercase() {
        final DirectBasicAuthClient client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final MockWebContext context = MockWebContext.create();
        final String header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER.toLowerCase(),
            "Basic " + Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8)));
        final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) client.getCredentials(context).get();
        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context).get();
        assertEquals(USERNAME, profile.getId());
    }
}
