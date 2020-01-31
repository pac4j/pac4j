package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.TwitterClient;
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
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.TWITTER_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.TWITTER_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class TwitterClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public TwitterClient twitterClient(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.TwitterProperties twitter = cfg.getTwitter();
        properties.put(PropertiesConstants.TWITTER_ID, StringUtils.defaultString(twitter.getId()));
        properties.put(PropertiesConstants.TWITTER_SECRET, StringUtils.defaultString(twitter.getSecret()));

        final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateTwitterClient(clients);
        if (!clients.isEmpty()) {
            return TwitterClient.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create twitterClient bean");
    }
}
