package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.Google2Client;
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
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GOOGLE_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GOOGLE_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class GoogleClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public Google2Client google2Client(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.GoogleProperties facebook = cfg.getGoogle();
        properties.put(PropertiesConstants.GOOGLE_ID, StringUtils.defaultString(facebook.getId()));
        properties.put(PropertiesConstants.GOOGLE_SCOPE, StringUtils.defaultString(facebook.getScope()));
        properties.put(PropertiesConstants.GOOGLE_SECRET, StringUtils.defaultString(facebook.getSecret()));

        final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateGoogleClient(clients);
        if (!clients.isEmpty()) {
            return Google2Client.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create google2Client bean");
    }
}
