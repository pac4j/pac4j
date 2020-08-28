package org.pac4j.core.http.callback;

import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link BaseCallbackUrlResolver}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
public abstract class BaseCallbackUrlResolver implements CallbackUrlResolver {
    /**
     * The Custom params.
     */
    protected Map<String, String> customParams = new HashMap<>();

    /**
     * Instantiates a new Base callback url resolver.
     */
    protected BaseCallbackUrlResolver() {
    }

    /**
     * Instantiates a new Base callback url resolver.
     *
     * @param config the config
     */
    protected BaseCallbackUrlResolver(final BaseClientConfiguration config) {
        if (config.getCustomParams() != null) {
            this.customParams = config.getCustomParams();
        }
    }

    /**
     * Instantiates a new Base callback url resolver.
     *
     * @param customParams the custom params
     */
    protected BaseCallbackUrlResolver(final Map<String, String> customParams) {
        this.customParams = customParams;
    }

    /**
     * Add custom params to the url.
     *
     * @param url the url
     * @return the url
     */
    protected String computeUrlCustomParams(final String url) {
        String newUrl = url;
        for (final Map.Entry<String, String> entry : this.customParams.entrySet()) {
            newUrl = CommonHelper.addParameter(newUrl, entry.getKey(), entry.getValue());
        }
        return newUrl;
    }
}
