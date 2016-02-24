package org.pac4j.config.client;

import com.nimbusds.jose.JWSAlgorithm;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Build a configuration from properties.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class PropertiesConfigFactory implements ConfigFactory {

    public final static String FACEBOOK_ID = "facebook.id";
    public final static String FACEBOOK_SECRET = "facebook.secret";
    public final static String FACEBOOK_SCOPE = "facebook.scope";
    public final static String FACEBOOK_FIELDS = "facebook.fields";

    public final static String TWITTER_ID = "twitter.id";
    public final static String TWITTER_SECRET = "twitter.secret";

    public final static String SAML_KEYSTORE_PASSWORD = "saml.keystorePassword";
    public final static String SAML_PRIVATE_KEY_PASSWORD = "saml.privateKeyPassword";
    public final static String SAML_KEYSTORE_PATH = "saml.keystorePath";
    public final static String SAML_IDENTITY_PROVIDER_METADATA_PATH = "saml.identityProviderMetadataPath";
    public final static String SAML_MAXIMUM_AUTHENTICATION_LIFETIME = "saml.maximumAuthenticationLifetime";
    public final static String SAML_SERVICE_PROVIDER_ENTITY_ID = "saml.serviceProviderEntityId";
    public final static String SAML_SERVICE_PROVIDER_METADATA_PATH = "saml.serviceProviderMetadataPath";

    public final static String CAS_LOGIN_URL = "cas.loginUrl";
    public final static String CAS_PROTOCOL = "cas.protocol";

    public final static String OIDC_ID = "oidc.id";
    public final static String OIDC_SECRET = "oidc.secret";
    public final static String OIDC_DISCOVERY_URI = "oidc.discoveryUri";
    public final static String OIDC_USE_NONCE = "oidc.useNonce";
    public final static String OIDC_PREFERRED_JWS_ALGORITHM = "oidc.preferredJwsAlgorithm";
    public final static String OIDC_MAX_CLOCK_SKEW = "oidc.maxClockSkew";
    public final static String OIDC_CUSTOM_PARAM_KEY1 = "oidc.customParamKey1";
    public final static String OIDC_CUSTOM_PARAM_VALUE1 = "oidc.customParamValue1";
    public final static String OIDC_CUSTOM_PARAM_KEY2 = "oidc.customParamKey2";
    public final static String OIDC_CUSTOM_PARAM_VALUE2 = "oidc.customParamValue2";

    private final String callbackUrl;

    private final Map<String, String> properties;

    public PropertiesConfigFactory(final Map<String, String> properties) {
        this(null, properties);
    }

    public PropertiesConfigFactory(final String callbackUrl, final Map<String, String> properties) {
        this.callbackUrl = callbackUrl;
        this.properties = properties;
    }

    private String getProperty(final String name) {
        return properties.get(name);
    }

    public Config build() {
        final List<Client> clients = new ArrayList<>();
        tryCreateFacebookClient(clients);
        tryCreateTwitterClient(clients);
        tryCreateSaml2Client(clients);
        tryCreateCasClient(clients);
        tryCreateOidcClient(clients);
        return new Config(callbackUrl, clients);
    }

    private void tryCreateFacebookClient(final List<Client> clients) {
        final String id = getProperty(FACEBOOK_ID);
        final String secret = getProperty(FACEBOOK_SECRET);
        final String scope = getProperty(FACEBOOK_SCOPE);
        final String fields = getProperty(FACEBOOK_FIELDS);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final FacebookClient facebookClient = new FacebookClient(id, secret);
            if (CommonHelper.isNotBlank(scope)) {
                facebookClient.setScope(scope);
            }
            if (CommonHelper.isNotBlank(fields)) {
                facebookClient.setFields(fields);
            }
            clients.add(facebookClient);
        }
    }

    private void tryCreateTwitterClient(final List<Client> clients) {
        final String id = getProperty(TWITTER_ID);
        final String secret = getProperty(TWITTER_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final TwitterClient twitterClient = new TwitterClient(id, secret);
            clients.add(twitterClient);
        }
    }

    private void tryCreateSaml2Client(final List<Client> clients) {
        final String keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD);
        final String privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD);
        final String keystorePath = getProperty(SAML_KEYSTORE_PATH);
        final String ientityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH);
        final String maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME);
        final String serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID);
        final String serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH);
        if (CommonHelper.isNotBlank(keystorePassword) && CommonHelper.isNotBlank(privateKeyPassword)
                && CommonHelper.isNotBlank(keystorePath) && CommonHelper.isNotBlank(ientityProviderMetadataPath)) {
            final SAML2ClientConfiguration cfg = new SAML2ClientConfiguration(keystorePath, keystorePassword,
                    privateKeyPassword, ientityProviderMetadataPath);
            if (CommonHelper.isNotBlank(maximumAuthenticationLifetime)) {
                cfg.setMaximumAuthenticationLifetime(Integer.parseInt(maximumAuthenticationLifetime));
            }
            if (CommonHelper.isNotBlank(serviceProviderEntityId)) {
                cfg.setServiceProviderEntityId(serviceProviderEntityId);
            }
            if (CommonHelper.isNotBlank(serviceProviderMetadataPath)) {
                cfg.setServiceProviderMetadataPath(serviceProviderMetadataPath);
            }
            final SAML2Client saml2Client = new SAML2Client(cfg);
            clients.add(saml2Client);
        }
    }

    private void tryCreateCasClient(final List<Client> clients) {
        final String loginUrl = getProperty(CAS_LOGIN_URL);
        final String protocol = getProperty(CAS_PROTOCOL);
        if (CommonHelper.isNotBlank(loginUrl)) {
            final CasClient casClient = new CasClient(loginUrl);
            if (CommonHelper.isNotBlank(protocol)) {
                casClient.setCasProtocol(CasClient.CasProtocol.valueOf(protocol));
            }
            clients.add(casClient);
        }
    }

    private void tryCreateOidcClient(final List<Client> clients) {
        final String id = getProperty(OIDC_ID);
        final String secret = getProperty(OIDC_SECRET);
        final String discoveryUri = getProperty(OIDC_DISCOVERY_URI);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret) && CommonHelper.isNotBlank(discoveryUri)) {
            final OidcClient oidcClient = new OidcClient(id, secret, discoveryUri);
            final String useNonce = getProperty(OIDC_USE_NONCE);
            if (CommonHelper.isNotBlank(useNonce)) {
                oidcClient.setUseNonce(Boolean.parseBoolean(useNonce));
            }
            final String jwsAlgo = getProperty(OIDC_PREFERRED_JWS_ALGORITHM);
            if (CommonHelper.isNotBlank(jwsAlgo)) {
                oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.parse(jwsAlgo));
            }
            final String maxClockSkew = getProperty(OIDC_MAX_CLOCK_SKEW);
            if (CommonHelper.isNotBlank(maxClockSkew)) {
                oidcClient.setMaxClockSkew(Integer.parseInt(maxClockSkew));
            }
            final String key1 = getProperty(OIDC_CUSTOM_PARAM_KEY1);
            final String value1 = getProperty(OIDC_CUSTOM_PARAM_VALUE1);
            if (CommonHelper.isNotBlank(key1)) {
                oidcClient.addCustomParam(key1, value1);
            }
            final String key2 = getProperty(OIDC_CUSTOM_PARAM_KEY2);
            final String value2 = getProperty(OIDC_CUSTOM_PARAM_VALUE2);
            if (CommonHelper.isNotBlank(key2)) {
                oidcClient.addCustomParam(key2, value2);
            }
            clients.add(oidcClient);
        }
    }
}
