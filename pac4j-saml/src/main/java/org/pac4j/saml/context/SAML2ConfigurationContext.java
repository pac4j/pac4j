package org.pac4j.saml.context;

import org.apache.commons.lang3.BooleanUtils;
import org.opensaml.messaging.context.BaseContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.List;

/**
 * This is {@link org.pac4j.saml.context.SAML2ConfigurationContext}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@SuppressWarnings("unchecked")
public class SAML2ConfigurationContext extends BaseContext {
    /** Constant <code>REQUEST_ATTR_AUTHN_REQUEST_BINDING_TYPE="AuthnRequestBindingType"</code> */
    public static final String REQUEST_ATTR_AUTHN_REQUEST_BINDING_TYPE = "AuthnRequestBindingType";
    /** Constant <code>REQUEST_ATTR_ASSERTION_CONSUMER_SERVICE_INDEX="AssertionConsumerServiceIndex"</code> */
    public static final String REQUEST_ATTR_ASSERTION_CONSUMER_SERVICE_INDEX = "AssertionConsumerServiceIndex";
    /** Constant <code>REQUEST_ATTR_ATTRIBUTE_CONSUMING_SERVICE_INDEX="AttributeConsumingServiceIndex"</code> */
    public static final String REQUEST_ATTR_ATTRIBUTE_CONSUMING_SERVICE_INDEX = "AttributeConsumingServiceIndex";
    /** Constant <code>REQUEST_ATTR_COMPARISON_TYPE="ComparisonType"</code> */
    public static final String REQUEST_ATTR_COMPARISON_TYPE = "ComparisonType";
    /** Constant <code>REQUEST_ATTR_NAME_ID_POLICY_FORMAT="NameIdPolicyFormat"</code> */
    public static final String REQUEST_ATTR_NAME_ID_POLICY_FORMAT = "NameIdPolicyFormat";
    /** Constant <code>REQUEST_ATTR_NAME_ID_POLICY_ALLOW_CREATE="NameIdPolicyAllowCreate"</code> */
    public static final String REQUEST_ATTR_NAME_ID_POLICY_ALLOW_CREATE = "NameIdPolicyAllowCreate";
    /** Constant <code>REQUEST_ATTR_PROVIDER_NAME="ProviderName"</code> */
    public static final String REQUEST_ATTR_PROVIDER_NAME = "ProviderName";
    /** Constant <code>REQUEST_ATTR_ISSUER_FORMAT="IssuerFormat"</code> */
    public static final String REQUEST_ATTR_ISSUER_FORMAT = "IssuerFormat";
    /** Constant <code>REQUEST_ATTR_USE_NAME_QUALIFIER="UseNameQualifier"</code> */
    public static final String REQUEST_ATTR_USE_NAME_QUALIFIER = "UseNameQualifier";
    /** Constant <code>REQUEST_ATTR_AUTHN_CONTEXT_CLASS_REFS="AuthnContextClassRefs"</code> */
    public static final String REQUEST_ATTR_AUTHN_CONTEXT_CLASS_REFS = "AuthnContextClassRefs";
    /** Constant <code>REQUEST_ATTR_NAME_ID_ATTRIBUTE="NameIdAttribute"</code> */
    public static final String REQUEST_ATTR_NAME_ID_ATTRIBUTE = "NameIdAttribute";

    /** Constant <code>REQUEST_ATTR_WANTS_ASSERTIONS_SIGNED="WantsAssertionsSigned"</code> */
    public static final String REQUEST_ATTR_WANTS_ASSERTIONS_SIGNED = "WantsAssertionsSigned";
    /** Constant <code>REQUEST_ATTR_WANTS_RESPONSES_SIGNED="WantsResponsesSigned"</code> */
    public static final String REQUEST_ATTR_WANTS_RESPONSES_SIGNED = "WantsResponsesSigned";
    /** Constant <code>REQUEST_ATTR_MAXIMUM_AUTHENTICATION_LIFETIME="MaximumAuthenticationLifetime"</code> */
    public static final String REQUEST_ATTR_MAXIMUM_AUTHENTICATION_LIFETIME = "MaximumAuthenticationLifetime";

    private final WebContext webContext;
    private final SAML2Configuration configuration;

    /**
     * <p>Constructor for SAML2ConfigurationContext.</p>
     *
     * @param webContext a {@link org.pac4j.core.context.WebContext} object
     * @param configuration a {@link org.pac4j.saml.config.SAML2Configuration} object
     */
    public SAML2ConfigurationContext(final WebContext webContext,
                                     final SAML2Configuration configuration) {
        this.webContext = webContext;
        this.configuration = configuration;
    }

    /**
     * <p>getSAML2Configuration.</p>
     *
     * @return a {@link org.pac4j.saml.config.SAML2Configuration} object
     */
    public SAML2Configuration getSAML2Configuration() {
        return configuration;
    }

    /**
     * <p>getAuthnRequestBindingType.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAuthnRequestBindingType() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_AUTHN_REQUEST_BINDING_TYPE)
            .orElse(configuration.getAuthnRequestBindingType());
    }

    /**
     * <p>getAssertionConsumerServiceIndex.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getAssertionConsumerServiceIndex() {
        return (Integer) webContext.getRequestAttribute(REQUEST_ATTR_ASSERTION_CONSUMER_SERVICE_INDEX)
            .orElse(configuration.getAssertionConsumerServiceIndex());
    }

    /**
     * <p>getAttributeConsumingServiceIndex.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getAttributeConsumingServiceIndex() {
        return (Integer) webContext.getRequestAttribute(REQUEST_ATTR_ATTRIBUTE_CONSUMING_SERVICE_INDEX)
            .orElse(configuration.getAttributeConsumingServiceIndex());
    }

    /**
     * <p>getComparisonType.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getComparisonType() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_COMPARISON_TYPE)
            .orElse(configuration.getComparisonType());
    }

    /**
     * <p>getNameIdPolicyFormat.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getNameIdPolicyFormat() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_POLICY_FORMAT)
            .orElse(configuration.getNameIdPolicyFormat());
    }

    /**
     * <p>isNameIdPolicyAllowCreate.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean isNameIdPolicyAllowCreate() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_POLICY_ALLOW_CREATE)
            .orElse(BooleanUtils.toBoolean(configuration.getNameIdPolicyAllowCreate()));
    }

    /**
     * <p>getProviderName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getProviderName() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_PROVIDER_NAME)
            .orElse(configuration.getProviderName());
    }

    /**
     * <p>getIssuerFormat.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIssuerFormat() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_ISSUER_FORMAT)
            .orElse(configuration.getIssuerFormat());
    }

    /**
     * <p>isUseNameQualifier.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean isUseNameQualifier() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_USE_NAME_QUALIFIER)
            .orElse(BooleanUtils.toBoolean(configuration.isUseNameQualifier()));
    }


    /**
     * <p>getAuthnContextClassRefs.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getAuthnContextClassRefs() {
        return (List<String>) webContext.getRequestAttribute(REQUEST_ATTR_AUTHN_CONTEXT_CLASS_REFS)
            .orElse(configuration.getAuthnContextClassRefs());
    }

    /**
     * <p>isPassive.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean isPassive() {
        return webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE).isPresent()
            || BooleanUtils.toBoolean(configuration.isPassive());
    }

    /**
     * <p>isForceAuth.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean isForceAuth() {
        return webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN).isPresent()
            || BooleanUtils.toBoolean(configuration.isForceAuth());
    }

    /**
     * <p>getNameIdAttribute.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getNameIdAttribute() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_ATTRIBUTE)
            .orElse(configuration.getNameIdAttribute());
    }

    /**
     * <p>isWantsAssertionsSigned.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean isWantsAssertionsSigned() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_WANTS_ASSERTIONS_SIGNED)
            .orElse(BooleanUtils.toBoolean(configuration.isWantsAssertionsSigned()));
    }

    /**
     * <p>getMaximumAuthenticationLifetime.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getMaximumAuthenticationLifetime() {
        return (Long) webContext.getRequestAttribute(REQUEST_ATTR_MAXIMUM_AUTHENTICATION_LIFETIME)
            .orElse(configuration.getMaximumAuthenticationLifetime());
    }

    /**
     * <p>isWantsResponsesSigned.</p>
     *
     * @return a boolean
     */
    public boolean isWantsResponsesSigned() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_WANTS_RESPONSES_SIGNED)
            .orElse(configuration.isWantsResponsesSigned());
    }
}
