package org.pac4j.saml.context;

import org.apache.commons.lang3.BooleanUtils;
import org.opensaml.messaging.context.BaseContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.List;

/**
 * This is {@link SAML2ConfigurationContext}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@SuppressWarnings("unchecked")
public class SAML2ConfigurationContext extends BaseContext {
    public static final String REQUEST_ATTR_AUTHN_REQUEST_BINDING_TYPE = "AuthnRequestBindingType";
    public static final String REQUEST_ATTR_ASSERTION_CONSUMER_SERVICE_INDEX = "AssertionConsumerServiceIndex";
    public static final String REQUEST_ATTR_ATTRIBUTE_CONSUMING_SERVICE_INDEX = "AttributeConsumingServiceIndex";
    public static final String REQUEST_ATTR_COMPARISON_TYPE = "ComparisonType";
    public static final String REQUEST_ATTR_NAME_ID_POLICY_FORMAT = "NameIdPolicyFormat";
    public static final String REQUEST_ATTR_NAME_ID_POLICY_ALLOW_CREATE = "NameIdPolicyAllowCreate";
    public static final String REQUEST_ATTR_PROVIDER_NAME = "ProviderName";
    public static final String REQUEST_ATTR_ISSUER_FORMAT = "IssuerFormat";
    public static final String REQUEST_ATTR_USE_NAME_QUALIFIER = "UseNameQualifier";
    public static final String REQUEST_ATTR_AUTHN_CONTEXT_CLASS_REFS = "AuthnContextClassRefs";
    public static final String REQUEST_ATTR_NAME_ID_ATTRIBUTE = "NameIdAttribute";

    public static final String REQUEST_ATTR_WANTS_ASSERTIONS_SIGNED = "WantsAssertionsSigned";
    public static final String REQUEST_ATTR_WANTS_RESPONSES_SIGNED = "WantsResponsesSigned";
    public static final String REQUEST_ATTR_MAXIMUM_AUTHENTICATION_LIFETIME = "MaximumAuthenticationLifetime";

    private final WebContext webContext;
    private final SAML2Configuration configuration;

    public SAML2ConfigurationContext(final WebContext webContext,
                                     final SAML2Configuration configuration) {
        this.webContext = webContext;
        this.configuration = configuration;
    }

    public SAML2Configuration getSAML2Configuration() {
        return configuration;
    }

    public String getAuthnRequestBindingType() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_AUTHN_REQUEST_BINDING_TYPE)
            .orElse(configuration.getAuthnRequestBindingType());
    }

    public Integer getAssertionConsumerServiceIndex() {
        return (Integer) webContext.getRequestAttribute(REQUEST_ATTR_ASSERTION_CONSUMER_SERVICE_INDEX)
            .orElse(configuration.getAssertionConsumerServiceIndex());
    }

    public Integer getAttributeConsumingServiceIndex() {
        return (Integer) webContext.getRequestAttribute(REQUEST_ATTR_ATTRIBUTE_CONSUMING_SERVICE_INDEX)
            .orElse(configuration.getAttributeConsumingServiceIndex());
    }

    public String getComparisonType() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_COMPARISON_TYPE)
            .orElse(configuration.getComparisonType());
    }

    public String getNameIdPolicyFormat() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_POLICY_FORMAT)
            .orElse(configuration.getNameIdPolicyFormat());
    }

    public Boolean isNameIdPolicyAllowCreate() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_POLICY_ALLOW_CREATE)
            .orElse(BooleanUtils.toBoolean(configuration.getNameIdPolicyAllowCreate()));
    }

    public String getProviderName() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_PROVIDER_NAME)
            .orElse(configuration.getProviderName());
    }

    public String getIssuerFormat() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_ISSUER_FORMAT)
            .orElse(configuration.getIssuerFormat());
    }

    public Boolean isUseNameQualifier() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_USE_NAME_QUALIFIER)
            .orElse(BooleanUtils.toBoolean(configuration.isUseNameQualifier()));
    }


    public List<String> getAuthnContextClassRefs() {
        return (List<String>) webContext.getRequestAttribute(REQUEST_ATTR_AUTHN_CONTEXT_CLASS_REFS)
            .orElse(configuration.getAuthnContextClassRefs());
    }

    public Boolean isPassive() {
        return webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE).isPresent()
            || BooleanUtils.toBoolean(configuration.isPassive());
    }

    public Boolean isForceAuth() {
        return webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN).isPresent()
            || BooleanUtils.toBoolean(configuration.isForceAuth());
    }

    public String getNameIdAttribute() {
        return (String) webContext.getRequestAttribute(REQUEST_ATTR_NAME_ID_ATTRIBUTE)
            .orElse(configuration.getNameIdAttribute());
    }

    public Boolean isWantsAssertionsSigned() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_WANTS_ASSERTIONS_SIGNED)
            .orElse(BooleanUtils.toBoolean(configuration.isWantsAssertionsSigned()));
    }

    public Long getMaximumAuthenticationLifetime() {
        return (Long) webContext.getRequestAttribute(REQUEST_ATTR_MAXIMUM_AUTHENTICATION_LIFETIME)
            .orElse(configuration.getMaximumAuthenticationLifetime());
    }

    public boolean isWantsResponsesSigned() {
        return (Boolean) webContext.getRequestAttribute(REQUEST_ATTR_WANTS_RESPONSES_SIGNED)
            .orElse(configuration.isWantsResponsesSigned());
    }
}
