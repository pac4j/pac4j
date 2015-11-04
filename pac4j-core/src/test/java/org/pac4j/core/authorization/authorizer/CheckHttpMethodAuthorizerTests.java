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
package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.UserProfile;

import static org.junit.Assert.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Tests {@link CheckHttpMethodAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckHttpMethodAuthorizerTests {

    @Test
    public void testGoodHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.GET, HTTP_METHOD.POST);
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRequestMethod("GET"), new UserProfile()));
    }

    @Test
    public void testBadHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.PUT);
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRequestMethod("DELETE"), new UserProfile()));
    }
}
