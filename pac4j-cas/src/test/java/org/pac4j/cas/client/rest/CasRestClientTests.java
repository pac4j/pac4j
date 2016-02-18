/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
