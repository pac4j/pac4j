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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RequireAllRolesAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAllRolesAuthorizerTests {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAllRolesOkDifferentOrder() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(ROLE3, ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder2() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(Arrays.asList(ROLE3, ROLE1));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder3() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setRoles(ROLE3, ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder4() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setRoles(new HashSet<String>(Arrays.asList(ROLE3, ROLE1)));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder5() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setRoles(Arrays.asList(ROLE3, ROLE1));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesNull() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer((List<String>) null);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesEmpty() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{});
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesTwoRolesFail() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ROLE3, ROLE1});
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
