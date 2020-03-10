package org.pac4j.core.authorization.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;


import static org.junit.Assert.*;

/**
 * This class tests {@link FromAttributesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public final class FromAttributesAuthorizationGeneratorTests {

    private static final String ATTRIB1 = "attrib1";
    private static final String VALUE1 = "info11,info12";
    private static final String ATTRIB2 = "attrib2";
    private static final String VALUE2 = "info21,info22";
    private static final String ATTRIB3 = "attrib3";
    private static final String ATTRIB4 = "attrib4";
    private static final String ATTRIB5 = "attrib5";
    private static final String[] ATTRIB_ARRAY = new String[]{"infoA1", "infoA2", "infoA3"};
    private static final List<String> ATTRIB_LIST = new ArrayList<>();

    static {
        ATTRIB_LIST.add("infoL1");
        ATTRIB_LIST.add("infoL2");
        ATTRIB_LIST.add("infoL3");
    }

    private CommonProfile profile;

    @Before
    public void setUp() {
        this.profile = new CommonProfile();
        this.profile.addAttribute(ATTRIB1, VALUE1);
        this.profile.addAttribute(ATTRIB2, VALUE2);
        this.profile.addAttribute(ATTRIB3, ATTRIB_ARRAY);
        this.profile.addAttribute(ATTRIB4, ATTRIB_LIST);
    }

    @Test
    public void testNoConfigWithCollections() {
        final FromAttributesAuthorizationGenerator generator =
                new FromAttributesAuthorizationGenerator(new ArrayList<>(), new HashSet<>());
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
        assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testNoConfig() {
        final FromAttributesAuthorizationGenerator generator =
                new FromAttributesAuthorizationGenerator(null, (String[]) null);
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
        assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testRolePermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator generator =
            new FromAttributesAuthorizationGenerator(roleAttributes,permissionAttributes);
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.contains("info11"));
        assertTrue(roles.contains("info12"));
        final Set<String> permissions = this.profile.getPermissions();
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("info21"));
        assertTrue(permissions.contains("info22"));
    }

    @Test
    public void testNoRolePermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB5
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator generator =
            new FromAttributesAuthorizationGenerator(roleAttributes,permissionAttributes);
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
        final Set<String> permissions = this.profile.getPermissions();
        assertEquals(2, permissions.size());
        assertTrue(permissions.contains("info21"));
        assertTrue(permissions.contains("info22"));
    }

    @Test
    public void testRoleNoPermission() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB5
        };
        final FromAttributesAuthorizationGenerator generator =
            new FromAttributesAuthorizationGenerator(roleAttributes,permissionAttributes);
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.contains("info11"));
        assertTrue(roles.contains("info12"));
        assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testRolePermissionChangeSplit() {
        final String[] roleAttributes = new String[] {
            ATTRIB1
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator generator =
            new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.setSplitChar("|");
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.contains(VALUE1));
        final Set<String> permissions = this.profile.getPermissions();
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains(VALUE2));
    }

    @Test
    public void testListRolesPermissions() {
        final String[] roleAttributes = new String[] {
                ATTRIB3, ATTRIB4
        };
        final String[] permissionAttributes = new String[] {
                ATTRIB3, ATTRIB4
        };

        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(
                roleAttributes,
                permissionAttributes);

        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        assertEquals(ATTRIB_ARRAY.length + ATTRIB_LIST.size(), roles.size());
        for(final String value : ATTRIB_ARRAY) {
            assertTrue(roles.contains(value));
        }
        for(final String value : ATTRIB_LIST) {
            assertTrue(roles.contains(value));
        }
        final Set<String> permissions = this.profile.getPermissions();
        assertEquals(ATTRIB_ARRAY.length + ATTRIB_LIST.size(), roles.size());
        for(final String value : ATTRIB_ARRAY) {
            assertTrue(permissions.contains(value));
        }
        for(final String value : ATTRIB_LIST) {
            assertTrue(permissions.contains(value));
        }
    }
}
