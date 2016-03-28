package org.pac4j.core.authorization.generator;

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
    
    private final static String ATTRIB1 = "attrib1";
    private final static String VALUE1 = "info11,info12";
    private final static String ATTRIB2 = "attrib2";
    private final static String VALUE2 = "info21,info22";
    private final static String ATTRIB3 = "attrib3";
    
    private CommonProfile profile;
    
    @Before
    public void setUp() {
        this.profile = new CommonProfile();
        this.profile.addAttribute(ATTRIB1, VALUE1);
        this.profile.addAttribute(ATTRIB2, VALUE2);
    }

    @Test
    public void testNoConfig() {
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<>(null, null);
        generator.generate(this.profile);
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
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
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
            ATTRIB3
        };
        final String[] permissionAttributes = new String[] {
            ATTRIB2
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
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
            ATTRIB3
        };
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.generate(this.profile);
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
        final FromAttributesAuthorizationGenerator<CommonProfile> generator = new FromAttributesAuthorizationGenerator<CommonProfile>(
                                                                                                                                      roleAttributes,
                                                                                                                                      permissionAttributes);
        generator.setSplitChar("|");
        generator.generate(this.profile);
        final Set<String> roles = this.profile.getRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.contains(VALUE1));
        final Set<String> permissions = this.profile.getPermissions();
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains(VALUE2));
    }
}
