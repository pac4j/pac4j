package org.pac4j.stormpath.credentials.authenticator;

import org.junit.Test;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * Tests the {@link StormpathAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class StormpathAuthenticatorTests implements TestsConstants {

    @Test
    public void testMissingAccessId() {
        final StormpathAuthenticator authenticator = new StormpathAuthenticator();
        authenticator.setSecretKey(VALUE);
        authenticator.setApplicationId(VALUE);
        TestsHelper.initShouldFail(authenticator, "accessId cannot be blank");
    }

    @Test
    public void testMissingSecretKey() {
        final StormpathAuthenticator authenticator = new StormpathAuthenticator(VALUE, null, VALUE);
        TestsHelper.initShouldFail(authenticator, "secretKey cannot be blank");
    }

    @Test
    public void testMissingAppId() {
        final StormpathAuthenticator authenticator = new StormpathAuthenticator();
        authenticator.setAccessId(VALUE);
        authenticator.setSecretKey(VALUE);
        TestsHelper.initShouldFail(authenticator, "applicationId cannot be blank");
    }
}
