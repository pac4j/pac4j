package org.pac4j.saml.credentials.authenticator;

import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.util.Configuration;
import org.w3c.dom.Element;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SAML2AuthenticatorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2AuthenticatorTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    private final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
        this.builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);

    private final SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>)
        this.builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);

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
            SAML2Credentials.SAMLAttribute.from(attributes), conditions, "session-index", contexts);
        return credentials;
    }

    private Attribute createAttribute(final String friendlyName, final String name, final String value) {
        final var attributeBuilder = (SAMLObjectBuilder<Attribute>)
            this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);

        final var attr = attributeBuilder.buildObject();
        attr.setFriendlyName(friendlyName);
        attr.setName(name);

        final var attrValue = mock(XMLObject.class);
        final var dom = mock(Element.class);
        when(dom.getTextContent()).thenReturn(value);
        when(attrValue.getDOM()).thenReturn(dom);

        attr.getAttributeValues().add(attrValue);
        return attr;
    }
}
