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
package org.pac4j.http.credentials.authenticator;

import org.junit.Test;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.credentials.TokenCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpRegexpAuthenticator}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpRegexpAuthenticatorTests {

    private final static String GOOD_IP = "127.0.0.1";
    private final static String BAD_IP = "192.168.0.1";

    private final static String CLIENT_NAME = "clientName";

    private final static IpRegexpAuthenticator authenticator = new IpRegexpAuthenticator(GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        IpRegexpAuthenticator authenticator = new IpRegexpAuthenticator();
        authenticator.validate(null);
    }

    @Test
    public void testValidateGoodIP() {
        final TokenCredentials credentials = new TokenCredentials(GOOD_IP, CLIENT_NAME);
        authenticator.validate(credentials);
    }

    @Test
    public void testValidateBadIP() {
        try {
            final TokenCredentials credentials = new TokenCredentials(BAD_IP, CLIENT_NAME);
            authenticator.validate(credentials);
            fail("Should fail");
        } catch (final CredentialsException e) {
            assertEquals("Unauthorized IP address: " + BAD_IP, e.getMessage());
        }
    }
}
