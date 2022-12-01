package org.pac4j.core.profile;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.util.serializer.JavaSerializer;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the {@link CommonProfile} class.
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class CommonProfileTests implements TestsConstants {

    private static final String ID = "id";

    private static final String ROLE1 = "role1";

    @Test
    public void testSetId() {
        val userProfile = new CommonProfile();
        assertNull(userProfile.getId());
        userProfile.setId(ID);
        assertEquals(ID, userProfile.getId());
    }

    @Test
    public void testAddAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(KEY, VALUE);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }

    @Test
    public void testGenderAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.GENDER, Gender.MALE);
        assertEquals(Gender.MALE, userProfile.getGender());
    }

    @Test
    public void testInvalidGenderAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.GENDER, "invalid");
        assertEquals(Gender.UNSPECIFIED, userProfile.getGender());
    }

    @Test
    public void testLocaleAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.LOCALE, Locale.US);
        assertEquals(Locale.US, userProfile.getLocale());
    }

    @Test
    public void testInvalidLocaleAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.LOCALE, "invalid");
        assertNull(userProfile.getLocale());
    }

    @Test
    public void testPictureURLAttribute() throws URISyntaxException {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        java.net.URI pictureUri = new java.net.URI("http://example.com/picture");
        userProfile.addAttribute(CommonProfileDefinition.PICTURE_URL, pictureUri);
        assertEquals(pictureUri, userProfile.getPictureUrl());
    }

    @Test
    public void testInvalidPictureURLAttribute() throws URISyntaxException {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.PICTURE_URL, "invalid");
        assertNull(userProfile.getPictureUrl());
    }

    @Test
    public void testProfileURLAttribute() throws URISyntaxException {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        java.net.URI profileUri = new java.net.URI("http://example.com/picture");
        userProfile.addAttribute(CommonProfileDefinition.PROFILE_URL, profileUri);
        assertEquals(profileUri, userProfile.getProfileUrl());
    }

    @Test
    public void testInvalidProfileURLAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(CommonProfileDefinition.PROFILE_URL, "invalid");
        assertNull(userProfile.getProfileUrl());
    }

    @Test
    public void testUsernameAttribute() throws URISyntaxException {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(Pac4jConstants.USERNAME, "username");
        assertEquals("username", userProfile.getUsername());
    }

    @Test
    public void testInvalidUsernameAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttribute(Pac4jConstants.USERNAME, 1);
        assertEquals("1", userProfile.getUsername());
    }

    @Test
    public void testAddAttributeMultipleValues() {
        val userProfile = new CommonProfile(true);
        userProfile.addAttribute(KEY, Arrays.asList("Value1"));
        userProfile.addAttribute(KEY, Arrays.asList("Value2", "Value3"));
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(Arrays.asList("Value1", "Value2", "Value3"), userProfile.getAttribute(KEY));
    }

    @Test
    public void testAddAttributeMultipleValuesOldBehaviour() {
        val userProfile = new CommonProfile(false);
        userProfile.addAttribute(KEY, Arrays.asList("Value1"));
        userProfile.addAttribute(KEY, Arrays.asList("Value2", "Value3"));
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(Arrays.asList("Value2", "Value3"), userProfile.getAttribute(KEY));
    }

    @Test
    public void testAddAuthenticationAttribute() {
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAuthenticationAttributes().size());
        userProfile.addAuthenticationAttribute(KEY, VALUE);
        assertEquals(1, userProfile.getAuthenticationAttributes().size());
        assertEquals(VALUE, userProfile.getAuthenticationAttributes().get(KEY));
    }

    @Test
    public void testAddAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(KEY, VALUE);
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAttributes().size());
        userProfile.addAttributes(attributes);
        assertEquals(1, userProfile.getAttributes().size());
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }

    @Test
    public void testAddAuthenticationAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(KEY, VALUE);
        val userProfile = new CommonProfile();
        assertEquals(0, userProfile.getAuthenticationAttributes().size());
        userProfile.addAuthenticationAttributes(attributes);
        assertEquals(1, userProfile.getAuthenticationAttributes().size());
        assertEquals(VALUE, userProfile.getAuthenticationAttributes().get(KEY));
    }

    @Test
    public void testUnsafeAddAttribute() throws UnsupportedOperationException {
        val userProfile = new CommonProfile();
        userProfile.getAttributes().put(KEY, VALUE);
    }

    @Test
    public void testUnsafeAddAuthenticationAttribute() throws UnsupportedOperationException {
        val userProfile = new CommonProfile();
        userProfile.getAuthenticationAttributes().put(KEY, VALUE);
    }

    @Test
    public void testRoles() {
        val profile = new CommonProfile();
        assertEquals(0, profile.getRoles().size());
        profile.addRole(ROLE1);
        assertEquals(1, profile.getRoles().size());
        assertTrue(profile.getRoles().contains(ROLE1));
    }

    @Test
    public void testRme() {
        val profile = new CommonProfile();
        assertFalse(profile.isRemembered());
        profile.setRemembered(true);
        assertTrue(profile.isRemembered());
    }

    @Test
    public void testTypeId() {
        val profile = new CommonProfile();
        profile.setId(ID);
        assertEquals("org.pac4j.core.profile.CommonProfile" + Pac4jConstants.TYPED_ID_SEPARATOR + ID, profile.getTypedId());
    }

    @Test
    public void testNullId() {
        val profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.setId(null), TechnicalException.class, "id cannot be blank");
    }

    @Test
    public void testBlankRole() {
        val profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRole(Pac4jConstants.EMPTY_STRING), TechnicalException.class, "role cannot be blank");
    }

    @Test
    public void testNullRoles() {
        val profile = new CommonProfile();
        TestsHelper.expectException(() -> profile.addRoles(null), TechnicalException.class, "roles cannot be null");
    }

    @Test
    public void serializeProfile() {
        val helper = new JavaSerializer();
        val profile = new CommonProfile();
        val s = helper.serializeToString(profile);
        val profile2 = (CommonProfile)helper.deserializeFromString(s);
        assertNotNull(profile2);
    }

    @Test
    public void testSetNullLinkedIdWhenAlreadySet() {
        val profile = new CommonProfile();
        profile.setLinkedId("dummyLinkecId");
        profile.setLinkedId(null);
        assertNull(profile.getLinkedId());
    }

    @Test
    public void testSetNullLinkedIdWhenNotAlreadySet() {
        val profile = new CommonProfile();
        profile.setLinkedId(null);
        assertNull(profile.getLinkedId());
    }
}
