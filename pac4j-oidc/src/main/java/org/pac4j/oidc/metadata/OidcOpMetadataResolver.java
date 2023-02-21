package org.pac4j.oidc.metadata;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.resource.SpringResourceLoader;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcUnsupportedClientAuthMethodException;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;

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

    public OidcOpMetadataResolver(final OidcConfiguration configuration) {
        super(buildResource(configuration));
        this.configuration = configuration;
    }

    private static Resource buildResource(final OidcConfiguration configuration) {
        if (configuration != null) {
            return SpringResourceHelper.buildResourceFromPath(configuration.getDiscoveryURI());
        }
        return null;
    }

    @Override
    protected void internalLoad() {
        this.loaded = retrieveMetadata();

        this.clientAuthentication = computeClientAuthentication();

        this.tokenValidator = new TokenValidator(configuration, this.loaded);
    }

    protected OIDCProviderMetadata retrieveMetadata() {
        val sslFactoryName = configuration.getSSLFactory();
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = sslFactoryName == null ? null : (SSLSocketFactory) CommonHelper.getConstructor(sslFactoryName).newInstance();
        } catch (final Exception e) {
            throw new OidcException(e);
        }
        try (val in = SpringResourceHelper.getResourceInputStream(
            resource,
            null,
            sslSocketFactory,
            null,
            configuration.getConnectTimeout(),
            configuration.getReadTimeout()
        )) {
            val metadata = IOUtils.readInputStreamToString(in);
            return OIDCProviderMetadata.parse(metadata);
        } catch (final IOException | ParseException e) {
            throw new OidcException("Error getting OP metadata", e);
        }
    }

    protected ClientAuthentication computeClientAuthentication() {
        val _clientID = new ClientID(configuration.getClientId());

        if (configuration.getSecret() != null) {
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
                try {
                    return new PrivateKeyJWT(_clientID, this.loaded.getTokenEndpointURI(), jwsAlgo, privateKey, keyID, null);
                } catch (final JOSEException e) {
                    throw new OidcException("Cannot instantiate private key JWT client authentication method", e);
                }
            } else {
                throw new OidcUnsupportedClientAuthMethodException("Unsupported client authentication method: " + chosenMethod);
            }
        }
        return null;
    }

    private ClientAuthenticationMethod getPreferredAuthenticationMethod(OidcConfiguration config) {
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
        final List<ClientAuthenticationMethod> serverSupportedAuthMethods,
        Set<ClientAuthenticationMethod> clientSupportedAuthMethods) {
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
}
