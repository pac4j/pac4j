package org.pac4j.config.client;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.DropBoxClient;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.FoursquareClient;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oauth.client.WindowsLiveClient;
import org.pac4j.oauth.client.YahooClient;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.oauth.client.LinkedIn2Client;

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

    public static final String FACEBOOK_ID = "facebook.id";
    public static final String FACEBOOK_SECRET = "facebook.secret";
    public static final String FACEBOOK_SCOPE = "facebook.scope";
    public static final String FACEBOOK_FIELDS = "facebook.fields";

    public static final String TWITTER_ID = "twitter.id";
    public static final String TWITTER_SECRET = "twitter.secret";

    public static final String GITHUB_ID = "github.id";
    public static final String GITHUB_SECRET = "github.secret";

    public static final String DROPBOX_ID = "dropbox.id";
    public static final String DROPBOX_SECRET = "dropbox.secret";

    public static final String WINDOWSLIVE_ID = "windowslive.id";
    public static final String WINDOWSLIVE_SECRET = "windowslive.secret";

    public static final String YAHOO_ID = "yahoo.id";
    public static final String YAHOO_SECRET = "yahoo.secret";

    public static final String FOURSQUARE_ID = "foursquare.id";
    public static final String FOURSQUARE_SECRET = "foursquare.secret";

    public static final String GOOGLE_ID = "google.id";
    public static final String GOOGLE_SECRET = "google.secret";
    public static final String GOOGLE_SCOPE = "google.scope";

    public static final String SAML_KEYSTORE_PASSWORD = "saml.keystorePassword";
    public static final String SAML_PRIVATE_KEY_PASSWORD = "saml.privateKeyPassword";
    public static final String SAML_KEYSTORE_PATH = "saml.keystorePath";
    public static final String SAML_IDENTITY_PROVIDER_METADATA_PATH = "saml.identityProviderMetadataPath";
    public static final String SAML_MAXIMUM_AUTHENTICATION_LIFETIME = "saml.maximumAuthenticationLifetime";
    public static final String SAML_SERVICE_PROVIDER_ENTITY_ID = "saml.serviceProviderEntityId";
    public static final String SAML_SERVICE_PROVIDER_METADATA_PATH = "saml.serviceProviderMetadataPath";
    public static final String SAML_DESTINATION_BINDING_TYPE = "saml.destinationBindingType";

    public static final String CAS_LOGIN_URL = "cas.loginUrl";
    public static final String CAS_PROTOCOL = "cas.protocol";

    public static final String OIDC_TYPE = "oidc.type";
    public static final String OIDC_GOOGLE_TYPE = "google";
    public static final String OIDC_AZURE_TYPE = "azure";
    public static final String OIDC_ID = "oidc.id";
    public static final String OIDC_SECRET = "oidc.secret";
    public static final String OIDC_DISCOVERY_URI = "oidc.discoveryUri";
    public static final String OIDC_USE_NONCE = "oidc.useNonce";
    public static final String OIDC_PREFERRED_JWS_ALGORITHM = "oidc.preferredJwsAlgorithm";
    public static final String OIDC_MAX_CLOCK_SKEW = "oidc.maxClockSkew";
    public static final String OIDC_CLIENT_AUTHENTICATION_METHOD = "oidc.clientAuthenticationMethod";
    public static final String OIDC_CUSTOM_PARAM_KEY1 = "oidc.customParamKey1";
    public static final String OIDC_CUSTOM_PARAM_VALUE1 = "oidc.customParamValue1";
    public static final String OIDC_CUSTOM_PARAM_KEY2 = "oidc.customParamKey2";
    public static final String OIDC_CUSTOM_PARAM_VALUE2 = "oidc.customParamValue2";

    public static final String LINKEDIN_ID = "linkedin.id";
    public static final String LINKEDIN_SECRET = "linkedin.secret";
    public static final String LINKEDIN_FIELDS = "linkedin.fields";
    public static final String LINKEDIN_SCOPE = "linkedin.scope";

    private static final int MAX_NUM_CLIENTS = 10;

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
        tryCreateDropboxClient(clients);
        tryCreateGithubClient(clients);
        tryCreateYahooClient(clients);
        tryCreateGoogleClient(clients);
        tryCreateFoursquareClient(clients);
        tryCreateWindowsLiveClient(clients);
        tryCreateLinkedInClient(clients);
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

    private void tryCreateWindowsLiveClient(final List<Client> clients) {
        final String id = getProperty(WINDOWSLIVE_ID);
        final String secret = getProperty(WINDOWSLIVE_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final WindowsLiveClient client = new WindowsLiveClient(id, secret);
            clients.add(client);
        }
    }

    private void tryCreateFoursquareClient(final List<Client> clients) {
        final String id = getProperty(FOURSQUARE_ID);
        final String secret = getProperty(FOURSQUARE_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final FoursquareClient client = new FoursquareClient(id, secret);
            clients.add(client);
        }
    }

    private void tryCreateGoogleClient(final List<Client> clients) {
        final String id = getProperty(GOOGLE_ID);
        final String secret = getProperty(GOOGLE_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final Google2Client client = new Google2Client(id, secret);
            final String scope = getProperty(GOOGLE_SCOPE);
            if (CommonHelper.isNotBlank(scope)) {
                client.setScope(Google2Client.Google2Scope.valueOf(scope.toUpperCase()));
            }
            clients.add(client);
        }
    }

    private void tryCreateYahooClient(final List<Client> clients) {
        final String id = getProperty(YAHOO_ID);
        final String secret = getProperty(YAHOO_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final YahooClient client = new YahooClient(id, secret);
            clients.add(client);
        }
    }

    private void tryCreateDropboxClient(final List<Client> clients) {
        final String id = getProperty(DROPBOX_ID);
        final String secret = getProperty(DROPBOX_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final DropBoxClient client = new DropBoxClient(id, secret);
            clients.add(client);
        }
    }

    private void tryCreateGithubClient(final List<Client> clients) {
        final String id = getProperty(GITHUB_ID);
        final String secret = getProperty(GITHUB_SECRET);
        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final GitHubClient client = new GitHubClient(id, secret);
            clients.add(client);
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
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD.concat(i == 0 ? "" : "." + i));
            final String privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD.concat(i == 0 ? "" : "." + i));
            final String keystorePath = getProperty(SAML_KEYSTORE_PATH.concat(i == 0 ? "" : "." + i));
            final String ientityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH.concat(i == 0 ? "" : "." + i));
            final String maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME.concat(i == 0 ? "" : "." + i));
            final String serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID.concat(i == 0 ? "" : "." + i));
            final String serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH.concat(i == 0 ? "" : "." + i));
            final String destinationBindingType = getProperty(SAML_DESTINATION_BINDING_TYPE.concat(i == 0 ? "" : "." + i));

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
                if (CommonHelper.isNotBlank(destinationBindingType)) {
                    cfg.setDestinationBindingType(destinationBindingType);
                }
                final SAML2Client saml2Client = new SAML2Client(cfg);

                if (i != 0) {
                    saml2Client.setName(saml2Client.getName().concat("." + i));
                }

                clients.add(saml2Client);
            }
        }
    }

    private void tryCreateCasClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String loginUrl = getProperty(CAS_LOGIN_URL.concat(i == 0 ? "" : "." + i));
            final String protocol = getProperty(CAS_PROTOCOL.concat(i == 0 ? "" : "." + i));
            if (CommonHelper.isNotBlank(loginUrl)) {
                CasConfiguration configuration = new CasConfiguration();
                final CasClient casClient = new CasClient(configuration);
                configuration.setLoginUrl(loginUrl);
                if (CommonHelper.isNotBlank(protocol)) {
                    configuration.setProtocol(CasProtocol.valueOf(protocol));
                }
                if (i != 0) {
                    casClient.setName(casClient.getName().concat("." + i));
                }
                clients.add(casClient);
            }
        }
    }

    private void tryCreateOidcClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String id = getProperty(OIDC_ID.concat(i == 0 ? "" : "." + i));
            final String secret = getProperty(OIDC_SECRET.concat(i == 0 ? "" : "." + i));
            if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
                final OidcConfiguration configuration = new OidcConfiguration();
                configuration.setClientId(id);
                configuration.setSecret(secret);
                final String discoveryUri = getProperty(OIDC_DISCOVERY_URI.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(discoveryUri)) {
                    configuration.setDiscoveryURI(discoveryUri);
                }
                final String useNonce = getProperty(OIDC_USE_NONCE.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(useNonce)) {
                    configuration.setUseNonce(Boolean.parseBoolean(useNonce));
                }
                final String jwsAlgo = getProperty(OIDC_PREFERRED_JWS_ALGORITHM.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(jwsAlgo)) {
                    configuration.setPreferredJwsAlgorithm(JWSAlgorithm.parse(jwsAlgo));
                }
                final String maxClockSkew = getProperty(OIDC_MAX_CLOCK_SKEW.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(maxClockSkew)) {
                    configuration.setMaxClockSkew(Integer.parseInt(maxClockSkew));
                }
                final String clientAuthenticationMethod = getProperty(OIDC_CLIENT_AUTHENTICATION_METHOD.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(clientAuthenticationMethod)) {
                    configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.parse(clientAuthenticationMethod));
                }
                final String key1 = getProperty(OIDC_CUSTOM_PARAM_KEY1.concat(i == 0 ? "" : "." + i));
                final String value1 = getProperty(OIDC_CUSTOM_PARAM_VALUE1.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(key1)) {
                    configuration.addCustomParam(key1, value1);
                }
                final String key2 = getProperty(OIDC_CUSTOM_PARAM_KEY2.concat(i == 0 ? "" : "." + i));
                final String value2 = getProperty(OIDC_CUSTOM_PARAM_VALUE2.concat(i == 0 ? "" : "." + i));
                if (CommonHelper.isNotBlank(key2)) {
                    configuration.addCustomParam(key2, value2);
                }

                final String type = getProperty(OIDC_TYPE.concat(i == 0 ? "" : "." + i));
                final OidcClient oidcClient;
                if (OIDC_AZURE_TYPE.equalsIgnoreCase(type)) {
                    oidcClient = new AzureAdClient(configuration);
                } else if (OIDC_GOOGLE_TYPE.equalsIgnoreCase(type)) {
                    oidcClient = new GoogleOidcClient(configuration);
                } else {
                    oidcClient = new OidcClient(configuration);
                }
                if (i != 0) {
                    oidcClient.setName(oidcClient.getName().concat("." + i));
                }
                clients.add(oidcClient);
            }
        }
    }

    private void tryCreateLinkedInClient(final List<Client> clients) {
        final String id = getProperty(LINKEDIN_ID);
        final String secret = getProperty(LINKEDIN_SECRET);
        final String scope = getProperty(LINKEDIN_SCOPE);
        final String fields = getProperty(LINKEDIN_FIELDS);

        if (CommonHelper.isNotBlank(id) && CommonHelper.isNotBlank(secret)) {
            final LinkedIn2Client linkedInClient = new LinkedIn2Client(id, secret);
            if (CommonHelper.isNotBlank(scope)) {
                linkedInClient.setScope(scope);
            }
            if (CommonHelper.isNotBlank(fields)) {
                linkedInClient.setFields(fields);
            }
            clients.add(linkedInClient);
        }
    }
}
