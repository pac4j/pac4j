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
