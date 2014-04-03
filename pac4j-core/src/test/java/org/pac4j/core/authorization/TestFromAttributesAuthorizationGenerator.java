/*
  Copyright 2012 - 2014 Jerome Leleu

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

import java.util.List;

import junit.framework.TestCase;

import org.pac4j.core.profile.CommonProfile;

/**
 * This class tests {@link FromAttributesAuthorizationGenerator}.
 * 
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class TestFromAttributesAuthorizationGenerator extends TestCase {
    
    private final static String ATTRIB1 = "attrib1";
    private final static String VALUE1 = "info11,info12";
    private final static String ATTRIB2 = "attrib2";
    private final static String VALUE2 = "info21,info22";
    private final static String ATTRIB3 = "attrib3";
    
    private CommonProfile profile;
    
    @Override
    public void setUp() {
        this.profile = new CommonProfile();
        this.profile.addAttribute(ATTRIB1, VALUE1);
        this.profile.addAttribute(ATTRIB2, VALUE2);
    }
    
    public void testNoConfig() {
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      null,
                                                                                                                                      null);
        generator.generate(this.profile);
        assertEquals(0, this.profile.getRoles().size());
        assertEquals(0, this.profile.getPermissions().size());
    }
    
    public void testRolePermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
        final List<String> roles = this.profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.contains("info11"));
        assertTrue(roles.contains("info12"));
        final List<String> permissions = this.profile.getPermissions();
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("info21"));
        assertTrue(permissions.contains("info22"));
    }
    
    public void testNoRolePermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB3
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
        assertEquals(0, this.profile.getRoles().size());
        final List<String> permissions = this.profile.getPermissions();
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("info21"));
        assertTrue(permissions.contains("info22"));
    }
    
    public void testRoleNoPermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB3
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
        final List<String> roles = this.profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.contains("info11"));
        assertTrue(roles.contains("info12"));
        assertEquals(0, this.profile.getPermissions().size());
    }
    
    public void testRolePermissionChangeSplit() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.setSplitChar("|");
        generator.generate(this.profile);
        final List<String> roles = this.profile.getRoles();
        assertEquals(1, roles.size());
        assertEquals(VALUE1, roles.get(0));
        final List<String> permissions = this.profile.getPermissions();
        assertEquals(1, permissions.size());
        assertEquals(VALUE2, permissions.get(0));
    }
}
