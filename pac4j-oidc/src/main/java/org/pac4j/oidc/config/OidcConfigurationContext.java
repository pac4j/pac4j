package org.pac4j.oidc.config;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectionActionBuilder;

import java.util.Map;
import java.util.Optional;

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

    /**
     * <p>Constructor for OidcConfigurationContext.</p>
     *
     * @param webContext a {@link WebContext} object
     * @param oidcConfiguration a {@link OidcConfiguration} object
     */
    public OidcConfigurationContext(final WebContext webContext,
                                    final OidcConfiguration oidcConfiguration) {
        this.context = webContext;
        this.configuration = oidcConfiguration;
    }

    /**
     * <p>getMaxAge.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getMaxAge() {
        return (Integer) context.getRequestAttribute(OidcConfiguration.MAX_AGE)
            .orElse(configuration.getMaxAge());
    }

    /**
     * <p>isForceAuthn.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isForceAuthn() {
        return context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN)
            .isPresent();
    }

    /**
     * <p>isPassive.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean isPassive() {
        return context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE)
            .isPresent();
    }

    /**
     * <p>getScope.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return (String) context.getRequestAttribute(OidcConfiguration.SCOPE)
            .or(() -> Optional.ofNullable(configuration.getScope()))
            .orElse("openid profile email");
    }

    /**
     * <p>getResponseType.</p>
     *
     * @return a {@link String} object
     */
    public String getResponseType() {
        return (String) context.getRequestAttribute(OidcConfiguration.RESPONSE_TYPE)
            .orElse(configuration.getResponseType());
    }

    /**
     * <p>getResponseMode.</p>
     *
     * @return a {@link String} object
     */
    public String getResponseMode() {
        return (String) context.getRequestAttribute(OidcConfiguration.RESPONSE_MODE)
            .orElse(configuration.getResponseMode());
    }

    /**
     * <p>getCustomParams.</p>
     *
     * @return a {@link Map} object
     */
    public Map<String, String> getCustomParams() {
        return (Map<String, String>) context.getRequestAttribute(OidcConfiguration.CUSTOM_PARAMS)
            .orElse(configuration.getCustomParams());
    }

    /**
     * <p>Getter for the field <code>configuration</code>.</p>
     *
     * @return a {@link OidcConfiguration} object
     */
    public OidcConfiguration getConfiguration() {
        return configuration;
    }
}
