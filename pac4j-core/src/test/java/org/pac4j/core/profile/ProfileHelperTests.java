package org.pac4j.core.profile;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;

import java.util.Arrays;
import java.util.List;

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
        assertTrue(ProfileHelper.isTypedIdOf("org.pac4j.core.profile.CommonProfile" + Pac4jConstants.TYPED_ID_SEPARATOR,
            CommonProfile.class));
    }

    @Test
    public void testBuildUserProfileByClassCompleteName() {
        UserProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        val profile2 = ProfileHelper.buildUserProfileByClassCompleteName(CommonProfile.class.getName());
        assertNotNull(profile2);
    }

    @Test
    public void testSanitizeNullIdentifier() {
        assertNull(ProfileHelper.sanitizeIdentifier(null));
    }

    @Test
    public void testSanitizeNullProfile() {
        assertEquals("123", ProfileHelper.sanitizeIdentifier(123));
    }

    @Test
    public void testSanitize() {
        assertEquals("yes", ProfileHelper.sanitizeIdentifier("org.pac4j.core.profile.CommonProfile#yes"));
    }

    @Test
    public void testSanitize2() {
        assertEquals("yes", ProfileHelper.sanitizeIdentifier("org.pac4j.core.profile.fake.FakeProfile#yes"));
    }

    @Test
    public void testFlatIntoOneProfileOneAnonymousProfile() {
        final List<CommonProfile> profiles = List.of(AnonymousProfile.INSTANCE);
        assertEquals(AnonymousProfile.INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNAnonymousProfiles() {
        final List<CommonProfile> profiles = Arrays.asList( null, AnonymousProfile.INSTANCE, null, AnonymousProfile.INSTANCE );
        assertEquals(AnonymousProfile.INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileOneProfile() {
        val profile1 = new CommonProfile();
        profile1.setId("ONE");
        val profiles = List.of(profile1);
        assertEquals(profile1, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNProfiles() {
        val profile2 = new CommonProfile();
        profile2.setId("TWO");
        val profiles = Arrays.asList( AnonymousProfile.INSTANCE, null, profile2 );
        assertEquals(profile2, ProfileHelper.flatIntoOneProfile(profiles).get());
    }
}
