package org.pac4j.saml.credentials.authenticator;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.credentials.SAML2AuthenticationCredentials;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.logout.impl.SAML2LogoutValidator;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.SAML2ProfileDefinition;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * Authenticator for SAML 2.0
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2Authenticator extends ProfileDefinitionAware implements Authenticator {

    /** Constant <code>SAML_CONDITION_NOT_BEFORE_ATTRIBUTE="notBefore"</code> */
    public static final String SAML_CONDITION_NOT_BEFORE_ATTRIBUTE = "notBefore";
    /** Constant <code>SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE="notOnOrAfter"</code> */
    public static final String SAML_CONDITION_NOT_ON_OR_AFTER_ATTRIBUTE = "notOnOrAfter";
    /** Constant <code>SESSION_INDEX="sessionindex"</code> */
    public static final String SESSION_INDEX = "sessionindex";
    /** Constant <code>ISSUER_ID="issuerId"</code> */
    public static final String ISSUER_ID = "issuerId";
    /** Constant <code>AUTHN_CONTEXT="authnContext"</code> */
    public static final String AUTHN_CONTEXT = "authnContext";
    /** Constant <code>AUTHN_CONTEXT_AUTHORITIES="authnContextAuthorities"</code> */
    public static final String AUTHN_CONTEXT_AUTHORITIES = "authnContextAuthorities";
    /** Constant <code>SAML_NAME_ID_FORMAT="samlNameIdFormat"</code> */
    public static final String SAML_NAME_ID_FORMAT = "samlNameIdFormat";
    /** Constant <code>SAML_NAME_ID_NAME_QUALIFIER="samlNameIdNameQualifier"</code> */
    public static final String SAML_NAME_ID_NAME_QUALIFIER = "samlNameIdNameQualifier";
    /** Constant <code>SAML_NAME_ID_SP_NAME_QUALIFIER="samlNameIdSpNameQualifier"</code> */
    public static final String SAML_NAME_ID_SP_NAME_QUALIFIER = "samlNameIdSpNameQualifier";
    /** Constant <code>SAML_NAME_ID_SP_PROVIDED_ID="samlNameIdSpProvidedId"</code> */
    public static final String SAML_NAME_ID_SP_PROVIDED_ID = "samlNameIdSpProvidedId";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final SAML2ResponseValidator loginValidator;

    private final SAML2LogoutValidator logoutValidator;

    private final String attributeAsId;

    /**
     * Describes the map of attributes that are to be fetched from the credential (map keys)
     * and then transformed/renamed using map values before they are put into a profile.
     * An example might be: fetch givenName from credential and rename it to 'urn:oid:2.5.4.42' or vice versa.
     * Note that this setting only applies to attribute names, and not friendly-names.
     */
    private final Map<String, String> mappedAttributes;

    /**
     * <p>Constructor for SAML2Authenticator.</p>
     *
     * @param loginValidator a {@link SAML2ResponseValidator} object
     * @param logoutValidator a {@link SAML2LogoutValidator} object
     * @param attributeAsId a {@link String} object
     * @param mappedAttributes a {@link Map} object
     */
    public SAML2Authenticator(final SAML2ResponseValidator loginValidator, final SAML2LogoutValidator logoutValidator,
                              final String attributeAsId, final Map<String, String> mappedAttributes) {
        this.loginValidator = loginValidator;
        this.logoutValidator = logoutValidator;
        this.attributeAsId = attributeAsId;
        this.mappedAttributes = mappedAttributes;
    }

    /**
     * <p>Constructor for SAML2Authenticator.</p>
     *
     * @param loginValidator a {@link SAML2ResponseValidator} object
     * @param logoutValidator a {@link SAML2LogoutValidator} object
     * @param attributeAsId a {@link String} object
     */
    public SAML2Authenticator(final SAML2ResponseValidator loginValidator, final SAML2LogoutValidator logoutValidator,
                              final String attributeAsId) {
        this(loginValidator, logoutValidator, attributeAsId, new HashMap<>());
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setProfileDefinitionIfUndefined(new SAML2ProfileDefinition());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials extractedCredentials) {
        init();

        val saml2Credentials = (SAML2Credentials) extractedCredentials;

        val samlContext = saml2Credentials.getContext();
        if (saml2Credentials.isForAuthentication()) {
            val authenticationCredentials = (SAML2AuthenticationCredentials) loginValidator.validate(samlContext);
            buildProfile(authenticationCredentials);
            return Optional.of(authenticationCredentials);
        } else {
            logoutValidator.validate(samlContext);
            return Optional.of(extractedCredentials);
        }
    }

    /**
     * <p>buildProfile.</p>
     *
     * @param credentials a {@link SAML2AuthenticationCredentials} object
     */
    protected void buildProfile(final SAML2AuthenticationCredentials credentials) {
        UserProfile profile = (SAML2Profile) getProfileDefinition().newProfile();

        val nameId = credentials.getNameId();
        profile.setId(nameId.getValue());

        profile.addAuthenticationAttribute(SESSION_INDEX, credentials.getSessionIndex());
        profile.addAuthenticationAttribute(SAML_NAME_ID_FORMAT, nameId.getFormat());
        profile.addAuthenticationAttribute(SAML_NAME_ID_NAME_QUALIFIER, nameId.getNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_NAME_QUALIFIER, nameId.getSpNameQualifier());
        profile.addAuthenticationAttribute(SAML_NAME_ID_SP_PROVIDED_ID, nameId.getSpProviderId());

        for (val attribute : credentials.getAttributes()) {
            logger.debug("Processing profile attribute {}", attribute);

            val name = attribute.getName();
            val friendlyName = attribute.getFriendlyName();

            val values = attribute.getAttributeValues();
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
        profile.addAuthenticationAttribute(AUTHN_CONTEXT_AUTHORITIES, credentials.getAuthnContextAuthorities());
        // Retrieve conditions attributes
        // Adding them to both the "regular" and authentication attributes so we don't break anyone currently using it.
        val conditions = credentials.getConditions();
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
