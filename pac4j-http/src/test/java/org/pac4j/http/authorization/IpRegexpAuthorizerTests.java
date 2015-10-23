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
package org.pac4j.http.authorization;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpRegexpAuthorizer}.
 * 
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class IpRegexpAuthorizerTests {

    private final static String GOOD_IP = "127.0.0.1";
    private final static String BAD_IP = "192.168.0.1";

    private final static IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer(GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        final IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer();
        authorizer.isAuthorized(MockWebContext.create(), null);
    }

    @Test
    public void testValidateGoodIP() {
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(GOOD_IP), null));
    }

    @Test
    public void testValidateBadIP() {
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(BAD_IP), null));
    }
}
