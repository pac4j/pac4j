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
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link HasAccessRoleAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HasAccessRoleAuthorizerTests {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAccessNull() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(null, null);
        final CommonProfile profile = new CommonProfile();
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessEmpty() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer("", "");
        final CommonProfile profile = new CommonProfile();
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAnyRoleOk() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(ROLE2, null);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAnyRoleKo() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(ROLE3, null);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAllRoleOk() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(null, ROLE1 + "," + ROLE2);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAllRoleKo() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(null, ROLE1 + "," + ROLE2 + "," + ROLE3);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAnyRoleOkAndAllRole() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(ROLE2, ROLE1 + "," + ROLE2 + "," + ROLE3);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAccessAnyRoleKoAndAllRole() {
        final HasAccessRoleAuthorizer authorizer = new HasAccessRoleAuthorizer(ROLE3, ROLE1 + "," + ROLE2 + "," + ROLE3);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
