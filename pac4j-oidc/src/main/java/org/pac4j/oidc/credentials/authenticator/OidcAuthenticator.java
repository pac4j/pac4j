package org.pac4j.oidc.credentials.authenticator;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcTokenException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * The OpenID Connect authenticator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class OidcAuthenticator implements Authenticator {

    protected OidcConfiguration configuration;

    protected OidcClient client;

    /**
     * <p>Constructor for OidcAuthenticator.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     * @param client a {@link OidcClient} object
     */
    public OidcAuthenticator(final OidcConfiguration configuration, final OidcClient client) {
        assertNotNull("configuration", configuration);
        assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        if (cred instanceof OidcCredentials credentials) {
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
                    throw new OidcException(e);
                }
            }
        }
        return Optional.ofNullable(cred);
    }

    /**
     * <p>refresh.</p>
     *
     * @param credentials a {@link OidcCredentials} object
     */
    public void refresh(final OidcCredentials credentials) {
        val refreshToken = credentials.getRefreshToken();
        if (refreshToken != null) {
            try {
                val request = createTokenRequest(new RefreshTokenGrant(refreshToken));
                executeTokenRequest(request, credentials);
            } catch (final IOException | ParseException e) {
                throw new OidcException(e);
            }
        }
    }

    /**
     * <p>createTokenRequest.</p>
     *
     * @param grant a {@link AuthorizationGrant} object
     * @return a {@link TokenRequest} object
     */
    protected TokenRequest createTokenRequest(final AuthorizationGrant grant) {
        val metadataResolver = configuration.getOpMetadataResolver();
        val tokenEndpointUri = metadataResolver.load().getTokenEndpointURI();
        val clientAuthentication = metadataResolver.getClientAuthentication();
        if (clientAuthentication != null) {
            return new TokenRequest(tokenEndpointUri, clientAuthentication, grant);
        } else {
            return new TokenRequest(tokenEndpointUri, new ClientID(configuration.getClientId()), grant);
        }
    }

    private void executeTokenRequest(Request request, OidcCredentials credentials) throws IOException, ParseException {
        val tokenHttpRequest = request.toHTTPRequest();
        configuration.configureHttpRequest(tokenHttpRequest);

        val httpResponse = tokenHttpRequest.send();
        LOGGER.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
            httpResponse.getContent());

        val response = OIDCTokenResponseParser.parse(httpResponse);
        if (response instanceof TokenErrorResponse tokenErrorResponse) {
            val errorObject = tokenErrorResponse.getErrorObject();
            throw new OidcTokenException("Bad token response, error=" + errorObject.getCode() + "," +
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
