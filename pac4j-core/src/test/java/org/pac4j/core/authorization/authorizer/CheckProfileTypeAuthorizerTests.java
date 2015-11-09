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
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link CheckProfileTypeAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckProfileTypeAuthorizerTests {

    class FakeProfile1 extends CommonProfile {
    }
    class FakeProfile2 extends CommonProfile {
    }

    @Test
    public void testGoodProfile() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(FakeProfile1.class, FakeProfile2.class);
        assertTrue(authorizer.isAuthorized(null, new FakeProfile1()));
    }

    @Test
    public void testBadProfileType() {
        final CheckProfileTypeAuthorizer authorizer = new CheckProfileTypeAuthorizer(FakeProfile1.class);
        assertFalse(authorizer.isAuthorized(null, new FakeProfile2()));
    }
}
