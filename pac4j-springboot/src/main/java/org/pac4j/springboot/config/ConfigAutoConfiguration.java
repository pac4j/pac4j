package org.pac4j.springboot.config;

import org.pac4j.config.client.PropertiesConfigFactory;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class for Spring.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@Configuration(value = "ConfigAutoConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class ConfigAutoConfiguration {

    @Autowired
    private Pac4jConfigurationProperties pac4j;

    /**
     * <p>config.</p>
     *
     * @return a {@link Config} object
     */
    @Bean
    @ConditionalOnMissingBean
    public Config config() {
        ConfigFactory factory = new PropertiesConfigFactory(pac4j.getCallbackUrl(), pac4j.getProperties());
        return factory.build();
    }
}
