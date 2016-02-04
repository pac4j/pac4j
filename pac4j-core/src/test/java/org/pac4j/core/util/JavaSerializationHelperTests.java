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
package org.pac4j.core.util;

import org.junit.Test;
import org.pac4j.core.profile.UserProfile;

import static org.junit.Assert.*;

/**
 * Tests {@link JavaSerializationHelper}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class JavaSerializationHelperTests implements TestsConstants {

    private JavaSerializationHelper helper = new JavaSerializationHelper();

    private UserProfile getUserProfile() {
        final UserProfile profile = new UserProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        return profile;
    }

    @Test
    public void testBytesSerialization() {
        final UserProfile profile = getUserProfile();
        final byte[] serialized = helper.serializeToBytes(profile);
        final UserProfile profile2 = (UserProfile) helper.unserializeFromBytes(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }

    @Test
    public void testBase64StringSerialization() {
        final UserProfile profile = getUserProfile();
        final String serialized = helper.serializeToBase64(profile);
        final UserProfile profile2 = (UserProfile) helper.unserializeFromBase64(serialized);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttribute(NAME), profile2.getAttribute(NAME));
    }
}
