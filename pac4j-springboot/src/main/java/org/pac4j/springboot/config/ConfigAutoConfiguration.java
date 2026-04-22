package org.pac4j.springboot.config;

import org.pac4j.config.client.PropertiesConfigFactory;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.util.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class will be removed in the next version 6.5.0.
 *
 * The configuration class for Spring.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@Configuration(value = "ConfigAutoConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
@Deprecated
public class ConfigAutoConfiguration {

    private static final Announcement ANNOUNCEMENT =
        new Announcement("6.5.0", "the `pac4j-springboot` module will be removed").announce();

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
