package org.pac4j.saml.credentials.authenticator;

import lombok.val;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.saml.credentials.SAML2AuthenticationCredentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.util.Configuration;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This is {@link SAML2AuthenticatorTests}.
 * @author Misagh Moayyed
 */
public class SAML2AuthenticatorTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    private final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)this.builderFactory.getBuilder(
        Conditions.DEFAULT_ELEMENT_NAME);

    private final SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>)this.builderFactory.getBuilder(
        NameID.DEFAULT_ELEMENT_NAME);

    @Test
    public void verifyAttributeMapping() {
        val credentials = createCredentialsForTest(true, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateWithMissingNotBeforeCondition() {
        val credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateAttributeConversion() throws URISyntaxException {
        val credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE, CommonProfileDefinition.GENDER,
            List.of("m"));
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE, CommonProfileDefinition.LOCALE,
            List.of(Locale.US.toLanguageTag()));

        final String pictureUri = "http://exapmle.com/picture";
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE,
            CommonProfileDefinition.PICTURE_URL, List.of(pictureUri));

        final String profileUri = "http://exapmle.com/profile";
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE,
            CommonProfileDefinition.PROFILE_URL, List.of(profileUri));

        assertTrue(finalProfile instanceof SAML2Profile);
        var samlProfile = (SAML2Profile)finalProfile;

        assertEquals(Gender.MALE, samlProfile.getGender());
        assertEquals(Locale.US, samlProfile.getLocale());
        assertEquals(pictureUri, samlProfile.getPictureUrl().toString());
        assertEquals(profileUri, samlProfile.getProfileUrl().toString());
    }

    @Test
    public void validateInvalidAttributeConversion() {
        val credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE, CommonProfileDefinition.GENDER,
            List.of("invalid"));
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE, CommonProfileDefinition.LOCALE,
            List.of(1L));
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE,
            CommonProfileDefinition.PICTURE_URL, List.of("invalid"));
        authenticator.getProfileDefinition().convertAndAdd(credentials.getUserProfile(), PROFILE_ATTRIBUTE,
            CommonProfileDefinition.PROFILE_URL, List.of("invalid"));

        assertTrue(finalProfile instanceof SAML2Profile);
        var samlProfile = (SAML2Profile)finalProfile;

        assertEquals(Gender.UNSPECIFIED, samlProfile.getGender());
        assertNull(samlProfile.getLocale());
        assertNotNull(samlProfile.getPictureUrl());
        assertEquals("invalid", samlProfile.getPictureUrl().toString());
        assertNotNull(samlProfile.getProfileUrl());
        assertEquals("invalid", samlProfile.getProfileUrl().toString());
    }

    @Test
    public void validateWithMissingNotOnOrAfterCondition() {
        val credentials = createCredentialsForTest(true, false);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateWithEmptyConditions() {
        val credentials = createCredentialsForTest(false, false);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        val authenticator = new SAML2Authenticator(null, null, "username", mappedAttributes);
        authenticator.init();
        authenticator.buildProfile(credentials);

        val finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    private Map<String, String> createMappedAttributesForTest() {
        final Map<String, String> mappedAttributes = new LinkedHashMap<>();
        mappedAttributes.put("urn:oid:2.16.840.1.113730.3.1.241", "mapped-display-name");
        mappedAttributes.put("urn:oid:2.5.4.42", "mapped-given-name");
        mappedAttributes.put("urn:oid:2.5.4.4", "mapped-surname");
        return mappedAttributes;
    }

    private SAML2AuthenticationCredentials createCredentialsForTest(boolean includeNotBefore, boolean includeNotOnOrAfter) {
        val nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");

        val conditions = conditionsBuilder.buildObject();

        if (includeNotBefore) {
            conditions.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        }

        if (includeNotOnOrAfter) {
            conditions.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        }

        final List<String> contexts = new ArrayList<>();
        contexts.add("cas-context");

        final List<Attribute> attributes = new ArrayList<>();

        attributes.add(createAttribute("username", "username", "pac4j"));
        attributes.add(createAttribute("displayName", "urn:oid:2.16.840.1.113730.3.1.241", "Pac4j Library"));
        attributes.add(createAttribute("givenName", "urn:oid:2.5.4.42", "developer"));
        attributes.add(createAttribute("surname", "urn:oid:2.5.4.4", "security"));

        val credentials = new SAML2AuthenticationCredentials(SAML2AuthenticationCredentials.SAMLNameID.from(nameid),
            "example.issuer.com",
            SAML2AuthenticationCredentials.SAMLAttribute.from(new SimpleSAML2AttributeConverter(), attributes), conditions, "session-index",
            contexts, List.of(),
            UUID.randomUUID().toString());
        return credentials;
    }

    private Attribute createAttribute(final String friendlyName, final String name, final String value) {
        val attributeBuilder = (SAMLObjectBuilder<Attribute>)this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);

        val attr = attributeBuilder.buildObject();
        attr.setFriendlyName(friendlyName);
        attr.setName(name);

        val attrValue = mock(XMLObject.class);
        val dom = mock(Element.class);
        when(dom.getTextContent()).thenReturn(value);
        when(attrValue.getDOM()).thenReturn(dom);
        when(attrValue.getSchemaType()).thenReturn(new QName(" http://www.w3.org/2001/XMLSchema", "string", "xs"));

        attr.getAttributeValues().add(attrValue);
        return attr;
    }
}
