package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.GitHubClient;
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
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GITHUB_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.GITHUB_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class GitHubClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public GitHubClient gitHubClient(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.GitHubProperties twitter = cfg.getGitHub();
        properties.put(PropertiesConstants.GITHUB_ID, StringUtils.defaultString(twitter.getId()));
        properties.put(PropertiesConstants.GITHUB_SECRET, StringUtils.defaultString(twitter.getSecret()));

        final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateGithubClient(clients);
        if (!clients.isEmpty()) {
            return GitHubClient.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create gitHubClient bean");
    }
}
