package org.pac4j.core.profile;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

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
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(null, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, null));
        assertTrue(ProfileHelper.isTypedIdOf("org.pac4j.core.profile.CommonProfile" + CommonProfile.SEPARATOR, CommonProfile.class));
    }

    @Test
    public void testBuildUserProfileByClassCompleteName() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final CommonProfile profile2 = ProfileHelper.buildUserProfileByClassCompleteName(CommonProfile.class.getName());
        assertNotNull(profile2);
    }

    @Test
    public void testSanitizeNullIdentifier() {
        assertNull(ProfileHelper.sanitizeIdentifier(new CommonProfile(), null));
    }

    @Test
    public void testSanitizeNullProfile() {
        assertEquals("123", ProfileHelper.sanitizeIdentifier(null, 123));
    }

    @Test
    public void testSanitize() {
        assertEquals("yes", ProfileHelper.sanitizeIdentifier(new CommonProfile(), "org.pac4j.core.profile.CommonProfile#yes"));
    }
}
