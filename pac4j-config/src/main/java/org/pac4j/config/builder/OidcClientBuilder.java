package org.pac4j.config.builder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.pac4j.config.client.PropertiesConstants;
import org.pac4j.core.client.Client;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
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
public class OidcClientBuilder extends AbstractBuilder implements PropertiesConstants {

    public OidcClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateOidcClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String id = getProperty(OIDC_ID, i);
            final String secret = getProperty(OIDC_SECRET, i);
            if (isNotBlank(id) && isNotBlank(secret)) {
                final OidcConfiguration configuration = new OidcConfiguration();
                configuration.setClientId(id);
                configuration.setSecret(secret);

                final String scope = getProperty(OIDC_SCOPE, i);
                if (isNotBlank(scope)) {
                    configuration.setScope(scope);
                }
                final String discoveryUri = getProperty(OIDC_DISCOVERY_URI, i);
                if (isNotBlank(discoveryUri)) {
                    configuration.setDiscoveryURI(discoveryUri);
                }
                final String useNonce = getProperty(OIDC_USE_NONCE, i);
                if (isNotBlank(useNonce)) {
                    configuration.setUseNonce(Boolean.parseBoolean(useNonce));
                }
                final String jwsAlgo = getProperty(OIDC_PREFERRED_JWS_ALGORITHM, i);
                if (isNotBlank(jwsAlgo)) {
                    configuration.setPreferredJwsAlgorithm(JWSAlgorithm.parse(jwsAlgo));
                }
                final String maxClockSkew = getProperty(OIDC_MAX_CLOCK_SKEW, i);
                if (isNotBlank(maxClockSkew)) {
                    configuration.setMaxClockSkew(Integer.parseInt(maxClockSkew));
                }
                final String clientAuthenticationMethod = getProperty(OIDC_CLIENT_AUTHENTICATION_METHOD, i);
                if (isNotBlank(clientAuthenticationMethod)) {
                    configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.parse(clientAuthenticationMethod));
                }
                final String key1 = getProperty(OIDC_CUSTOM_PARAM_KEY1, i);
                final String value1 = getProperty(OIDC_CUSTOM_PARAM_VALUE1, i);
                if (isNotBlank(key1)) {
                    configuration.addCustomParam(key1, value1);
                }
                final String key2 = getProperty(OIDC_CUSTOM_PARAM_KEY2, i);
                final String value2 = getProperty(OIDC_CUSTOM_PARAM_VALUE2, i);
                if (isNotBlank(key2)) {
                    configuration.addCustomParam(key2, value2);
                }

                final String type = getProperty(OIDC_TYPE, i);
                final OidcClient oidcClient;
                if (OIDC_AZURE_TYPE.equalsIgnoreCase(type)) {
                    oidcClient = new AzureAdClient(configuration);
                } else if (OIDC_GOOGLE_TYPE.equalsIgnoreCase(type)) {
                    oidcClient = new GoogleOidcClient(configuration);
                } else {
                    oidcClient = new OidcClient(configuration);
                }
                if (i != 0) {
                    oidcClient.setName(concat(oidcClient.getName(), i));
                }
                clients.add(oidcClient);
            }
        }
    }
}
