package org.pac4j.core.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link CommonProfile} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class CommonProfileTests implements TestsConstants {

    private static final String ID = "id";

    private static final String ROLE1 = "role1";
    private static final String PERMISSION = "onePermission";

    @Test
    public void testSetId() {
        final CommonProfile userProfile = new CommonProfile();
        assertNull(userProfile.getId());
        userProfile.setId(ID);
        assertEquals(ID, userProfile.getId());
    }

    @Test
    public void testAddAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(KEY, VALUE);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }

    @Test
    public void testAddAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(KEY, VALUE);
        final CommonProfile userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttributes(attributes);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }

    @Test
    public void testUnsafeAddAttribute() {
        final CommonProfile userProfile = new CommonProfile();
        try {
            userProfile.getAttributes().put(KEY, VALUE);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNull(e.getMessage());
        }
    }

    @Test
    public void testRoles() {
        final CommonProfile profile = new CommonProfile();
        assertEquals(0, profile.getRoles().size());
        profile.addRole(ROLE1);
        assertEquals(1, profile.getRoles().size());
        assertTrue(profile.getRoles().contains(ROLE1));
    }

    @Test
    public void testPermissions() {
        final CommonProfile profile = new CommonProfile();
        assertEquals(0, profile.getPermissions().size());
        profile.addPermission(PERMISSION);
        assertEquals(1, profile.getPermissions().size());
        assertTrue(profile.getPermissions().contains(PERMISSION));
    }

    @Test
    public void testRme() {
        final CommonProfile profile = new CommonProfile();
        assertFalse(profile.isRemembered());
        profile.setRemembered(true);
        assertTrue(profile.isRemembered());
    }

    @Test
    public void testTypeId() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        assertEquals("org.pac4j.core.profile.CommonProfile" + CommonProfile.SEPARATOR + ID, profile.getTypedId());
    }

    @Test
    public void testNullId() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.setId(null), TechnicalException.class, "id cannot be null");
    }

    @Test
    public void testNullClientName() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.setClientName(null), TechnicalException.class, "clientName cannot be null");
    }

    @Test
    public void testBlankRole() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRole(""), TechnicalException.class, "role cannot be blank");
    }

    @Test
    public void testNullRoles() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRoles((Set<String>) null), TechnicalException.class, "roles cannot be null");
    }

    @Test
    public void testBlankPermission() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addPermission(""), TechnicalException.class, "permission cannot be blank");
    }

    @Test
    public void testNullPermissions() {
        final CommonProfile profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addPermissions((Set<String>) null), TechnicalException.class, "permissions cannot be null");
    }

    @Test
    public void serializeProfile() {
        final JavaSerializationHelper helper = new JavaSerializationHelper();
        final CommonProfile profile = new CommonProfile();
        final String s = helper.serializeToBase64(profile);
        final CommonProfile profile2 = (CommonProfile) helper.unserializeFromBase64(s);
        assertNotNull(profile2);
    }

    @Test
    public void stringifyProfile() {
        try {
            ProfileHelper.getInternalAttributeHandler().setStringify(true);
            final CommonProfile profile = new CommonProfile();
            profile.setId(ID);
            profile.addAttribute(KEY, true);
            profile.addAttribute(NAME, 1);
            assertEquals(true, profile.getAttribute(KEY));
            assertEquals(1, profile.getAttributes().get(NAME));
        } finally {
            ProfileHelper.getInternalAttributeHandler().setStringify(false);
        }
    }
}
