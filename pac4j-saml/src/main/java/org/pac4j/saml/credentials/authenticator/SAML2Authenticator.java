package org.pac4j.saml.credentials.authenticator;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
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

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void internalInit(final WebContext context) {
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new SAML2Profile()));
    }

    @Override
    public void validate(final SAML2Credentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        final SAML2Profile profile = getProfileDefinition().newProfile();
        profile.setId(credentials.getNameId().getValue());
        profile.addAttribute(SESSION_INDEX, credentials.getSessionIndex());
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

        // Retrieve conditions attributes
        Conditions conditions = credentials.getConditions();
        if (conditions != null) {
            profile.addAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            profile.addAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
        }

        credentials.setUserProfile(profile);
    }
}
