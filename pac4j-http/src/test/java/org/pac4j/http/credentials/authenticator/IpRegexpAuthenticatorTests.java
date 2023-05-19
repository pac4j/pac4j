package org.pac4j.http.credentials.authenticator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.profile.IpProfile;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link IpRegexpAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpRegexpAuthenticatorTests implements TestsConstants {

    private final static String GOOD_IP = "goodIp";
    private final static String BAD_IP = "badIp";

    private final static IpRegexpAuthenticator authenticator = new IpRegexpAuthenticator(GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        val credentials = new TokenCredentials(GOOD_IP);
        Authenticator authenticator = new IpRegexpAuthenticator();
        authenticator.validate(new CallContext(null, new MockSessionStore()), credentials);
    }

    @Test
    public void testValidateGoodIP() {
        val credentials = new TokenCredentials(GOOD_IP);
        authenticator.validate(new CallContext(null, new MockSessionStore()), credentials);
        UserProfile profile = (IpProfile) credentials.getUserProfile();
        assertEquals(GOOD_IP, profile.getId());
    }

    @Test
    public void testValidateBadIP() {
        val credentials = new TokenCredentials(BAD_IP);
        TestsHelper.expectException(() -> authenticator.validate(new CallContext(null, new MockSessionStore()), credentials),
            CredentialsException.class, "Unauthorized IP address: " + BAD_IP);
    }
}
