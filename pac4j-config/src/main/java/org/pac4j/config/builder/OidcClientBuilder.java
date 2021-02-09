package org.pac4j.config.builder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.pac4j.core.client.Client;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.AzureAdOidcConfiguration;
import org.pac4j.oidc.config.OidcConfiguration;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for OpenID connect clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OidcClientBuilder extends AbstractBuilder {

    public OidcClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateOidcClient(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final var id = getProperty(OIDC_ID, i);
            final var secret = getProperty(OIDC_SECRET, i);
            if (isNotBlank(id) && isNotBlank(secret)) {
                final var configuration = new OidcConfiguration();
                configuration.setClientId(id);
                configuration.setSecret(secret);

                final var scope = getProperty(OIDC_SCOPE, i);
                if (isNotBlank(scope)) {
                    configuration.setScope(scope);
                }
                final var discoveryUri = getProperty(OIDC_DISCOVERY_URI, i);
                if (isNotBlank(discoveryUri)) {
                    configuration.setDiscoveryURI(discoveryUri);
                }
                final var useNonce = getProperty(OIDC_USE_NONCE, i);
                if (isNotBlank(useNonce)) {
                    configuration.setUseNonce(Boolean.parseBoolean(useNonce));
                }
                final var jwsAlgo = getProperty(OIDC_PREFERRED_JWS_ALGORITHM, i);
                if (isNotBlank(jwsAlgo)) {
                    configuration.setPreferredJwsAlgorithm(JWSAlgorithm.parse(jwsAlgo));
                }
                final var maxClockSkew = getProperty(OIDC_MAX_CLOCK_SKEW, i);
                if (isNotBlank(maxClockSkew)) {
                    configuration.setMaxClockSkew(Integer.parseInt(maxClockSkew));
                }
                final var clientAuthenticationMethod = getProperty(OIDC_CLIENT_AUTHENTICATION_METHOD, i);
                if (isNotBlank(clientAuthenticationMethod)) {
                    configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.parse(clientAuthenticationMethod));
                }
                for (var j = 1; j <= MAX_NUM_CUSTOM_PROPERTIES; j++) {
                    if (containsProperty(OIDC_CUSTOM_PARAM_KEY + j, i)) {
                        configuration.addCustomParam(getProperty(OIDC_CUSTOM_PARAM_KEY + j, i),
                            getProperty(OIDC_CUSTOM_PARAM_VALUE + j, i));
                    }
                }

                final var type = getProperty(OIDC_TYPE, i);
                final OidcClient oidcClient;
                if (OIDC_AZURE_TYPE.equalsIgnoreCase(type)) {
                    final var azureAdConfiguration = new AzureAdOidcConfiguration(configuration);
                    final var tenant = getProperty(OIDC_AZURE_TENANT, i);
                    if (isNotBlank(tenant)) {
                        azureAdConfiguration.setTenant(tenant);
                    }
                    oidcClient = new AzureAdClient(azureAdConfiguration);
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
