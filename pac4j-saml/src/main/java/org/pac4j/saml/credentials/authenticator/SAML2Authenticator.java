package org.pac4j.saml.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.SAML2ProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * Authenticator for SAML 2.0
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2Authenticator extends ProfileDefinitionAware implements Authenticator {

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

    /**
     * Describes the map of attributes that are to be fetched from the credential (map keys)
     * and then transformed/renamed using map values before they are put into a profile.
     * An example might be: fetch givenName from credential and rename it to 'urn:oid:2.5.4.42' or vice versa.
     * Note that this setting only applies to attribute names, and not friendly-names.
     */
    private final Map<String, String> mappedAttributes;

    public SAML2Authenticator(final String attributeAsId, final Map<String, String> mappedAttributes) {
        this.attributeAsId = attributeAsId;
        this.mappedAttributes = mappedAttributes;
    }

    public SAML2Authenticator(final String attributeAsId) {
        this(attributeAsId, new HashMap<>());
    }

    @Override
    protected void internalInit() {
        defaultProfileDefinition(new SAML2ProfileDefinition());
    }

    @Override
    public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        final var credentials = (SAML2Credentials) cred;
        final var profile = (SAML2Profile) getProfileDefinition().newProfile();

        final var nameId = credentials.getNameId();
        profile.setId(nameId.getValue());

        profile.addAuthenticationAttribute(SESSION_INDEX, credentials.getSessionIndex());
        profile.addAuthenticationAttribute(SAML_NAME_ID_FORMAT, nameId.getFormat());
        profile.addAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER, nameId.getNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER, nameId.getSpNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID, nameId.getSpProviderId());

        for (final var attribute : credentials.getAttributes()) {
            logger.debug("Processing profile attribute {}", attribute);

            final var name = attribute.getName();
            final var friendlyName = attribute.getFriendlyName();

            final var values = attribute.getAttributeValues();
            if (!values.isEmpty()) {
                if (CommonHelper.isNotBlank(attributeAsId)
                    && (attributeAsId.equalsIgnoreCase(name) || attributeAsId.equalsIgnoreCase(friendlyName))) {
                    if (values.size() == 1) {
                        profile.setId(values.get(0));
                    } else {
                        logger.warn("Will not add {} as id because it has multiple values: {}", attributeAsId, values);
                    }
                }

                final String actualName;
                if (mappedAttributes != null && !mappedAttributes.isEmpty() && mappedAttributes.containsKey(name)) {
                    actualName = mappedAttributes.get(name);
                    logger.debug("Mapping attribute {} as {} with values {} to profile", name, actualName, values);
                    getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, actualName, values);
                } else {
                    actualName = name;
                    logger.debug("Adding attribute {} to profile with values {}", name, values);
                    getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, name, values);
                }

                if (CommonHelper.isNotBlank(friendlyName) && CommonHelper.areNotEquals(friendlyName, actualName)) {
                    logger.debug("Adding attribute {} to profile with values {}", friendlyName, values);
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
        final var conditions = credentials.getConditions();
        if (conditions != null) {
            if (conditions.getNotBefore() != null) {
                profile.addAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
                profile.addAuthenticationAttribute(SAML_CONDITION_NOT_BEFORE_ATTRIBUTE, conditions.getNotBefore());
            }
            if (conditions.getNotOnOrAfter() != null) {
                profile.addAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
                profile.addAuthenticationAttribute(SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE, conditions.getNotOnOrAfter());
            }
        }

        credentials.setUserProfile(profile);
    }
}
