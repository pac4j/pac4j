package org.pac4j.saml.credentials;

import org.apache.commons.lang.SerializationUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.saml.util.Configuration;

import java.util.ArrayList;
import java.util.List;

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
        final SAMLObjectBuilder<NameID> nameIdBuilder = (SAMLObjectBuilder<NameID>)
            this.builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
        assertNotNull(nameIdBuilder);

        final NameID nameid = nameIdBuilder.buildObject();
        nameid.setValue("pac4j");
        nameid.setSPNameQualifier("pac4j");
        nameid.setNameQualifier("pac4j");
        nameid.setSPProvidedID("pac4j");

        final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
            this.builderFactory.getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
        assertNotNull(conditionsBuilder);

        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.setNotBefore(DateTime.now());
        conditions.setNotOnOrAfter(DateTime.now());

        final List<String> contexts = new ArrayList<>();
        contexts.add("cas-context");

        final SAMLObjectBuilder<Attribute> attributeBuilder = (SAMLObjectBuilder<Attribute>)
            this.builderFactory.getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
        assertNotNull(attributeBuilder);

        final List<Attribute> attributes = new ArrayList<>();
        final Attribute attr = attributeBuilder.buildObject();
        attr.setFriendlyName("pac4j");
        attr.setName("pac4j");
        attr.setNameFormat("pac4j");
        attributes.add(attr);
        final SAML2Credentials credentials = new SAML2Credentials(nameid, "example.issuer.com",
            attributes, conditions, "session-index", contexts);
        final byte[] data = SerializationUtils.serialize(credentials);
        final SAML2Credentials result = (SAML2Credentials) SerializationUtils.deserialize(data);
        assertNotNull(result);
    }
}
