package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import static org.junit.Assert.assertEquals;

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
        val client = new IpClient(null);
        TestsHelper.expectException(() -> client.getCredentials(new CallContext(MockWebContext.create(), new MockSessionStore())),
            TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val client = new IpClient(new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.expectException(() -> client.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new TokenCredentials(TOKEN)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val client = new IpClient(new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testAuthentication() {
        Client client = new IpClient(new SimpleTestTokenAuthenticator());
        val context = MockWebContext.create();
        context.setRemoteAddress(IP);
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = (TokenCredentials) client.getCredentials(ctx).get();
        val authnCredentials = client.validateCredentials(ctx, credentials).get();
        UserProfile profile = (CommonProfile) client.getUserProfile(ctx, authnCredentials).get();
        assertEquals(IP, profile.getId());
    }
}
