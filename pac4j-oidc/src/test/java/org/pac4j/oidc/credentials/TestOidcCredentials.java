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
package org.pac4j.oidc.credentials;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import junit.framework.TestCase;

/**
 * General test cases for OidcCredentials.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestOidcCredentials extends TestCase {

    public void testClearOidcCredentials() {
        OidcCredentials oidcCredentials = new OidcCredentials(new AuthorizationCode(), "testClient");
        oidcCredentials.clear();
        assertNull(oidcCredentials.getClientName());
        assertNull(oidcCredentials.getCode());
    }
}
