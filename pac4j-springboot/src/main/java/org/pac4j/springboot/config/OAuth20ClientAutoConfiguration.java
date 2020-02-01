package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.GenericOAuth20Client;
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
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OAUTH2_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OAUTH2_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class OAuth20ClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public GenericOAuth20Client genericOAuth20Client(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.OAuth20Properties oauth = cfg.getOauth2();
        properties.put(PropertiesConstants.OAUTH2_ID, StringUtils.defaultString(oauth.getId()));
        properties.put(PropertiesConstants.OAUTH2_SECRET, StringUtils.defaultString(oauth.getSecret()));
        properties.put(PropertiesConstants.OAUTH2_AUTH_URL, StringUtils.defaultString(oauth.getAuthUrl()));
        properties.put(PropertiesConstants.OAUTH2_TOKEN_URL, StringUtils.defaultString(oauth.getTokenUrl()));
        properties.put(PropertiesConstants.OAUTH2_PROFILE_URL, StringUtils.defaultString(oauth.getProfileUrl()));
        properties.put(PropertiesConstants.OAUTH2_PROFILE_PATH, StringUtils.defaultString(oauth.getProfilePath()));
        properties.put(PropertiesConstants.OAUTH2_PROFILE_ID, StringUtils.defaultString(oauth.getProfileId()));
        properties.put(PropertiesConstants.OAUTH2_SCOPE, StringUtils.defaultString(oauth.getScope()));
        properties.put(PropertiesConstants.OAUTH2_WITH_STATE, StringUtils.defaultString(oauth.getWithState()));
        properties.put(PropertiesConstants.OAUTH2_CLIENT_AUTHENTICATION_METHOD,
            StringUtils.defaultString(oauth.getClientAuthenticationMethod()));

        final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateGenericOAuth2Clients(clients);
        if (!clients.isEmpty()) {
            return GenericOAuth20Client.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create genericOAuth20Client bean");
    }
}
