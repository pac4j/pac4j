/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.profile;

import junit.framework.TestCase;

import org.scribe.up.profile.UserProfile;

/**
 * This class tests the UserProfile class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestUserProfile extends TestCase {
    
    private static final String ID = "id";
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    public void testId() {
        UserProfile userProfile = new UserProfile();
        assertNull(userProfile.getId());
        userProfile.setId(ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testIdInConstructor() {
        UserProfile userProfile = new UserProfile(ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testAttributes() {
        UserProfile userProfile = new UserProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.getAttributes().put(KEY, VALUE);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
}
