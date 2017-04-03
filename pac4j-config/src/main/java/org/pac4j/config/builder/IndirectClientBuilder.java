package org.pac4j.config.builder;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for indirect clients regarding, SAML, CAS and OpenID Connect.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class IndirectClientBuilder extends AbstractBuilder {

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
    public static final String OIDC_SCOPE = "oidc.scope";
    public static final String OIDC_DISCOVERY_URI = "oidc.discoveryUri";
    public static final String OIDC_USE_NONCE = "oidc.useNonce";
    public static final String OIDC_PREFERRED_JWS_ALGORITHM = "oidc.preferredJwsAlgorithm";
    public static final String OIDC_MAX_CLOCK_SKEW = "oidc.maxClockSkew";
    public static final String OIDC_CLIENT_AUTHENTICATION_METHOD = "oidc.clientAuthenticationMethod";
    public static final String OIDC_CUSTOM_PARAM_KEY1 = "oidc.customParamKey1";
    public static final String OIDC_CUSTOM_PARAM_VALUE1 = "oidc.customParamValue1";
    public static final String OIDC_CUSTOM_PARAM_KEY2 = "oidc.customParamKey2";
    public static final String OIDC_CUSTOM_PARAM_VALUE2 = "oidc.customParamValue2";

    public static final String LOGINFORM_AUTHENTICATOR = "loginForm.authenticator";
    public static final String LOGINFORM_LOGIN_URL = "loginForm.loginUrl";
    public static final String LOGINFORM_USERNAME_PARAMETER = "loginForm.usernameParameter";
    public static final String LOGINFORM_PASSWORD_PARAMETER = "loginForm.passwordParameter";

    public static final String INDIRECTBASICAUTH_AUTHENTICATOR = "indirectBasicAuth.authenticator";
    public static final String INDIRECTBASICAUTH_REALM_NAME = "indirectBasicAuth.realName";

    public IndirectClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateSaml2Client(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD, i);
            final String privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD, i);
            final String keystorePath = getProperty(SAML_KEYSTORE_PATH, i);
            final String ientityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH, i);
            final String maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME, i);
            final String serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID, i);
            final String serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH, i);
            final String destinationBindingType = getProperty(SAML_DESTINATION_BINDING_TYPE, i);

            if (isNotBlank(keystorePassword) && isNotBlank(privateKeyPassword)
                    && isNotBlank(keystorePath) && isNotBlank(ientityProviderMetadataPath)) {
                final SAML2ClientConfiguration cfg = new SAML2ClientConfiguration(keystorePath, keystorePassword,
                        privateKeyPassword, ientityProviderMetadataPath);
                if (isNotBlank(maximumAuthenticationLifetime)) {
                    cfg.setMaximumAuthenticationLifetime(Integer.parseInt(maximumAuthenticationLifetime));
                }
                if (isNotBlank(serviceProviderEntityId)) {
                    cfg.setServiceProviderEntityId(serviceProviderEntityId);
                }
                if (isNotBlank(serviceProviderMetadataPath)) {
                    cfg.setServiceProviderMetadataPath(serviceProviderMetadataPath);
                }
                if (isNotBlank(destinationBindingType)) {
                    cfg.setDestinationBindingType(destinationBindingType);
                }
                final SAML2Client saml2Client = new SAML2Client(cfg);

                if (i != 0) {
                    saml2Client.setName(concat(saml2Client.getName(), i));
                }

                clients.add(saml2Client);
            }
        }
    }

    public void tryCreateCasClient(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String loginUrl = getProperty(CAS_LOGIN_URL, i);
            final String protocol = getProperty(CAS_PROTOCOL, i);
            if (isNotBlank(loginUrl)) {
                CasConfiguration configuration = new CasConfiguration();
                final CasClient casClient = new CasClient(configuration);
                configuration.setLoginUrl(loginUrl);
                if (isNotBlank(protocol)) {
                    configuration.setProtocol(CasProtocol.valueOf(protocol));
                }
                if (i != 0) {
                    casClient.setName(concat(casClient.getName(), i));
                }
                clients.add(casClient);
            }
        }
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

    public void tryCreateLoginFormClient(final List<Client> clients, final Map<String, Authenticator> authenticators) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String loginUrl = getProperty(LOGINFORM_LOGIN_URL, i);
            final String authenticator = getProperty(LOGINFORM_AUTHENTICATOR, i);
            if (isNotBlank(loginUrl) && isNotBlank(authenticator)) {
                final FormClient formClient = new FormClient();
                formClient.setLoginUrl(loginUrl);
                formClient.setAuthenticator(authenticators.get(authenticator));
                if (containsProperty(LOGINFORM_USERNAME_PARAMETER, i)) {
                    formClient.setUsernameParameter(getProperty(LOGINFORM_USERNAME_PARAMETER, i));
                }
                if (containsProperty(LOGINFORM_PASSWORD_PARAMETER, i)) {
                    formClient.setPasswordParameter(getProperty(LOGINFORM_PASSWORD_PARAMETER, i));
                }
                if (i != 0) {
                    formClient.setName(concat(formClient.getName(), i));
                }
                clients.add(formClient);
            }
        }
    }

    public void tryCreateIndirectBasciAuthClient(final List<Client> clients, final Map<String, Authenticator> authenticators) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String authenticator = getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i);
            if (isNotBlank(authenticator)) {
                final IndirectBasicAuthClient indirectBasicAuthClient = new IndirectBasicAuthClient();
                indirectBasicAuthClient.setAuthenticator(authenticators.get(authenticator));
                if (containsProperty(INDIRECTBASICAUTH_REALM_NAME, i)) {
                    indirectBasicAuthClient.setRealmName(getProperty(INDIRECTBASICAUTH_REALM_NAME, i));
                }
                if (i != 0) {
                    indirectBasicAuthClient.setName(concat(indirectBasicAuthClient.getName(), i));
                }
                clients.add(indirectBasicAuthClient);
            }
        }
    }
}
