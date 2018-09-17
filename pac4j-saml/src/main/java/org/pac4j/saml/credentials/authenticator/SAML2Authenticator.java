package org.pac4j.saml.credentials.authenticator;

import org.apache.commons.lang.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

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

    private final String attributeAsId;

    public SAML2Authenticator(final String attributeAsId) {
        this.attributeAsId = attributeAsId;
    }

    @Override
    protected void internalInit() {
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new SAML2Profile()));
    }

    @Override
    public void validate(final SAML2Credentials credentials, final WebContext context) {
        init();

        final SAML2Profile profile = getProfileDefinition().newProfile();

        final SAML2Credentials.SAMLNameID nameId = credentials.getNameId();
        profile.setId(nameId.getValue());

        profile.addAttribute(SESSION_INDEX, credentials.getSessionIndex());
        profile.addAuthenticationAttribute(SAML_NAME_ID_FORMAT, nameId.getFormat());
        profile.addAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER, nameId.getNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER, nameId.getSpNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID, nameId.getSpProviderId());

        for (final SAML2Credentials.SAMLAttribute attribute : credentials.getAttributes()) {
            logger.debug("Processing profile attribute {}", attribute);

            final String name = attribute.getName();
            final String friendlyName = attribute.getFriendlyName();

            final List<String> values = attribute.getAttributeValues();
            if (!values.isEmpty()) {
                if (StringUtils.isNotBlank(attributeAsId)
                    && (attributeAsId.equalsIgnoreCase(name) || attributeAsId.equalsIgnoreCase(friendlyName))) {
                    if (values.size() == 1) {
                        profile.setId(values.get(0));
                    } else {
                        logger.warn("Will not add {} as id because it has multiple values: {}", attributeAsId, values);
                    }
                }
                getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, name, values);
                if (CommonHelper.isNotBlank(friendlyName)) {
                    getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, friendlyName, values);
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
        final SAML2Credentials.SAMLConditions conditions = credentials.getConditions();
        if (conditions != null) {
            profile.addAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            profile.addAuthenticationAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            profile.addAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
            profile.addAuthenticationAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
        }

        credentials.setUserProfile(profile);
    }
}
