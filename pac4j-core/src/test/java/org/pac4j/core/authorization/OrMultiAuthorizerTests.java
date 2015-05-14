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
package org.pac4j.core.authorization;

import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link OrMultiAuthorizerTests}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class OrMultiAuthorizerTests {

    private static class FixedAuthorizer<U extends CommonProfile> implements Authorizer<U> {

        private final boolean result;

        public FixedAuthorizer(final boolean result) {
            this.result = result;
        }

        public boolean isAuthorized(WebContext context, U profile) {
            return result;
        }
    }

    private final FixedAuthorizer trueAuthorizer = new FixedAuthorizer(true);
    private final FixedAuthorizer falseAuthorizer = new FixedAuthorizer(false);

    private final J2EContext context = new J2EContext(null, null);
    private final CommonProfile profile = new CommonProfile();

    @Test
    public void testNullReturnFalse() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(null);
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testTrueAuthorizer() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(new Authorizer[]{ trueAuthorizer });
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testFalseAuthorizer() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(new Authorizer[]{ falseAuthorizer });
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testTrueFalseAuthorizer() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(new Authorizer[]{ trueAuthorizer, falseAuthorizer });
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testFalseFalseAuthorizer() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(new Authorizer[]{ falseAuthorizer, falseAuthorizer });
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testFalseTrueAuthorizer() {
        final OrMultiAuthorizer authorizer = new OrMultiAuthorizer(new Authorizer[]{ falseAuthorizer, trueAuthorizer });
        assertTrue(authorizer.isAuthorized(context, profile));
    }
}
