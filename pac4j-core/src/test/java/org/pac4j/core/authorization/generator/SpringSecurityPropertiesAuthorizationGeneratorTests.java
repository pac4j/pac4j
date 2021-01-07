package org.pac4j.core.authorization.generator;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

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
        final Properties properties = new Properties();
        properties.put(USERNAME, PASSWORD + value);
        final SpringSecurityPropertiesAuthorizationGenerator generator = new SpringSecurityPropertiesAuthorizationGenerator(properties);
        final CommonProfile profile = new CommonProfile();
        profile.setId(USERNAME);
        generator.generate(null, null, profile);
        return profile.getRoles();
    }

    @Test
    public void testOnlyPassword() {
        final Set<String> roles = test("");
        assertEquals(0, roles.size());
    }

    @Test
    public void testEnabled() {
        final Set<String> roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testDisabled() {
        final Set<String> roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testOneRole() {
        final Set<String> roles = test(SEPARATOR + ROLE);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(ROLE));
    }

    @Test
    public void testOneRoleEnabled() {
        final Set<String> roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(1, roles.size());
        assertTrue(roles.contains(ROLE));
    }

    @Test
    public void testOneRoleDisabled() {
        final Set<String> roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testTwoRoles() {
        final Set<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(ROLE));
        assertTrue(roles.contains(ROLE2));
    }

    @Test
    public void testTwoRolesEnabled() {
        final Set<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR
            + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(ROLE));
        assertTrue(roles.contains(ROLE2));
    }

    @Test
    public void testTwoRolesDisabled() {
        final Set<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR
            + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }
}
