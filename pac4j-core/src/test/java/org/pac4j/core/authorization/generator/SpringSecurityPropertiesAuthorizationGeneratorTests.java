package org.pac4j.core.authorization.generator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;

import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests {@link SpringSecurityPropertiesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class SpringSecurityPropertiesAuthorizationGeneratorTests implements TestsConstants {

    private final static String SEPARATOR = ",";
    private final static String ROLE2 = "role2";

    private Set<String> test(final String value) {
        val properties = new Properties();
        properties.put(USERNAME, PASSWORD + value);
        val generator = new SpringSecurityPropertiesAuthorizationGenerator(properties);
        val profile = new CommonProfile();
        profile.setId(USERNAME);
        generator.generate(null, null, profile);
        return profile.getRoles();
    }

    @Test
    public void testOnlyPassword() {
        val roles = test(Pac4jConstants.EMPTY_STRING);
        assertEquals(0, roles.size());
    }

    @Test
    public void testEnabled() {
        val roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testDisabled() {
        val roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testOneRole() {
        val roles = test(SEPARATOR + ROLE);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(ROLE));
    }

    @Test
    public void testOneRoleEnabled() {
        val roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(ROLE));
    }

    @Test
    public void testOneRoleDisabled() {
        val roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testTwoRoles() {
        val roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(ROLE));
        assertTrue(roles.contains(ROLE2));
    }

    @Test
    public void testTwoRolesEnabled() {
        val roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR
            + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(ROLE));
        assertTrue(roles.contains(ROLE2));
    }

    @Test
    public void testTwoRolesDisabled() {
        val roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR
            + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }
}
