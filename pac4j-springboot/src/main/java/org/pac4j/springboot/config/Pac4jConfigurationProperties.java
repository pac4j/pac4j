package org.pac4j.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The pac4j configuration and callback URL.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@ConfigurationProperties(prefix = "pac4j", ignoreUnknownFields = false)
public class Pac4jConfigurationProperties {
    private Map<String, String> properties = new LinkedHashMap<>();

    private String callbackUrl;

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
