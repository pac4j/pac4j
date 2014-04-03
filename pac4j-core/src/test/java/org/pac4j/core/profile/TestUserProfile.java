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
package org.pac4j.core.profile;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link UserProfile} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestUserProfile extends TestCase implements TestsConstants {
    
    private static final String ID = "id";
    
    private static final String ROLE = "oneRole";
    private static final String PERMISSION = "onePermission";
    
    public void testSetId() {
        final UserProfile userProfile = new UserProfile();
        assertNull(userProfile.getId());
        userProfile.setId(ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testAddAttribute() {
        final UserProfile userProfile = new UserProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(KEY, VALUE);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributes() {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(KEY, VALUE);
        final UserProfile userProfile = new UserProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttributes(attributes);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testUnsafeAddAttribute() {
        final UserProfile userProfile = new UserProfile();
        try {
            userProfile.getAttributes().put(KEY, VALUE);
            fail();
        } catch (final UnsupportedOperationException e) {
        }
    }
    
    public void testRoles() {
        final UserProfile profile = new UserProfile();
        assertEquals(0, profile.getRoles().size());
        profile.addRole(ROLE);
        assertEquals(1, profile.getRoles().size());
        assertEquals(ROLE, profile.getRoles().get(0));
    }
    
    public void testPermissions() {
        final UserProfile profile = new UserProfile();
        assertEquals(0, profile.getPermissions().size());
        profile.addPermission(PERMISSION);
        assertEquals(1, profile.getPermissions().size());
        assertEquals(PERMISSION, profile.getPermissions().get(0));
    }
    
    public void testRme() {
        final UserProfile profile = new UserProfile();
        assertFalse(profile.isRemembered());
        profile.setRemembered(true);
        assertTrue(profile.isRemembered());
    }
}
