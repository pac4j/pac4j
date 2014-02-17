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
 * This class tests the {@link ProfileHelper} class for the appropriate profile.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class TestProfileHelper extends TestCase implements TestsConstants {
    
    protected static final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();
    
    protected abstract Class<? extends CommonProfile> getProfileClass();
    
    protected abstract String getProfileType();
    
    public void testIsTypedIdOfNullId() {
        assertFalse(ProfileHelper.isTypedIdOf(null, getProfileClass()));
    }
    
    public void testIsTypedIdOfWrongClass() {
        assertFalse(ProfileHelper.isTypedIdOf(getProfileType() + "#" + STRING_ID, CommonProfile.class));
    }
    
    public void testIsTypedIdOfRightClass() {
        assertTrue(ProfileHelper.isTypedIdOf(getProfileType() + "#" + STRING_ID, getProfileClass()));
    }
    
    public void testBuildProfileWrongFormat() {
        assertNull(ProfileHelper.buildProfile(getProfileType() + "*" + STRING_ID, null));
    }
    
    public void testBuildProfileWrongProfileClass() {
        assertNull(ProfileHelper.buildProfile(getProfileType() + "2#" + STRING_ID, null));
    }
    
    public void testBuildProfileNoAttribute() {
        assertNotNull(ProfileHelper.buildProfile(getProfileType() + "#" + STRING_ID, EMPTY_MAP));
    }
    
    protected abstract String getAttributeName();
    
    public void testBuildProfileOK() {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(getAttributeName(), VALUE);
        final UserProfile userProfile = ProfileHelper.buildProfile(getProfileType() + "#" + STRING_ID, attributes);
        assertNotNull(userProfile);
        assertEquals(STRING_ID, userProfile.getId());
        assertEquals(getProfileType() + "#" + STRING_ID, userProfile.getTypedId());
        final Map<String, Object> attributesProfile = userProfile.getAttributes();
        assertEquals(1, attributesProfile.size());
        assertEquals(VALUE, attributesProfile.get(getAttributeName()));
    }
}
