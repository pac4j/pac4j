package org.pac4j.core.profile;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests {@link ProfileHelper}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileHelperTests implements TestsConstants {

    // Examples of attribute keys, borrowed from the SAML module to have something realistic.
    private static final String SAML_CONDITION_NOT_BEFORE_ATTRIBUTE = "notBefore";
    private static final String SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE = "notOnOrAfter";
    private static final String SESSION_INDEX = "sessionindex";
    private static final String ISSUER_ID = "issuerId";
    private static final String AUTHN_CONTEXT = "authnContext";
    private static final String SAML_NAME_ID_FORMAT = "samlNameIdFormat";
    private static final String SAML_NAME_ID_NAME_QUALIFIER = "samlNameIdNameQualifier";
    private static final String SAML_NAME_ID_SP_NAME_QUALIFIER = "samlNameIdSpNameQualifier";
    private static final String SAML_NAME_ID_SP_PROVIDED_ID = "samlNameIdSpProvidedId";

    private LocalDateTime notBeforeTime;
    private LocalDateTime notOnOrAfterTime;

    @Before
    public void setUpTestData() {
        notBeforeTime = LocalDateTime.now();
        notOnOrAfterTime = notBeforeTime.plusHours(1L);
    }

    @Test
    public void testIsTypedIdOf() {
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(null, CommonProfile.class));
        assertFalse(ProfileHelper.isTypedIdOf(VALUE, null));
        assertTrue(ProfileHelper.isTypedIdOf("org.pac4j.core.profile.CommonProfile" + CommonProfile.SEPARATOR, CommonProfile.class));
    }

    @Test
    public void testBuildUserProfileByClassCompleteName() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final CommonProfile profile2 = ProfileHelper.buildUserProfileByClassCompleteName(CommonProfile.class.getName());
        assertNotNull(profile2);
    }

    @Test
    public void testSanitizeNullIdentifier() {
        assertNull(ProfileHelper.sanitizeIdentifier(new CommonProfile(), null));
    }

    @Test
    public void testSanitizeNullProfile() {
        assertEquals("123", ProfileHelper.sanitizeIdentifier(null, 123));
    }

    @Test
    public void testSanitize() {
        assertEquals("yes", ProfileHelper.sanitizeIdentifier(new CommonProfile(), "org.pac4j.core.profile.CommonProfile#yes"));
    }

    /**
     * Tests {@link ProfileHelper#restoreOrBuildProfile(ProfileDefinition, String, Map, Map, Object...)} when the profile is created using
     * its constructor. The Typed ID contains a separator.
     */
    @Test
    public void testProfileRestoreFromClassName() {
        final String typedId = CommonProfile.class.getName() + CommonProfile.SEPARATOR + ID;
        final CommonProfile restoredProfile = profileRestoreMustBringBackAllAttributes(typedId);

        assertNotNull(restoredProfile);
        assertEquals(ID, restoredProfile.getId());
        assertEquals("a@b.cc", restoredProfile.getEmail());
        assertEquals("John", restoredProfile.getFirstName());
        assertEquals("Doe", restoredProfile.getFamilyName());
        assertEquals(Gender.UNSPECIFIED, restoredProfile.getGender()); // Because it was not set
        assertNull(restoredProfile.getDisplayName()); // Was not set either

        assertEquals("12345-67890", restoredProfile.getAttribute(SESSION_INDEX));
        assertEquals(notBeforeTime, restoredProfile.getAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        assertEquals(notOnOrAfterTime, restoredProfile.getAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));

        assertEquals("IssuerId", restoredProfile.getAuthenticationAttribute(ISSUER_ID));
        final List<String> context = (List<String>) restoredProfile.getAuthenticationAttribute(AUTHN_CONTEXT);
        assertThat(context, CoreMatchers.hasItem("ContextItem1"));
        assertThat(context, CoreMatchers.hasItem("ContextItem2"));
        assertEquals("NameIdFormat", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_FORMAT));
        assertEquals("NameIdNameQualifier", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER));
        assertEquals("NameIdSpNameQualifier", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER));
        assertEquals("NameIdSpProvidedId", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID));
        assertEquals(notBeforeTime, restoredProfile.getAuthenticationAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        assertEquals(notOnOrAfterTime, restoredProfile.getAuthenticationAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
    }

    /**
     * Tests {@link ProfileHelper#restoreOrBuildProfile(ProfileDefinition, String, Map, Map, Object...)} when the profile is created using
     * through the profile definition's create function. The Typed ID does not contain a separator.
     */
    @Test
    public void testProfileRestoreFromProfileDefinitionCreateFunction() {
        final String typedId = ID;
        final CommonProfile restoredProfile = profileRestoreMustBringBackAllAttributes(typedId);

        assertNotNull(restoredProfile);
        assertEquals(ID, restoredProfile.getId());
        assertEquals("a@b.cc", restoredProfile.getEmail());
        assertEquals("John", restoredProfile.getFirstName());
        assertEquals("Doe", restoredProfile.getFamilyName());
        assertEquals(Gender.UNSPECIFIED, restoredProfile.getGender()); // Because it was not set
        assertNull(restoredProfile.getDisplayName()); // Was not set either

        assertEquals("12345-67890", restoredProfile.getAttribute(SESSION_INDEX));
        assertEquals(notBeforeTime, restoredProfile.getAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        assertEquals(notOnOrAfterTime, restoredProfile.getAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));

        assertEquals("IssuerId", restoredProfile.getAuthenticationAttribute(ISSUER_ID));
        final List<String> context = (List<String>) restoredProfile.getAuthenticationAttribute(AUTHN_CONTEXT);
        assertThat(context, CoreMatchers.hasItem("ContextItem1"));
        assertThat(context, CoreMatchers.hasItem("ContextItem2"));
        assertEquals("NameIdFormat", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_FORMAT));
        assertEquals("NameIdNameQualifier", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER));
        assertEquals("NameIdSpNameQualifier", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER));
        assertEquals("NameIdSpProvidedId", restoredProfile.getAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID));
        assertEquals(notBeforeTime, restoredProfile.getAuthenticationAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE));
        assertEquals(notOnOrAfterTime, restoredProfile.getAuthenticationAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE));
    }

    private CommonProfile profileRestoreMustBringBackAllAttributes(final String typedId) {
        final ProfileDefinition<CommonProfile> pd = new CommonProfileDefinition<>();
        final Map<String,Object> profileAttributes = exampleSamlProfileAttributes();
        final Map<String,Object> authenticationAttributes = exampleSamlAuthenticationAttributes();
        return ProfileHelper.restoreOrBuildProfile(pd, typedId, profileAttributes, authenticationAttributes);
    }

    private Map<String, Object> exampleSamlProfileAttributes() {
        final Map<String, Object> attr = new HashMap<>();
        attr.put(CommonProfileDefinition.EMAIL, "a@b.cc");
        attr.put(CommonProfileDefinition.FIRST_NAME, "John");
        attr.put(CommonProfileDefinition.FAMILY_NAME, "Doe");
        attr.put(SESSION_INDEX, "12345-67890");
        attr.put(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, notBeforeTime);
        attr.put(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, notOnOrAfterTime);
        return attr;
    }

    private Map<String, Object> exampleSamlAuthenticationAttributes() {
        final Map<String, Object> attr = new HashMap<>();
        attr.put(ISSUER_ID, "IssuerId");
        attr.put(AUTHN_CONTEXT, Arrays.asList("ContextItem1", "ContextItem2"));
        attr.put(SAML_NAME_ID_FORMAT, "NameIdFormat");
        attr.put(SAML_NAME_ID_NAME_QUALIFIER, "NameIdNameQualifier");
        attr.put(SAML_NAME_ID_SP_NAME_QUALIFIER, "NameIdSpNameQualifier");
        attr.put(SAML_NAME_ID_SP_PROVIDED_ID, "NameIdSpProvidedId");
        attr.put(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, notBeforeTime);
        attr.put(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, notOnOrAfterTime);
        return attr;
    }

    @Test
    public void testFlatIntoOneProfileOneAnonymousProfile() {
        final List<CommonProfile> profiles = Arrays.asList( AnonymousProfile.INSTANCE );
        assertEquals(AnonymousProfile.INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNAnonymousProfiles() {
        final List<CommonProfile> profiles = Arrays.asList( null, AnonymousProfile.INSTANCE, null, AnonymousProfile.INSTANCE );
        assertEquals(AnonymousProfile.INSTANCE, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileOneProfile() {
        final CommonProfile profile1 = new CommonProfile();
        profile1.setId("ONE");
        final List<CommonProfile> profiles = Arrays.asList( profile1 );
        assertEquals(profile1, ProfileHelper.flatIntoOneProfile(profiles).get());
    }

    @Test
    public void testFlatIntoOneProfileNProfiles() {
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId("TWO");
        final List<CommonProfile> profiles = Arrays.asList( AnonymousProfile.INSTANCE, null, profile2 );
        assertEquals(profile2, ProfileHelper.flatIntoOneProfile(profiles).get());
    }
}
