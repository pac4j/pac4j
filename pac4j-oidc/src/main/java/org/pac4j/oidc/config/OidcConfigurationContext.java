package org.pac4j.oidc.config;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectionActionBuilder;

import java.util.Map;

/**
 * This is {@link OidcConfigurationContext}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@SuppressWarnings("unchecked")
public class OidcConfigurationContext {
    private final WebContext context;
    private final OidcConfiguration configuration;

    public OidcConfigurationContext(final WebContext webContext,
                                    final OidcConfiguration oidcConfiguration) {
        this.context = webContext;
        this.configuration = oidcConfiguration;
    }

    public Integer getMaxAge() {
        return (Integer) context.getRequestAttribute(OidcConfiguration.MAX_AGE)
            .orElse(configuration.getMaxAge());
    }

    public Boolean isForceAuthn() {
        return context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN)
            .isPresent();
    }

    public Boolean isPassive() {
        return context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE)
            .isPresent();
    }

    public String getScope() {
        return (String) context.getRequestAttribute(OidcConfiguration.SCOPE).orElse("openid profile email");
    }

    public String getResponseType() {
        return (String) context.getRequestAttribute(OidcConfiguration.RESPONSE_TYPE)
            .orElse(configuration.getResponseType());
    }

    public String getResponseMode() {
        return (String) context.getRequestAttribute(OidcConfiguration.RESPONSE_MODE)
            .orElse(configuration.getResponseMode());
    }

    public Map<String, String> getCustomParams() {
        return (Map<String, String>) context.getRequestAttribute(OidcConfiguration.CUSTOM_PARAMS)
            .orElse(configuration.getCustomParams());
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }
}
