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
package org.pac4j.core.authorization.generator;

import org.junit.Test;
import org.pac4j.core.authorization.generator.DefaultRolesPermissionsAuthorizationGenerator;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests {@link DefaultRolesPermissionsAuthorizationGenerator}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultRolesPermissionsAuthorizationGeneratorTests {

    private final static String[] DEFAULT_ROLES_ARRAY = new String[]{"R1", "R2"};
    private final static String[] DEFAULT_PERMISSIONS_ARRAY = new String[]{"P1", "P2"};
    private final static List<String> DEFAULT_ROLES_LIST = Arrays.asList(DEFAULT_ROLES_ARRAY);
    private final static List<String> DEFAULT_PERMISSIONS_LIST = Arrays.asList(DEFAULT_PERMISSIONS_ARRAY);

    @Test
    public void testNullArrays() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator<CommonProfile>((String[]) null, null);
        checkEmptyProfile(generator);
    }

    private void checkEmptyProfile(final DefaultRolesPermissionsAuthorizationGenerator generator) {
        final CommonProfile profile = new CommonProfile();
        generator.generate(profile);
        assertEquals(0, profile.getRoles().size());
        assertEquals(0, profile.getPermissions().size());
    }

    @Test
    public void testNullLists() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator<CommonProfile>((List<String>) null, null);
        checkEmptyProfile(generator);
    }

    @Test
    public void testDefaultValuesArrays() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator<CommonProfile>(DEFAULT_ROLES_ARRAY, DEFAULT_PERMISSIONS_ARRAY);
        checkProfile(generator);
    }

    private void checkProfile(final DefaultRolesPermissionsAuthorizationGenerator generator) {
        final CommonProfile profile = new CommonProfile();
        generator.generate(profile);
        final List<String> roles = profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.containsAll(DEFAULT_ROLES_LIST));
        final List<String> permissions = profile.getPermissions();
        assertEquals(2, permissions.size());
        assertTrue(permissions.containsAll(DEFAULT_PERMISSIONS_LIST));
    }

    @Test
    public void testDefaultValuesLists() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator<CommonProfile>(DEFAULT_ROLES_LIST, DEFAULT_PERMISSIONS_LIST);
        checkProfile(generator);
    }
}
