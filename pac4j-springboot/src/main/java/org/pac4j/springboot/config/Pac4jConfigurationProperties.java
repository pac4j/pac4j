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

    /**
     * <p>Getter for the field <code>properties</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * <p>Setter for the field <code>properties</code>.</p>
     *
     * @param properties a {@link java.util.Map} object
     */
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * <p>Getter for the field <code>callbackUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCallbackUrl() {
        return callbackUrl;
    }

    /**
     * <p>Setter for the field <code>callbackUrl</code>.</p>
     *
     * @param callbackUrl a {@link java.lang.String} object
     */
    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
