package org.pac4j.saml.credentials;

import lombok.val;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.util.Configuration;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This is {@link SAML2CredentialsSerializationTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2CredentialsSerializationTests {
    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    @Test
    public void verifySerialization() {
        var mockWebContext = MockWebContext.create();
        Serializable credentials = new SAML2Credentials(LogoutType.BACK,
            new SAML2MessageContext(new CallContext(mockWebContext, new MockSessionStore())));
        val data = SerializationUtils.serialize(credentials);
        assertNotNull(data);
    }

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
        conditions.setNotBefore(Instant.now());
        conditions.setNotOnOrAfter(Instant.now());

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
        Serializable credentials = new SAML2AuthenticationCredentials(
            SAML2AuthenticationCredentials.SAMLNameID.from(nameid), "example.issuer.com",
            SAML2AuthenticationCredentials.SAMLAttribute.from(
                new SimpleSAML2AttributeConverter(), attributes), conditions, "session-index",
            contexts, List.of(), UUID.randomUUID().toString());
        val data = SerializationUtils.serialize(credentials);
        val result = (SAML2AuthenticationCredentials) SerializationUtils.deserialize(data);
        assertNotNull(result);
        assertNotNull(result.getInResponseTo());
    }
}
