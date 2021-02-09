package org.pac4j.core.profile.definition;

import org.junit.Test;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.*;

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
        final var profile = definition.newProfile();
        assertTrue(profile instanceof CommonProfile);
    }

    @Test
    public void testRestoreProfile() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        final var profile = definition.newProfile(BasicUserProfile.class.getName() + "#");
        assertFalse(profile instanceof CommonProfile);
        assertTrue(profile instanceof BasicUserProfile);
    }

    @Test
    public void testRestoreProfileNoSeparator() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        final var profile = definition.newProfile(BasicUserProfile.class.getName());
        assertTrue(profile instanceof CommonProfile);
    }

    @Test
    public void testRestoreProfileBadType() {
        final ProfileDefinition definition = new CommonProfileDefinition();
        definition.setRestoreProfileFromTypedId(true);
        final var profile = definition.newProfile(String.class.getName() + "#");
        assertTrue(profile instanceof CommonProfile);
    }
}
