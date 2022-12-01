package org.pac4j.saml.profile.converter;

import lombok.val;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.saml.credentials.SAML2Credentials;
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

    @Override
    public Object convert(final Object a) {
        val attribute = (Attribute) a;

        List<SAML2Credentials.SAMLAttribute> extractedAttributes = new ArrayList<>();

        // collect all complex values
        attribute.getAttributeValues().stream()
            .filter(XMLObject::hasChildren)
            .forEach(attributeValue -> {
                List<SAML2Credentials.SAMLAttribute> attrs = collectAttributesFromNodeList(attributeValue.getDOM().getChildNodes());
                extractedAttributes.addAll(attrs);
            });
        // collect all simple values
        SAML2Credentials.SAMLAttribute simpleValues = from(attribute);
        if (!simpleValues.getAttributeValues().isEmpty()) {
            extractedAttributes.add(simpleValues);
        }

        return extractedAttributes;
    }

    private SAML2Credentials.SAMLAttribute from(Attribute attribute) {
        val samlAttribute = new SAML2Credentials.SAMLAttribute();
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

    private List<SAML2Credentials.SAMLAttribute> collectAttributesFromNodeList(NodeList nodeList)  {

        var results = new ArrayList<SAML2Credentials.SAMLAttribute>();

        if (nodeList == null) {
            return results;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.hasChildNodes()) {
                results.addAll(collectAttributesFromNodeList(node.getChildNodes()));
            } else if (!node.getTextContent().isBlank()) {
                SAML2Credentials.SAMLAttribute samlAttribute = new SAML2Credentials.SAMLAttribute();
                samlAttribute.setName(node.getParentNode().getLocalName());
                samlAttribute.getAttributeValues().add(node.getTextContent());
                results.add(samlAttribute);
            }
        }

        return results;
    }
}
