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
package org.pac4j.core.profile;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link ProfileHelper}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileHelperTests implements TestsConstants {

    @Test
    public void testIsTypedIdOf() {
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, UserProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(null, UserProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, null));
        assertTrue(ProfileHelper.isTypedIdOf("UserProfile" + UserProfile.SEPARATOR, UserProfile.class));
        assertTrue(ProfileHelper.isTypedIdOf("org.pac4j.core.profile.UserProfile" + UserProfile.SEPARATOR, UserProfile.class));
    }

    @Test
    public void testBuildProfile() {
        final UserProfile profile = new UserProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final UserProfile profile2 = ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile2.getId());
        final Map<String, Object> attributes = profile2.getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(VALUE, attributes.get(NAME));
        final UserProfile profile3 = ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile3.getId());
    }
}
