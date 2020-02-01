package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.OidcClientBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oidc.client.OidcClient;
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
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OIDC_ID,
    Pac4jConfigurationProperties.PREFIX + '.' + PropertiesConstants.OIDC_SECRET
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class OidcClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public OidcClient oidClient(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.OidcProperties oauth = cfg.getOidc();
        properties.put(PropertiesConstants.OIDC_ID, StringUtils.defaultString(oauth.getId()));
        properties.put(PropertiesConstants.OIDC_SECRET, StringUtils.defaultString(oauth.getSecret()));
        properties.put(PropertiesConstants.OIDC_TYPE, StringUtils.defaultString(oauth.getType()));
        properties.put(PropertiesConstants.OIDC_AZURE_TENANT, StringUtils.defaultString(oauth.getAzureTenant()));
        properties.put(PropertiesConstants.OIDC_SCOPE, StringUtils.defaultString(oauth.getScope()));
        properties.put(PropertiesConstants.OIDC_DISCOVERY_URI, StringUtils.defaultString(oauth.getDiscoveryUri()));
        properties.put(PropertiesConstants.OIDC_USE_NONCE, StringUtils.defaultString(oauth.getUseNonce()));
        properties.put(PropertiesConstants.OIDC_PREFERRED_JWS_ALGORITHM,
            StringUtils.defaultString(oauth.getPreferredJwsAlgorithm()));
        properties.put(PropertiesConstants.OIDC_MAX_CLOCK_SKEW, StringUtils.defaultString(oauth.getMaxClockSkew()));
        properties.put(PropertiesConstants.OIDC_CLIENT_AUTHENTICATION_METHOD,
            StringUtils.defaultString(oauth.getClientAuthenticationMethod()));
        properties.put(PropertiesConstants.OIDC_CUSTOM_PARAM_KEY, StringUtils.defaultString(oauth.getCustomParamKey()));
        properties.put(PropertiesConstants.OIDC_CUSTOM_PARAM_VALUE, StringUtils.defaultString(oauth.getCustomParamValue()));

        final OidcClientBuilder builder = new OidcClientBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        builder.tryCreateOidcClient(clients);
        if (!clients.isEmpty()) {
            return OidcClient.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create oidClient bean");
    }
}
