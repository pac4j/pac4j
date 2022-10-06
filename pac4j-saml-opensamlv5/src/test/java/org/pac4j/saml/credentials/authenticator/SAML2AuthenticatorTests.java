package org.pac4j.saml.credentials.authenticator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.*;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.util.Configuration;
import org.w3c.dom.Element;

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
        final var credentials = createCredentialsForTest(true, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateWithMissingNotBeforeCondition() {
        final var credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateAttributeConversion() throws URISyntaxException {
        final var credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
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
        final var credentials = createCredentialsForTest(false, true);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
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
        final var credentials = createCredentialsForTest(true, false);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
        assertTrue(finalProfile.containsAttribute("mapped-display-name"));
        assertTrue(finalProfile.containsAttribute("mapped-given-name"));
        assertTrue(finalProfile.containsAttribute("mapped-surname"));
    }

    @Test
    public void validateWithEmptyConditions() {
        final var credentials = createCredentialsForTest(false, false);
        final Map<String, String> mappedAttributes = createMappedAttributesForTest();

        final var authenticator = new SAML2Authenticator("username", mappedAttributes);
        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var finalProfile = credentials.getUserProfile();
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

    private SAML2Credentials createCredentialsForTest(boolean includeNotBefore, boolean includeNotOnOrAfter) {
        final var nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");

        final var conditions = conditionsBuilder.buildObject();

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

        final var credentials = new SAML2Credentials(SAML2Credentials.SAMLNameID.from(nameid),
            "example.issuer.com",
            SAML2Credentials.SAMLAttribute.from(new SimpleSAML2AttributeConverter(), attributes), conditions, "session-index", contexts,
            UUID.randomUUID().toString());
        return credentials;
    }

    private Attribute createAttribute(final String friendlyName, final String name, final String value) {
        final var attributeBuilder = (SAMLObjectBuilder<Attribute>)this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);

        final var attr = attributeBuilder.buildObject();
        attr.setFriendlyName(friendlyName);
        attr.setName(name);

        final var attrValue = mock(XMLObject.class);
        final var dom = mock(Element.class);
        when(dom.getTextContent()).thenReturn(value);
        when(attrValue.getDOM()).thenReturn(dom);
        when(attrValue.getSchemaType()).thenReturn(new QName(" http://www.w3.org/2001/XMLSchema", "string", "xs"));

        attr.getAttributeValues().add(attrValue);
        return attr;
    }
}
