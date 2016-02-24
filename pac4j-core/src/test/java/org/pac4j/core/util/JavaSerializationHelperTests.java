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
