package org.pac4j.oidc.credentials.authenticator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.isNotEmpty;

/**
 * The OpenID Connect authenticator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class OidcAuthenticator implements Authenticator {

    private static final Collection<ClientAuthenticationMethod> SUPPORTED_METHODS =
        Arrays.asList(
            ClientAuthenticationMethod.CLIENT_SECRET_POST,
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
            ClientAuthenticationMethod.PRIVATE_KEY_JWT,
            ClientAuthenticationMethod.NONE);

    protected OidcConfiguration configuration;

    protected OidcClient client;

    @Getter
    @Setter
    private ClientAuthentication clientAuthentication;

    public OidcAuthenticator(final OidcConfiguration configuration, final OidcClient client) {
        assertNotNull("configuration", configuration);
        assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;

        val _clientID = new ClientID(configuration.getClientId());

        if (configuration.getSecret() != null) {
            // check authentication methods
            val metadataMethods = configuration.findProviderMetadata()
                .getTokenEndpointAuthMethods();

            val preferredMethod = getPreferredAuthenticationMethod(configuration);

            final ClientAuthenticationMethod chosenMethod;
            if (isNotEmpty(metadataMethods)) {
                if (preferredMethod != null) {
                    if (metadataMethods.contains(preferredMethod)) {
                        chosenMethod = preferredMethod;
                    } else {
                        throw new TechnicalException(
                            "Preferred authentication method (" + preferredMethod + ") not supported "
                                + "by provider according to provider metadata (" + metadataMethods + ").");
                    }
                } else {
                    chosenMethod = firstSupportedMethod(metadataMethods);
                }
            } else {
                chosenMethod = preferredMethod != null ? preferredMethod : ClientAuthenticationMethod.getDefault();
                LOGGER.info("Provider metadata does not provide Token endpoint authentication methods. Using: {}",
                    chosenMethod);
            }

            if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(chosenMethod)) {
                val _secret = new Secret(configuration.getSecret());
                clientAuthentication = new ClientSecretPost(_clientID, _secret);
            } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(chosenMethod)) {
                val _secret = new Secret(configuration.getSecret());
                clientAuthentication = new ClientSecretBasic(_clientID, _secret);
            } else if (ClientAuthenticationMethod.PRIVATE_KEY_JWT.equals(chosenMethod)) {
                val privateKetJwtConfig = configuration.getPrivateKeyJWTClientAuthnMethodConfig();
                assertNotNull("privateKetJwtConfig", privateKetJwtConfig);
                val jwsAlgo = privateKetJwtConfig.getJwsAlgorithm();
                assertNotNull("privateKetJwtConfig.getJwsAlgorithm()", jwsAlgo);
                val privateKey = privateKetJwtConfig.getPrivateKey();
                assertNotNull("privateKetJwtConfig.getPrivateKey()", privateKey);
                val keyID = privateKetJwtConfig.getKeyID();
                try {
                    clientAuthentication = new PrivateKeyJWT(_clientID, configuration.findProviderMetadata().getTokenEndpointURI(),
                        jwsAlgo, privateKey, keyID, null);
                } catch (final JOSEException e) {
                    throw new TechnicalException("Cannot instantiate private key JWT client authentication method", e);
                }
            } else {
                throw new TechnicalException("Unsupported client authentication method: " + chosenMethod);
            }
        }
    }

    /**
     * The preferred {@link ClientAuthenticationMethod} specified in the given
     * {@link OidcConfiguration}, or <code>null</code> meaning that the a
     * provider-supported method should be chosen.
     */
    private static ClientAuthenticationMethod getPreferredAuthenticationMethod(OidcConfiguration config) {
        val configurationMethod = config.getClientAuthenticationMethod();
        if (configurationMethod == null) {
            return null;
        }

        if (!SUPPORTED_METHODS.contains(configurationMethod)) {
            throw new TechnicalException("Configured authentication method (" + configurationMethod + ") is not supported.");
        }

        return configurationMethod;
    }

    /**
     * The first {@link ClientAuthenticationMethod} from the given list of
     * methods that is supported by this implementation.
     *
     * @throws TechnicalException if none of the provider-supported methods is supported.
     */
    private static ClientAuthenticationMethod firstSupportedMethod(final List<ClientAuthenticationMethod> metadataMethods) {
        var firstSupported =
            metadataMethods.stream().filter(SUPPORTED_METHODS::contains).findFirst();
        if (firstSupported.isPresent()) {
            return firstSupported.get();
        } else {
            throw new TechnicalException("None of the Token endpoint provider metadata authentication methods are supported: " +
                metadataMethods);
        }
    }

    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        val credentials = (OidcCredentials) cred;
        val code = credentials.getCode();
        // if we have a code
        if (code != null) {
            try {
                val computedCallbackUrl = client.computeFinalCallbackUrl(ctx.webContext());
                var verifier = (CodeVerifier) configuration.getValueRetriever()
                    .retrieve(ctx, client.getCodeVerifierSessionAttributeName(), client).orElse(null);
                // Token request
                val request = createTokenRequest(new AuthorizationCodeGrant(code, new URI(computedCallbackUrl), verifier));
                executeTokenRequest(request, credentials);
            } catch (final URISyntaxException | IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
        return Optional.of(credentials);
    }

    public void refresh(final OidcCredentials credentials) {
        val refreshToken = credentials.getRefreshToken();
        if (refreshToken != null) {
            try {
                val request = createTokenRequest(new RefreshTokenGrant(refreshToken));
                executeTokenRequest(request, credentials);
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    protected TokenRequest createTokenRequest(final AuthorizationGrant grant) {
        if (clientAuthentication != null) {
            return new TokenRequest(configuration.findProviderMetadata().getTokenEndpointURI(),
                this.clientAuthentication, grant);
        } else {
            return new TokenRequest(configuration.findProviderMetadata().getTokenEndpointURI(),
                new ClientID(configuration.getClientId()), grant);
        }
    }

    private void executeTokenRequest(TokenRequest request, OidcCredentials credentials) throws IOException, ParseException {
        val tokenHttpRequest = request.toHTTPRequest();
        configuration.configureHttpRequest(tokenHttpRequest);

        val httpResponse = tokenHttpRequest.send();
        LOGGER.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
            httpResponse.getContent());

        val response = OIDCTokenResponseParser.parse(httpResponse);
        if (response instanceof TokenErrorResponse tokenErrorResponse) {
            val errorObject = tokenErrorResponse.getErrorObject();
            throw new TechnicalException("Bad token response, error=" + errorObject.getCode() + "," +
                " description=" + errorObject.getDescription() + ", status=" + errorObject.getHTTPStatusCode());
        }
        LOGGER.debug("Token response successful");
        val tokenSuccessResponse = (OIDCTokenResponse) response;

        val oidcTokens = tokenSuccessResponse.getOIDCTokens();
        credentials.setAccessToken(oidcTokens.getAccessToken());
        credentials.setRefreshToken(oidcTokens.getRefreshToken());
        if (oidcTokens.getIDToken() != null) {
            credentials.setIdToken(oidcTokens.getIDToken());
        }
    }
}
