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
