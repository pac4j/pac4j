package org.pac4j.saml.profile.converter;

import lombok.val;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.saml.credentials.SAML2InternalCredentials;

/**
 * A simple attribute converter for SAML2.
 *
 * @author Jerome LELEU
 * @since 5.4.0
 */
public class SimpleSAML2AttributeConverter implements AttributeConverter {

    @Override
    public Object convert(final Object a) {
        val attribute = (Attribute) a;
        val samlAttribute = new SAML2InternalCredentials.SAMLAttribute();
        samlAttribute.setFriendlyName(attribute.getFriendlyName());
        samlAttribute.setName(attribute.getName());
        samlAttribute.setNameFormat(attribute.getNameFormat());
        attribute.getAttributeValues()
            .stream()
            .map(XMLObject::getDOM)
            .filter(dom -> dom != null && dom.getTextContent() != null)
            .forEach(dom -> samlAttribute.getAttributeValues().add(dom.getTextContent().trim()));
        return samlAttribute;
    }
}
