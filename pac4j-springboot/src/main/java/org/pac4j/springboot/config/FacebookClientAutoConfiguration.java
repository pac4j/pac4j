package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.FacebookClient;
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
    Pac4jConfigurationProperties.PREFIX + '.'+ PropertiesConstants.FACEBOOK_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.FACEBOOK_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class FacebookClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public FacebookClient facebookClient(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.FacebookProperties facebook = cfg.getFacebook();
        properties.put(PropertiesConstants.FACEBOOK_ID, StringUtils.defaultString(facebook.getId()));
        properties.put(PropertiesConstants.FACEBOOK_SECRET, StringUtils.defaultString(facebook.getSecret()));
        properties.put(PropertiesConstants.FACEBOOK_SCOPE, StringUtils.defaultString(facebook.getScope()));
        properties.put(PropertiesConstants.FACEBOOK_FIELDS, StringUtils.defaultString(facebook.getFields()));

        final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateFacebookClient(clients);
        if (!clients.isEmpty()) {
            return FacebookClient.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create facebookClient bean");
    }
}
