package org.pac4j.saml.credentials;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.saml.util.Configuration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * This is {@link SAML2CredentialsSerializationTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2CredentialsSerializationTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    @Test
    public void verifyOperation() {
        final var nameIdBuilder = (SAMLObjectBuilder<NameID>)
            this.builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        assertNotNull(nameIdBuilder);

        final var nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");

        final var conditionsBuilder = (SAMLObjectBuilder<Conditions>)
            this.builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
        assertNotNull(conditionsBuilder);

        final var conditions = conditionsBuilder.buildObject();
        conditions.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        conditions.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());

        final List<String> contexts = new ArrayList<>();
        contexts.add("cas-context");

        final var attributeBuilder = (SAMLObjectBuilder<Attribute>)
            this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        assertNotNull(attributeBuilder);

        final List<Attribute> attributes = new ArrayList<>();
        final var attr = attributeBuilder.buildObject();
        attr.setFriendlyName("pac4j");
        attr.setName("pac4j");
        attr.setNameFormat("pac4j");
        attributes.add(attr);
        final var credentials = new SAML2Credentials(SAML2Credentials.SAMLNameID.from(nameid), "example.issuer.com",
            SAML2Credentials.SAMLAttribute.from(attributes), conditions, "session-index", contexts,
            UUID.randomUUID().toString());
        final var data = SerializationUtils.serialize(credentials);
        final var result = (SAML2Credentials) SerializationUtils.deserialize(data);
        assertNotNull(result);
        assertNotNull(result.getInResponseTo());
    }
}
