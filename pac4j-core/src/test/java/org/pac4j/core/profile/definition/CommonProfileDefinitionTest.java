package org.pac4j.core.profile.definition;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link CommonProfileDefinition}.
 *
 * @author Jerome LELEU
 * @since 5.0.0
 */
public class CommonProfileDefinitionTest {

    @Test
    public void testNewProfile() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        val profile = definition.newProfile();
        assertTrue(profile instanceof CommonProfile);
    }

    @Test
    public void testRestoreProfile() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        val profile = definition.newProfile(BasicUserProfile.class.getName() + "#");
        assertFalse(profile instanceof CommonProfile);
        assertTrue(profile instanceof BasicUserProfile);
    }

    @Test
    public void testRestoreProfileNoSeparator() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        val profile = definition.newProfile(BasicUserProfile.class.getName());
        assertTrue(profile instanceof CommonProfile);
    }

    @Test
    public void testRestoreProfileBadType() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        val profile = definition.newProfile(String.class.getName() + "#");
        assertTrue(profile instanceof CommonProfile);
    }

    @Test
    public void testAttributesMapping() {
        val def = new CommonProfileDefinition();
        def.getAttributesMapper().put("firstName", "prenom");
        val profile = new CommonProfile();
        def.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, "firstName", "Jerome");
        assertEquals("Jerome", profile.getAttribute("prenom"));
        assertNull(profile.getAttribute("firstName"));
    }

    @Test
    public void testNoAttributesMapping() {
        val def = new CommonProfileDefinition();
        def.setAttributesMapper(null);
        val profile = new CommonProfile();
        def.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, "firstName", "Jerome");
        assertEquals("Jerome", profile.getAttribute("firstName"));
        assertNull(profile.getAttribute("prenom"));
    }
}
