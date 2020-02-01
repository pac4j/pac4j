package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.config.builder.CasClientBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.springboot.Pac4jConfigurationProperties;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnProperty({
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.CAS_LOGIN_URL,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.CAS_PROTOCOL
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class CasClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public CasClient casClient(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.CasProperties twitter = cfg.getCas();
        properties.put(PropertiesConstants.CAS_LOGIN_URL, StringUtils.defaultString(twitter.getLoginUrl()));
        properties.put(PropertiesConstants.CAS_PROTOCOL, StringUtils.defaultString(twitter.getProtocol()));

        final CasClientBuilder builder = new CasClientBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        builder.tryCreateCasClient(clients);
        if (!clients.isEmpty()) {
            return CasClient.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create casClient bean");
    }
}
