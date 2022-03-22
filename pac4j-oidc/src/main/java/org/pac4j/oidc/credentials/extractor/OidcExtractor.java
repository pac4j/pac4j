package org.pac4j.oidc.credentials.extractor;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Extract the authorization code on the callback.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcExtractor implements CredentialsExtractor {

    private static final Logger logger = LoggerFactory.getLogger(OidcExtractor.class);

    protected OidcConfiguration configuration;

    protected OidcClient client;

    public OidcExtractor(final OidcConfiguration configuration, final OidcClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<Credentials> extract(final WebContext context, final SessionStore sessionStore) {
        final var logoutEndpoint = context.getRequestParameter(Pac4jConstants.LOGOUT_ENDPOINT_PARAMETER)
            .isPresent();
        if (logoutEndpoint) {
            final var logoutToken = context.getRequestParameter("logout_token");
            // back-channel logout
            if (logoutToken.isPresent()) {
                try {
                    final var jwt = JWTParser.parse(logoutToken.get());
                    // we should use the tokenValidator, but we can't as validation fails on missing claims: exp, iat...
                    //final IDTokenClaimsSet claims = configuration.findTokenValidator().validate(jwt, null);
                    //final String sid = (String) claims.getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                    final var sid = (String) jwt.getJWTClaimsSet().getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                    logger.debug("Handling back-channel logout for sessionId: {}", sid);
                    configuration.findLogoutHandler().destroySessionBack(context, sessionStore, sid);
                } catch (final java.text.ParseException e) {
                    logger.error("Cannot validate JWT logout token", e);
                    throw BadRequestAction.INSTANCE;
                }
            } else {
                final var sid = context.getRequestParameter(Pac4jConstants.OIDC_CLAIM_SESSIONID).orElse(null);
                logger.debug("Handling front-channel logout for sessionId: {}", sid);
                // front-channel logout
                configuration.findLogoutHandler().destroySessionFront(context, sessionStore, sid);
            }
            context.setResponseHeader("Cache-Control", "no-cache, no-store");
            context.setResponseHeader("Pragma", "no-cache");
            throw new OkAction("");
        } else {
            final var computedCallbackUrl = client.computeFinalCallbackUrl(context);
            final var parameters = retrieveParameters(context);
            AuthenticationResponse response;
            try {
                response = AuthenticationResponseParser.parse(new URI(computedCallbackUrl), parameters);
            } catch (final URISyntaxException | ParseException e) {
                throw new TechnicalException(e);
            }

            if (response instanceof AuthenticationErrorResponse) {
                logger.error("Bad authentication response, error={}",
                    ((AuthenticationErrorResponse) response).getErrorObject());
                return Optional.empty();
            }

            logger.debug("Authentication response successful");
            var successResponse = (AuthenticationSuccessResponse) response;

            var metadata = configuration.getProviderMetadata();
            if (metadata.supportsAuthorizationResponseIssuerParam() &&
                !metadata.getIssuer().equals(successResponse.getIssuer())) {
                throw new TechnicalException("Issuer mismatch, possible mix-up attack.");
            }

            if (configuration.isWithState()) {
                // Validate state for CSRF mitigation
                final var requestState = (State) configuration.getValueRetriever()
                    .retrieve(client.getStateSessionAttributeName(), client, context, sessionStore)
                    .orElseThrow(() -> new TechnicalException("State cannot be determined"));

                final var responseState = successResponse.getState();
                if (responseState == null) {
                    throw new TechnicalException("Missing state parameter");
                }

                logger.debug("Request state: {}/response state: {}", requestState, responseState);
                if (!requestState.equals(responseState)) {
                    throw new TechnicalException(
                        "State parameter is different from the one sent in authentication request.");
                }
            }

            final var credentials = new OidcCredentials();
            // get authorization code
            final var code = successResponse.getAuthorizationCode();
            if (code != null) {
                credentials.setCode(code);
            }
            // get ID token
            final var idToken = successResponse.getIDToken();
            if (idToken != null) {
                credentials.setIdToken(idToken);
            }
            // get access token
            final var accessToken = successResponse.getAccessToken();
            if (accessToken != null) {
                credentials.setAccessToken(accessToken);
            }

            return Optional.of(credentials);
        }
    }

    protected Map<String, List<String>> retrieveParameters(final WebContext context) {
        final var requestParameters = context.getRequestParameters();
        final Map<String, List<String>> map = new HashMap<>();
        for (final var entry : requestParameters.entrySet()) {
            map.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        return map;
    }
}
