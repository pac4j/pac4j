package org.pac4j.oidc.metadata;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.resource.SpringResourceLoader;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcUnsupportedClientAuthMethodException;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HttpsURLConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.PrivateKey;
import java.security.Provider;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;
import org.pac4j.oidc.config.PrivateKeyJWTClientAuthnMethodConfig;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.JWTID;

/**
 * The metadata resolver for the OIDC OP.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
public class OidcOpMetadataResolver extends SpringResourceLoader<OIDCProviderMetadata> {

    private static final Collection<ClientAuthenticationMethod> SUPPORTED_METHODS =
        Arrays.asList(
            ClientAuthenticationMethod.CLIENT_SECRET_POST,
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
            ClientAuthenticationMethod.PRIVATE_KEY_JWT,
            ClientAuthenticationMethod.NONE);

    protected final OidcConfiguration configuration;

    @Getter
    protected ClientAuthentication clientAuthentication;

    @Getter
    protected TokenValidator tokenValidator;

    /**
     * <p>Constructor for OidcOpMetadataResolver.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public OidcOpMetadataResolver(final OidcConfiguration configuration) {
        super(buildResource(configuration));
        this.configuration = configuration;
    }

    private static Resource buildResource(final OidcConfiguration configuration) {
        if (configuration != null && configuration.getDiscoveryURI() != null) {
            var resource = SpringResourceHelper.buildResourceFromPath(configuration.getDiscoveryURI());
            if (resource instanceof final UrlResource urlResource) {
                return new OidcMetadataUrlResource(urlResource.getURL(), configuration);
            } else {
                return resource;
            }
        }
        return null;
    }

    @Override
    protected void internalLoad() {
        this.loaded = retrieveMetadata();

        this.clientAuthentication = computeClientAuthentication();

        this.tokenValidator = createTokenValidator();
    }

    /**
     * <p>retrieveMetadata.</p>
     *
     * @return a {@link OIDCProviderMetadata} object
     */
    protected OIDCProviderMetadata retrieveMetadata() {
        try (val in = SpringResourceHelper.getResourceInputStream(
            resource,
            null,
            configuration.getSslSocketFactory(),
            configuration.getHostnameVerifier(),
            configuration.getConnectTimeout(),
            configuration.getReadTimeout()
        )) {
            val metadata = IOUtils.readInputStreamToString(in);
            return OIDCProviderMetadata.parse(metadata);
        } catch (final IOException | ParseException e) {
            throw new OidcException("Error getting OP metadata", e);
        }
    }

    /**
     * <p>computeClientAuthentication.</p>
     *
     * @return a {@link ClientAuthentication} object
     */
    protected ClientAuthentication computeClientAuthentication() {
        val _clientID = new ClientID(configuration.getClientId());

        if (configuration.getSecret() != null || configuration.getPrivateKeyJWTClientAuthnMethodConfig() != null) {
            // check authentication methods
            val serverSupportedAuthMethods = this.loaded.getTokenEndpointAuthMethods();
            val preferredMethod = getPreferredAuthenticationMethod(configuration);

            final ClientAuthenticationMethod chosenMethod;
            if (isNotEmpty(serverSupportedAuthMethods)) {
                if (preferredMethod != null) {
                    if (serverSupportedAuthMethods.contains(preferredMethod)) {
                        chosenMethod = preferredMethod;
                    } else {
                        throw new OidcUnsupportedClientAuthMethodException(
                            "Preferred authentication method (" + preferredMethod + ") not supported "
                                + "by provider according to provider metadata (" + serverSupportedAuthMethods + ").");
                    }
                } else {
                    chosenMethod = firstSupportedMethod(serverSupportedAuthMethods,
                        configuration.getSupportedClientAuthenticationMethods());
                }
            } else {
                chosenMethod = preferredMethod != null ? preferredMethod : ClientAuthenticationMethod.getDefault();
                LOGGER.info("Provider metadata does not provide Token endpoint authentication methods. Using: {}",
                    chosenMethod);
            }

            if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(chosenMethod)) {
                val _secret = new Secret(configuration.getSecret());
                return new ClientSecretPost(_clientID, _secret);
            } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(chosenMethod)) {
                val _secret = new Secret(configuration.getSecret());
                return new ClientSecretBasic(_clientID, _secret);
            } else if (ClientAuthenticationMethod.PRIVATE_KEY_JWT.equals(chosenMethod)) {
                val privateKeyJwtConfig = configuration.getPrivateKeyJWTClientAuthnMethodConfig();
                assertNotNull("privateKeyJwtConfig", privateKeyJwtConfig);
                val jwsAlgo = privateKeyJwtConfig.getJwsAlgorithm();
                assertNotNull("privateKeyJwtConfig.getJwsAlgorithm()", jwsAlgo);
                val privateKey = privateKeyJwtConfig.getPrivateKey();
                assertNotNull("privateKeyJwtConfig.getPrivateKey()", privateKey);
                val keyID = privateKeyJwtConfig.getKeyID();
                val validity = privateKeyJwtConfig.getValidity();
                try {
                    return createPrivateKeyJWT(_clientID, this.loaded.getTokenEndpointURI(), jwsAlgo, privateKey, keyID, validity, null);
                } catch (final JOSEException e) {
                    throw new OidcException("Cannot instantiate private key JWT client authentication method", e);
                }
            } else {
                throw new OidcUnsupportedClientAuthMethodException("Unsupported client authentication method: " + chosenMethod);
            }
        }
        return null;
    }

    private static ClientAuthenticationMethod getPreferredAuthenticationMethod(OidcConfiguration config) {
        val configurationMethod = config.getClientAuthenticationMethod();
        if (configurationMethod == null) {
            return null;
        }

        if (!SUPPORTED_METHODS.contains(configurationMethod)) {
            throw new OidcUnsupportedClientAuthMethodException("Configured authentication method (" + configurationMethod +
                ") is not supported.");
        }

        return configurationMethod;
    }

    private static ClientAuthenticationMethod firstSupportedMethod(
        final Collection<ClientAuthenticationMethod> serverSupportedAuthMethods,
        Collection<ClientAuthenticationMethod> clientSupportedAuthMethods) {
        Collection<ClientAuthenticationMethod> supportedMethods =
            clientSupportedAuthMethods != null ? clientSupportedAuthMethods : SUPPORTED_METHODS;
        var firstSupported =
            serverSupportedAuthMethods.stream().filter(supportedMethods::contains).findFirst();
        if (firstSupported.isPresent()) {
            return firstSupported.get();
        } else {
            throw new OidcUnsupportedClientAuthMethodException("None of the Token endpoint provider metadata authentication methods are "
                + "supported: " + serverSupportedAuthMethods);
        }
    }

    protected TokenValidator createTokenValidator() {
        return new TokenValidator(configuration, this.loaded);
    }

    /**
     * Check if the PrivateKeyJWK is expired
     * @param privateKey the key to test
     * @return true if expired
     */
    private boolean isJWTExpired(PrivateKeyJWT privateKey, PrivateKeyJWTClientAuthnMethodConfig privateKeyJwtConfig) {
        try {
            // Gets expiration time in claims 
            // (claims can't be null they are built in constructor or it generates an IllegalArgumentException)
            var expirationTime = privateKey.getJWTAuthenticationClaimsSet().getExpirationTime();
            // Check if the JWT is expired
            if (expirationTime == null) {
                // No expiration date, not expired
                return false; 
            }
            // Check if expiration time is greater than now + some seconds (we use the same clock skew used to check idToken)
            int clockSkew = privateKeyJwtConfig.getKeyClockSkew();
            var nowWithTolerance = Instant.now();
            // Adds clockSkew if defined with a positive value
            if (clockSkew > 0) {
                nowWithTolerance = nowWithTolerance.plusSeconds(clockSkew);
            }
            // Return true for expired if expirationTime is before now
            return expirationTime.before(Date.from(nowWithTolerance));
        } catch (RuntimeException e) {
            throw new OidcException("An unexpected error occured while checking PrivateKeyJWT expiration occurred.", e);
        }
    }

    /**
     * Gets the clientAuthentication
     * If the the clientAuthentication is a PrivateKeyJWT, check if it expired
     * @return In most cases returns clientAuthentication, except for expired PrivateKeyJWT
     */
    public ClientAuthentication getClientAuthentication() {
        // Gets result of super method
        ClientAuthentication auth = clientAuthentication;
  
        var privateKeyJwtConfig = configuration.getPrivateKeyJWTClientAuthnMethodConfig();
        if (privateKeyJwtConfig != null 
            && privateKeyJwtConfig.isUseExpiration()
            && auth instanceof PrivateKeyJWT pvk
            && isJWTExpired(pvk, privateKeyJwtConfig)) {
            // We have a private JWT Token configuration and an expired token
            // recreate expired PrivateKeyJWT tokens
            val jwsAlgo = privateKeyJwtConfig.getJwsAlgorithm();
            val privateKey = privateKeyJwtConfig.getPrivateKey();
            val keyID = privateKeyJwtConfig.getKeyID();
            val validity = privateKeyJwtConfig.getValidity();
            try {
                var newPvk = createPrivateKeyJWT(pvk.getClientID(), this.loaded.getTokenEndpointURI(), 
                    jwsAlgo, privateKey, keyID, validity, null);
                clientAuthentication = newPvk;
                return newPvk;
            } catch (final JOSEException e) {
                throw new OidcException("Cannot renew private key JWT client authentication method", e);
            }     
        }
        return auth;
    } 

    /**
     * Create a private key JWT
     * Permits to sets the validity of the key
     * @param clientID The client identifier. Used to specify the issuer and the subject
     * @param audienceURI The audience
     * @param jwsAlgorithm The expected RSA or EC signature algorithm (RS256, ...)
     * @param privateKey the private key for signing (RSA ou EC)
     * @param keyID Optional identifier for the key
     * @param keyValidity validity of the key in seconds (the stored validity is no more precise)
     * @param jcaProvider Optional JCA provider
     */
    private PrivateKeyJWT createPrivateKeyJWT(final ClientID clientID, final URI audienceURI,
        final JWSAlgorithm jwsAlgorithm,
        final PrivateKey privateKey,
        final String keyID,
        final long keyValidity,
        final Provider jcaProvider) throws JOSEException {

        var aud = new Audience(audienceURI);
        var expirationDate = new Date(new Date().getTime() + keyValidity * 1000L);
        var claimSet = new JWTAuthenticationClaimsSet(clientID, aud.toSingleAudienceList(), expirationDate, null, null, new JWTID());

        return new PrivateKeyJWT(claimSet, jwsAlgorithm, privateKey, keyID, null, null, jcaProvider);
    }


    @EqualsAndHashCode(callSuper = true)
    private static class OidcMetadataUrlResource extends UrlResource {
        private final OidcConfiguration configuration;

        public OidcMetadataUrlResource(final URL url, final OidcConfiguration configuration) {
            super(url);
            this.configuration = configuration;
        }

        @Override
        protected void customizeConnection(final URLConnection connection) throws IOException {
            if (connection instanceof final HttpsURLConnection httpsConnection) {
                if (configuration.getHostnameVerifier() != null) {
                    httpsConnection.setHostnameVerifier(configuration.getHostnameVerifier());
                }
                if (configuration.getSslSocketFactory() != null) {
                    httpsConnection.setSSLSocketFactory(configuration.getSslSocketFactory());
                }
            }
            super.customizeConnection(connection);
        }
    }
}
