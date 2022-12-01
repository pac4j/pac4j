package org.pac4j.core.authorization.generator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests {@link DefaultRolesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultRolesAuthorizationGeneratorTests {

    private final static String[] DEFAULT_ROLES_ARRAY = new String[]{"R1", "R2"};
    private final static List<String> DEFAULT_ROLES_LIST = Arrays.asList(DEFAULT_ROLES_ARRAY);

    @Test
    public void testNullArrays() {
        val generator = new DefaultRolesAuthorizationGenerator((String[]) null);
        checkEmptyProfile(generator);
    }

    private void checkEmptyProfile(final DefaultRolesAuthorizationGenerator generator) {
        val profile = new CommonProfile();
        generator.generate(null, null, profile);
        assertEquals(0, profile.getRoles().size());
    }

    @Test
    public void testNullLists() {
        val generator = new DefaultRolesAuthorizationGenerator((List<String>) null);
        checkEmptyProfile(generator);
    }

    @Test
    public void testDefaultValuesArrays() {
        val generator = new DefaultRolesAuthorizationGenerator(DEFAULT_ROLES_ARRAY);
        checkProfile(generator);
    }

    private void checkProfile(final DefaultRolesAuthorizationGenerator generator) {
        val profile = new CommonProfile();
        generator.generate(null, null, profile);
        val roles = profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.containsAll(DEFAULT_ROLES_LIST));
    }

    @Test
    public void testDefaultValuesLists() {
        val generator = new DefaultRolesAuthorizationGenerator(DEFAULT_ROLES_LIST);
        checkProfile(generator);
    }
}
