package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
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
        final IpClient client = new IpClient(null);
        TestsHelper.expectException(() -> client.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.expectException(() -> client.getUserProfile(new TokenCredentials(TOKEN),
                MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testAuthentication() {
        final IpClient client = new IpClient(new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();
        context.setRemoteAddress(IP);
        final TokenCredentials credentials = client.getCredentials(context).get();
        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context).get();
        assertEquals(IP, profile.getId());
    }
}
