package org.pac4j.core.authorization.generator;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        val generator = new FromAttributesAuthorizationGenerator(new ArrayList<>());
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
    }

    @Test
    public void testNoConfig() {
        val generator =
                new FromAttributesAuthorizationGenerator((String[]) null);
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
    }

    @Test
    public void testRole() {
        val roleAttributes = new String[] {
            ATTRIB1
        };
        val generator = new FromAttributesAuthorizationGenerator(roleAttributes);
        generator.generate(null, this.profile);
        val roles = this.profile.getRoles();
        assertEquals(2, roles.size());
        assertTrue(roles.contains("info11"));
        assertTrue(roles.contains("info12"));
    }

    @Test
    public void testNoRole() {
        val roleAttributes = new String[] {
            ATTRIB5
        };
        val generator = new FromAttributesAuthorizationGenerator(roleAttributes);
        generator.generate(null, this.profile);
        assertEquals(0, this.profile.getRoles().size());
    }

    @Test
    public void testRoleChangeSplit() {
        val roleAttributes = new String[] {
            ATTRIB1
        };
        val generator = new FromAttributesAuthorizationGenerator(roleAttributes);
        generator.setSplitChar("|");
        generator.generate(null, this.profile);
        val roles = this.profile.getRoles();
        assertEquals(1, roles.size());
        assertTrue(roles.contains(VALUE1));
    }

    @Test
    public void testListRoles() {
        val roleAttributes = new String[] {
                ATTRIB3, ATTRIB4
        };
        val generator = new FromAttributesAuthorizationGenerator(roleAttributes);
        generator.generate(null, this.profile);
        val roles = this.profile.getRoles();
        assertEquals(ATTRIB_ARRAY.length + ATTRIB_LIST.size(), roles.size());
        for(var value : ATTRIB_ARRAY) {
            assertTrue(roles.contains(value));
        }
        for(var value : ATTRIB_LIST) {
            assertTrue(roles.contains(value));
        }
    }
}
