package org.pac4j.core.authorization.generator;

import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * This class tests {@link SpringSecurityPropertiesAuthorizationGenerator}.
 * 
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class SpringSecurityPropertiesAuthorizationGeneratorTests implements TestsConstants {

    private final static String SEPARATOR = ",";
    private final static String ROLE2 = "role2";

    private List<String> test(final String value) {
        final Properties properties = new Properties();
        properties.put(USERNAME, PASSWORD + value);
        final SpringSecurityPropertiesAuthorizationGenerator generator = new SpringSecurityPropertiesAuthorizationGenerator(properties);
        final CommonProfile profile = new CommonProfile();
        profile.setId(USERNAME);
        generator.generate(profile);
        return profile.getRoles();
    }

    @Test
    public void testOnlyPassword() {
        final List<String> roles = test("");
        assertEquals(0, roles.size());
    }

    @Test
    public void testEnabled() {
        final List<String> roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testDisabled() {
        final List<String> roles = test(SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testOneRole() {
        final List<String> roles = test(SEPARATOR + ROLE);
        assertEquals(1, roles.size());
        assertEquals(ROLE, roles.get(0));
    }

    @Test
    public void testOneRoleEnabled() {
        final List<String> roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(1, roles.size());
        assertEquals(ROLE, roles.get(0));
    }

    @Test
    public void testOneRoleDisabled() {
        final List<String> roles = test(SEPARATOR + ROLE + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }

    @Test
    public void testTwoRoles() {
        final List<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2);
        assertEquals(2, roles.size());
        assertEquals(ROLE, roles.get(0));
        assertEquals(ROLE2, roles.get(1));
    }

    @Test
    public void testTwoRolesEnabled() {
        final List<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.ENABLED);
        assertEquals(2, roles.size());
        assertEquals(ROLE, roles.get(0));
        assertEquals(ROLE2, roles.get(1));
    }

    @Test
    public void testTwoRolesDisabled() {
        final List<String> roles = test(SEPARATOR + ROLE + SEPARATOR + ROLE2 + SEPARATOR + SpringSecurityPropertiesAuthorizationGenerator.DISABLED);
        assertEquals(0, roles.size());
    }
}
