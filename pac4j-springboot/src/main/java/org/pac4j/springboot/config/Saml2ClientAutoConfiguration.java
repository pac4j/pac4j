package org.pac4j.springboot.config;

import org.apache.commons.lang.StringUtils;
import org.pac4j.config.builder.Saml2ClientBuilder;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.saml.client.SAML2Client;
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
    Pac4jConfigurationProperties.PREFIX + ".saml2.identityProviderMetadataPath",
    Pac4jConfigurationProperties.PREFIX + ".saml2.keystorePath",
    Pac4jConfigurationProperties.PREFIX + ".saml2.keystorePassword",
    Pac4jConfigurationProperties.PREFIX + ".saml2.privateKeyPassword",
    Pac4jConfigurationProperties.PREFIX + ".saml2.serviceProviderEntityId",
    Pac4jConfigurationProperties.PREFIX + ".saml2.identityProviderMetadataPath"
})
@EnableConfigurationProperties(Pac4jConfigurationProperties.class)
public class Saml2ClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    @Autowired
    public SAML2Client saml2Client(final Pac4jConfigurationProperties cfg) {
        final Map<String, String> properties = new HashMap<>();
        final Pac4jConfigurationProperties.Saml2Properties oauth = cfg.getSaml2();
        properties.put(PropertiesConstants.SAML_IDENTITY_PROVIDER_METADATA_PATH,
            StringUtils.defaultString(oauth.getIdentityProviderMetadataPath()));
        properties.put(PropertiesConstants.SAML_KEYSTORE_PATH,
            StringUtils.defaultString(oauth.getKeystorePath()));
        properties.put(PropertiesConstants.SAML_SERVICE_PROVIDER_ENTITY_ID,
            StringUtils.defaultString(oauth.getServiceProviderEntityId()));
        properties.put(PropertiesConstants.SAML_SERVICE_PROVIDER_METADATA_PATH,
            StringUtils.defaultString(oauth.getServiceProviderMetadataPath()));
        properties.put(PropertiesConstants.SAML_KEYSTORE_PASSWORD,
            StringUtils.defaultString(oauth.getKeystorePassword()));
        properties.put(PropertiesConstants.SAML_PRIVATE_KEY_PASSWORD,
            StringUtils.defaultString(oauth.getPrivateKeyPassword()));
        properties.put(PropertiesConstants.SAML_MAXIMUM_AUTHENTICATION_LIFETIME,
            StringUtils.defaultString(oauth.getMaximumAuthenticationLifetime()));
        properties.put(PropertiesConstants.SAML_AUTHN_REQUEST_BINDING_TYPE,
            StringUtils.defaultString(oauth.getAuthnRequestBindingType()));
        properties.put(PropertiesConstants.SAML_KEYSTORE_ALIAS,
            StringUtils.defaultString(oauth.getKeystoreAlias()));

        final Saml2ClientBuilder builder = new Saml2ClientBuilder(properties);
        final List<Client> clients = new ArrayList<>();
        builder.tryCreateSaml2Client(clients);
        if (!clients.isEmpty()) {
            return SAML2Client.class.cast(clients.get(0));
        }
        throw new BeanCreationException("Unable to create saml2Client bean");
    }
}
