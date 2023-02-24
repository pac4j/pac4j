package org.pac4j.saml.profile.converter;

import lombok.val;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.saml.credentials.SAML2AuthenticationCredentials;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An attribute converter for SAML2 complex types.
 *
 * @author Jerome LELEU
 * @since 5.4.0
 */
public class ComplexTypeSAML2AttributeConverter implements AttributeConverter {

    /** {@inheritDoc} */
    @Override
    public Object convert(final Object a) {
        val attribute = (Attribute) a;

        val extractedAttributes = new ArrayList<SAML2AuthenticationCredentials.SAMLAttribute>();

        // collect all complex values
        attribute.getAttributeValues().stream()
            .filter(XMLObject::hasChildren)
            .forEach(attributeValue -> {
                val attrs = collectAttributesFromNodeList(attributeValue.getDOM().getChildNodes());
                extractedAttributes.addAll(attrs);
            });
        // collect all simple values
        SAML2AuthenticationCredentials.SAMLAttribute simpleValues = from(attribute);
        if (!simpleValues.getAttributeValues().isEmpty()) {
            extractedAttributes.add(simpleValues);
        }

        return extractedAttributes;
    }

    private SAML2AuthenticationCredentials.SAMLAttribute from(Attribute attribute) {
        val samlAttribute = new SAML2AuthenticationCredentials.SAMLAttribute();
        samlAttribute.setFriendlyName(attribute.getFriendlyName());
        samlAttribute.setName(attribute.getName());
        samlAttribute.setNameFormat(attribute.getNameFormat());
        List<String> values = attribute.getAttributeValues()
            .stream()
            .filter(val -> !val.hasChildren())
            .map(XMLObject::getDOM)
            .filter(dom -> dom != null && dom.getTextContent() != null)
            .map(Element::getTextContent)
            .collect(Collectors.toList());
        samlAttribute.setAttributeValues(values);
        return samlAttribute;
    }

    private List<SAML2AuthenticationCredentials.SAMLAttribute> collectAttributesFromNodeList(NodeList nodeList)  {

        var results = new ArrayList<SAML2AuthenticationCredentials.SAMLAttribute>();

        if (nodeList == null) {
            return results;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.hasChildNodes()) {
                results.addAll(collectAttributesFromNodeList(node.getChildNodes()));
            } else if (!node.getTextContent().isBlank()) {
                SAML2AuthenticationCredentials.SAMLAttribute samlAttribute = new SAML2AuthenticationCredentials.SAMLAttribute();
                samlAttribute.setName(node.getParentNode().getLocalName());
                samlAttribute.getAttributeValues().add(node.getTextContent());
                results.add(samlAttribute);
            }
        }

        return results;
    }
}
