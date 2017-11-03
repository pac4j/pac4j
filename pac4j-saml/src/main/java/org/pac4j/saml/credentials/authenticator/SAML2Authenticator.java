package org.pac4j.saml.credentials.authenticator;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Authenticator for SAML 2.0
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2Authenticator extends ProfileDefinitionAware<SAML2Profile> implements Authenticator<SAML2Credentials> {

    public static final String SAML_CONDITION_NOT_BEFORE_ATTRIBUTE = "notBefore";
    public static final String SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE = "notOnOrAfter";
    public static final String SESSION_INDEX = "sessionindex";
    public static final String ISSUER_ID = "issuerId";
    public static final String AUTHN_CONTEXT = "authnContext";
    public static final String SAML_NAME_ID_FORMAT = "samlNameIdFormat";
    public static final String SAML_NAME_ID_NAME_QUALIFIER = "samlNameIdNameQualifier";
    public static final String SAML_NAME_ID_SP_NAME_QUALIFIER = "samlNameIdSpNameQualifier";
    public static final String SAML_NAME_ID_SP_PROVIDED_ID = "samlNameIdSpProvidedId";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void internalInit() {
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new SAML2Profile()));
    }

    @Override
    public void validate(final SAML2Credentials credentials, final WebContext context) {
        init();

        final SAML2Profile profile = getProfileDefinition().newProfile();
        final NameID nameId = credentials.getNameId();
        profile.setId(nameId.getValue());
        profile.addAttribute(SESSION_INDEX, credentials.getSessionIndex());
        profile.addAuthenticationAttribute(SAML_NAME_ID_FORMAT, nameId.getFormat());
        profile.addAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER, nameId.getNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER, nameId.getSPNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID, nameId.getSPProvidedID());

        for (final Attribute attribute : credentials.getAttributes()) {
            logger.debug("Processing profile attribute {}", attribute);

            final String name = attribute.getName();
            final String friendlyName = attribute.getFriendlyName();

            final List<String> values = new ArrayList<>();
            for (final XMLObject attributeValue : attribute.getAttributeValues()) {
                final Element attributeValueElement = attributeValue.getDOM();
                if (attributeValueElement != null) {
                    final String value = attributeValueElement.getTextContent();
                    logger.debug("Adding attribute value {} for attribute {} / {}", value,
                            name, friendlyName);
                    values.add(value);
                } else {
                    logger.warn("Attribute value DOM element is null for {}", attribute);
                }
            }

            if (!values.isEmpty()) {
                getProfileDefinition().convertAndAdd(profile, name, values);
                if (CommonHelper.isNotBlank(friendlyName)) {
                    getProfileDefinition().convertAndAdd(profile, friendlyName, values);
                }
            } else {
                logger.debug("No attribute values found for {}", name);
            }
        }

        // Add in issuerID and authnContexts
        profile.addAuthenticationAttribute(ISSUER_ID, credentials.getIssuerId());
        profile.addAuthenticationAttribute(AUTHN_CONTEXT, credentials.getAuthnContexts());
        // Retrieve conditions attributes
        // Adding them to both the "regular" and authentication attributes so we don't break anyone currently using it.
        Conditions conditions = credentials.getConditions();
        if (conditions != null) {
            profile.addAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            profile.addAuthenticationAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            profile.addAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
            profile.addAuthenticationAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
        }

        credentials.setUserProfile(profile);
    }
}
