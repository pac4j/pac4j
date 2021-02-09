package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpClientTests implements TestsConstants {

    private final static String IP = "goodIp";

    @Test
    public void testMissingTokendAuthenticator() {
        final var client = new IpClient(null);
        TestsHelper.expectException(() -> client.getCredentials(MockWebContext.create(), new MockSessionStore()),
             TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final var client = new IpClient(new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.expectException(() -> client.getUserProfile(new TokenCredentials(TOKEN),
                MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final var client = new IpClient(new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testAuthentication() {
        final var client = new IpClient(new SimpleTestTokenAuthenticator());
        final var context = MockWebContext.create();
        context.setRemoteAddress(IP);
        final var credentials = (TokenCredentials) client.getCredentials(context, new MockSessionStore()).get();
        final var profile = (CommonProfile) client.getUserProfile(credentials, context, new MockSessionStore()).get();
        assertEquals(IP, profile.getId());
    }
}
