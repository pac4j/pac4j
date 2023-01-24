package org.pac4j.saml.credentials;

import lombok.val;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.util.Configuration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * This is {@link SAML2CredentialsSerializationTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2CredentialsSerializationTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    @Test
    public void verifyOperation() {
        val nameIdBuilder = (SAMLObjectBuilder<NameID>)
            this.builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        assertNotNull(nameIdBuilder);

        val nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");

        val conditionsBuilder = (SAMLObjectBuilder<Conditions>)
            this.builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
        assertNotNull(conditionsBuilder);

        val conditions = conditionsBuilder.buildObject();
        conditions.setNotBefore(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        conditions.setNotOnOrAfter(ZonedDateTime.now(ZoneOffset.UTC).toInstant());

        final List<String> contexts = new ArrayList<>();
        contexts.add("cas-context");

        val attributeBuilder = (SAMLObjectBuilder<Attribute>)
            this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        assertNotNull(attributeBuilder);

        final List<Attribute> attributes = new ArrayList<>();
        val attr = attributeBuilder.buildObject();
        attr.setFriendlyName("pac4j");
        attr.setName("pac4j");
        attr.setNameFormat("pac4j");
        attributes.add(attr);
        val credentials = new SAML2InternalCredentials(SAML2InternalCredentials.SAMLNameID.from(nameid), "example.issuer.com",
            SAML2InternalCredentials.SAMLAttribute.from(new SimpleSAML2AttributeConverter(), attributes), conditions, "session-index",
            contexts, List.of(), UUID.randomUUID().toString());
        val data = SerializationUtils.serialize(credentials);
        val result = (SAML2InternalCredentials) SerializationUtils.deserialize(data);
        assertNotNull(result);
        assertNotNull(result.getInResponseTo());
    }
}
