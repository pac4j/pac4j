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
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(null, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, null));
        assertTrue(ProfileHelper.isTypedIdOf("CommonProfile" + CommonProfile.SEPARATOR, CommonProfile.class));
        assertTrue(ProfileHelper.isTypedIdOf("org.pac4j.core.profile.CommonProfile" + CommonProfile.SEPARATOR, CommonProfile.class));
    }

    @Test
    public void testBuildProfile() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final CommonProfile profile2 = ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile2.getId());
        final Map<String, Object> attributes = profile2.getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(VALUE, attributes.get(NAME));
        final CommonProfile profile3 = ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile3.getId());
    }
    
    @Test
    public void testBuildUserProfileByClassCompleteName() {
    	try {
	        final CommonProfile profile = new CommonProfile();
	        profile.setId(ID);
	        profile.addAttribute(NAME, VALUE);
	        final CommonProfile profile2 = ProfileHelper.buildUserProfileByClassCompleteName(CommonProfile.class.getName());
            assertNotNull(profile2);
	        final CommonProfile profile3 = ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
	        assertEquals(ID, profile3.getId());
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }
}
