package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

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
        TestsHelper.initShouldFail(basicAuthClient, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), null);
        TestsHelper.initShouldFail(basicAuthClient, "profileCreator cannot be null");
    }

    @Test
    public void testBadAuthenticatorType() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new LocalCachingAuthenticator<>(new SimpleTestTokenAuthenticator(), 10, 10, TimeUnit.HOURS));
        TestsHelper.initShouldFail(basicAuthClient, "Unsupported authenticator type: class org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        basicAuthClient.init(null);
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction, UnsupportedEncodingException {
        final DirectBasicAuthClient client = new DirectBasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
        final MockWebContext context = MockWebContext.create();
        final String header = USERNAME + ":" + USERNAME;
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
                "Basic " + Base64.getEncoder().encodeToString(header.getBytes(HttpConstants.UTF8_ENCODING)));
        final UsernamePasswordCredentials credentials = client.getCredentials(context);
        final CommonProfile profile = client.getUserProfile(credentials, context);
        assertEquals(USERNAME, profile.getId());
    }
}
