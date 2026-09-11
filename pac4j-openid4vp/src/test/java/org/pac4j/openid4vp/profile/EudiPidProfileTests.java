package org.pac4j.openid4vp.profile;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.profile.AttributeLocation;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.profile.EudiPidProfileDefinition.*;

/**
 * Tests {@link EudiPidProfile}: the PID definition builds it, and its accessors give the converted values.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class EudiPidProfileTests {

    @Test
    void testTheDefinitionBuildsAPidProfile() {
        assertInstanceOf(EudiPidProfile.class, new EudiPidProfileDefinition().newProfile());
    }

    @Test
    void testTheAccessorsReturnTheConvertedAttributes() {
        val definition = new EudiPidProfileDefinition();
        val profile = (EudiPidProfile) definition.newProfile();
        // the values arrive as strings and the definition converts them
        definition.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, GIVEN_NAME, "Jeanne");
        definition.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, AGE_OVER_18, "true");
        definition.convertAndAdd(profile, AttributeLocation.PROFILE_ATTRIBUTE, AGE_IN_YEARS, "34");

        assertEquals("Jeanne", profile.getGivenName());
        assertEquals(Boolean.TRUE, profile.isAgeOver18());
        assertEquals(34, profile.getAgeInYears());
        // an attribute the holder did not disclose
        assertNull(profile.getNationality());
    }
}
