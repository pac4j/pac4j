package org.pac4j.cas.client.rest;

import org.junit.Test;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.concurrent.TimeUnit;

/**
 * Tests the {@link CasRestBasicAuthClient} and {@link CasRestFormClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class CasRestClientTests implements TestsConstants {

    private class FakeAuthenticator implements Authenticator<CasCredentials> {

        public void validate(final CasCredentials credentials) {}
    }

    @Test
    public void testBasicAuthBadAuthenticatorType() {
        final CasRestBasicAuthClient client = new CasRestBasicAuthClient(new FakeAuthenticator());
        TestsHelper.initShouldFail(client, "Unsupported authenticator type: class org.pac4j.cas.client.rest.CasRestClientTests$FakeAuthenticator");
    }

    @Test
    public void testFormBadAuthenticatorType() {
        final CasRestFormClient client = new CasRestFormClient(new LocalCachingAuthenticator<>(new FakeAuthenticator(), 100, 10, TimeUnit.SECONDS));
        TestsHelper.initShouldFail(client, "Unsupported authenticator type: class org.pac4j.cas.client.rest.CasRestClientTests$FakeAuthenticator");
    }
}
