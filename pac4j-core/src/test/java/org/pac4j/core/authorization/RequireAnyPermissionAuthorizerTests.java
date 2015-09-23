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
 * Tests {@link RequireAnyPermissionAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyPermissionAuthorizerTests {

    private static final String PERMISSION1 = "permission1";
    private static final String PERMISSION2 = "permission2";
    private static final String PERMISSION3 = "permission3";

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAnyPermissionOnePermission() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(PERMISSION1);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionOnePermission2() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setPermissions(PERMISSION1);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionOnePermission3() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setPermissions(Arrays.asList(PERMISSION1));
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionOnePermission4() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setPermissions(new HashSet<String>(Arrays.asList(PERMISSION1)));
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionOnePermissionFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION1});
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionNull() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer((List<String>) null);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionEmpty() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {});
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionOkTwoPermissions() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(PERMISSION2, PERMISSION1);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyPermissionProfileTwoPermissionsFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION2});
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
