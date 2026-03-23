package org.pac4j.springboot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class will be removed in the next version 6.5.0.
 *
 * The pac4j configuration and callback URL.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@ConfigurationProperties(prefix = "pac4j", ignoreUnknownFields = false)
@Getter
@Setter
@Deprecated
public class Pac4jConfigurationProperties {
    private Map<String, String> properties = new LinkedHashMap<>();

    private String callbackUrl;
}
