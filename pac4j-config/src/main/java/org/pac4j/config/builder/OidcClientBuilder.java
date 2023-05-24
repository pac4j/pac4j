package org.pac4j.config.builder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.oidc.client.AzureAd2Client;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.AzureAd2OidcConfiguration;
import org.pac4j.oidc.config.OidcConfiguration;

import java.util.Collection;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for OpenID connect clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OidcClientBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for OidcClientBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public OidcClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreateOidcClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateOidcClient(final Collection<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val id = getProperty(OIDC_ID, i);
            if (isNotBlank(id)) {
                val configuration = new OidcConfiguration();
                configuration.setClientId(id);

                val secret = getProperty(OIDC_SECRET, i);
                if (isNotBlank(secret)) {
                    configuration.setSecret(secret);
                }
                val scope = getProperty(OIDC_SCOPE, i);
                if (isNotBlank(scope)) {
                    configuration.setScope(scope);
                }
                val discoveryUri = getProperty(OIDC_DISCOVERY_URI, i);
                if (isNotBlank(discoveryUri)) {
                    configuration.setDiscoveryURI(discoveryUri);
                }
                val responseType = getProperty(OIDC_RESPONSE_TYPE, i);
                if (isNotBlank(responseType)) {
                    configuration.setResponseType(responseType);
                }
                val responseMode = getProperty(OIDC_RESPONSE_MODE, i);
                if (isNotBlank(responseMode)) {
                    configuration.setResponseMode(responseMode);
                }
                val useNonce = getProperty(OIDC_USE_NONCE, i);
                if (isNotBlank(useNonce)) {
                    configuration.setUseNonce(Boolean.parseBoolean(useNonce));
                }
                val jwsAlgo = getProperty(OIDC_PREFERRED_JWS_ALGORITHM, i);
                if (isNotBlank(jwsAlgo)) {
                    configuration.setPreferredJwsAlgorithm(JWSAlgorithm.parse(jwsAlgo));
                }
                val maxClockSkew = getProperty(OIDC_MAX_CLOCK_SKEW, i);
                if (isNotBlank(maxClockSkew)) {
                    configuration.setMaxClockSkew(Integer.parseInt(maxClockSkew));
                }
                val clientAuthenticationMethod = getProperty(OIDC_CLIENT_AUTHENTICATION_METHOD, i);
                if (isNotBlank(clientAuthenticationMethod)) {
                    configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.parse(clientAuthenticationMethod));
                }
                for (var j = 1; j <= MAX_NUM_CUSTOM_PROPERTIES; j++) {
                    if (containsProperty(OIDC_CUSTOM_PARAM_KEY + j, i)) {
                        configuration.addCustomParam(getProperty(OIDC_CUSTOM_PARAM_KEY + j, i),
                            getProperty(OIDC_CUSTOM_PARAM_VALUE + j, i));
                    }
                }

                val type = getProperty(OIDC_TYPE, i);
                final OidcClient oidcClient;
                if (OIDC_AZURE_TYPE.equalsIgnoreCase(type)) {
                    val azureAdConfiguration = new AzureAd2OidcConfiguration(configuration);
                    val tenant = getProperty(OIDC_AZURE_TENANT, i);
                    if (isNotBlank(tenant)) {
                        azureAdConfiguration.setTenant(tenant);
                    }
                    oidcClient = new AzureAd2Client(azureAdConfiguration);
                } else if (OIDC_GOOGLE_TYPE.equalsIgnoreCase(type)) {
                    oidcClient = new GoogleOidcClient(configuration);
                } else {
                    oidcClient = new OidcClient(configuration);
                }
                oidcClient.setName(concat(oidcClient.getName(), i));
                clients.add(oidcClient);
            }
        }
    }
}
