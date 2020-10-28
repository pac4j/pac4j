package org.pac4j.oidc.credentials.authenticator;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The OpenID Connect authenticator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcAuthenticator implements Authenticator<OidcCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(OidcAuthenticator.class);

    private static final Collection<ClientAuthenticationMethod> SUPPORTED_METHODS =
            Arrays.asList(
                    ClientAuthenticationMethod.CLIENT_SECRET_POST,
                    ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                    ClientAuthenticationMethod.NONE);

    protected OidcConfiguration configuration;

    protected OidcClient client;

    private ClientAuthentication clientAuthentication;

    public OidcAuthenticator(final OidcConfiguration configuration, final OidcClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;

        final ClientID _clientID = new ClientID(configuration.getClientId());

        if (configuration.getSecret() != null) {

            // check authentication methods
            final List<ClientAuthenticationMethod> metadataMethods = configuration.findProviderMetadata()
                    .getTokenEndpointAuthMethods();

            final ClientAuthenticationMethod preferredMethod = getPreferredAuthenticationMethod(configuration);

            final ClientAuthenticationMethod chosenMethod;
            if (CommonHelper.isNotEmpty(metadataMethods)) {
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
                logger.info("Provider metadata does not provide Token endpoint authentication methods. Using: {}",
                        chosenMethod);
            }

            if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(chosenMethod)) {
                final Secret _secret = new Secret(configuration.getSecret());
                clientAuthentication = new ClientSecretPost(_clientID, _secret);
            } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(chosenMethod)) {
                final Secret _secret = new Secret(configuration.getSecret());
                clientAuthentication = new ClientSecretBasic(_clientID, _secret);
            } else if (ClientAuthenticationMethod.NONE.equals(chosenMethod)) {
                clientAuthentication = new ClientNoSecret(_clientID);
            } else {
                throw new TechnicalException("Unsupported client authentication method: " + chosenMethod);
            }

        } else {

            clientAuthentication = new ClientNoSecret(_clientID);

        }

    }

    /**
     * The preferred {@link ClientAuthenticationMethod} specified in the given
     * {@link OidcConfiguration}, or <code>null</code> meaning that the a
     * provider-supported method should be chosen.
     */
    private static ClientAuthenticationMethod getPreferredAuthenticationMethod(OidcConfiguration config) {
        final ClientAuthenticationMethod configurationMethod = config.getClientAuthenticationMethod();
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
     * @throws TechnicalException
     *         if none of the provider-supported methods is supported.
     */
    private static ClientAuthenticationMethod firstSupportedMethod(final List<ClientAuthenticationMethod> metadataMethods) {
        Optional<ClientAuthenticationMethod> firstSupported =
            metadataMethods.stream().filter((m) -> SUPPORTED_METHODS.contains(m)).findFirst();
        if (firstSupported.isPresent()) {
            return firstSupported.get();
        } else {
            throw new TechnicalException("None of the Token endpoint provider metadata authentication methods are supported: " +
                metadataMethods);
        }
    }

    @Override
    public void validate(final OidcCredentials credentials, final WebContext context) {
        final AuthorizationCode code = credentials.getCode();
        // if we have a code
        if (code != null) {
            try {
                final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
                CodeVerifier verifier = (CodeVerifier) configuration.getValueRetriever()
                        .retrieve(client.getCodeVerifierSessionAttributeName(), client, context).orElse(null);
                // Token request
                final TokenRequest request = new TokenRequest(
                        configuration.findProviderMetadata().getTokenEndpointURI(), this.clientAuthentication,
                        new AuthorizationCodeGrant(code, new URI(computedCallbackUrl), verifier));
                executeTokenRequest(request, credentials);
            } catch (final URISyntaxException | IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    public void refresh(final OidcCredentials credentials) {
        final RefreshToken refreshToken = credentials.getRefreshToken();
        if (refreshToken != null) {
            try {
                final TokenRequest request = new TokenRequest(configuration.findProviderMetadata().getTokenEndpointURI(),
                    this.clientAuthentication, new RefreshTokenGrant(refreshToken));
                executeTokenRequest(request, credentials);
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    private void executeTokenRequest(TokenRequest request, OidcCredentials credentials) throws IOException, ParseException {
        HTTPRequest tokenHttpRequest = request.toHTTPRequest();
        configuration.configureHttpRequest(tokenHttpRequest);

        final HTTPResponse httpResponse = tokenHttpRequest.send();
        logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
            httpResponse.getContent());

        final TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);
        if (response instanceof TokenErrorResponse) {
            throw new TechnicalException("Bad token response, error=" + ((TokenErrorResponse) response).getErrorObject());
        }
        logger.debug("Token response successful");
        final OIDCTokenResponse tokenSuccessResponse = (OIDCTokenResponse) response;

        final OIDCTokens oidcTokens = tokenSuccessResponse.getOIDCTokens();
        credentials.setAccessToken(oidcTokens.getAccessToken());
        credentials.setRefreshToken(oidcTokens.getRefreshToken());
        if (oidcTokens.getIDToken() != null) {
            credentials.setIdToken(oidcTokens.getIDToken());
        }
    }

    public ClientAuthentication getClientAuthentication() {
        return clientAuthentication;
    }

    public void setClientAuthentication(final ClientAuthentication clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public static class ClientNoSecret extends ClientAuthentication {

        protected ClientNoSecret(ClientID clientID) {
            super(ClientAuthenticationMethod.NONE, clientID);
        }

        @Override
        public void applyTo(HTTPRequest request) {
            // do nothing
        }

    }

}
