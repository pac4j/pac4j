package org.pac4j.oidc.credentials.extractor;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.SessionKeyCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.exceptions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Extract the OIDC credentials.
 *
 * @author Jerome Leleu
 * @since 6.0.0
 */
@Slf4j
public class OidcCredentialsExtractor implements CredentialsExtractor {

    protected OidcConfiguration configuration;

    protected OidcClient client;

    /**
     * <p>Constructor for OidcCredentialsExtractor.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     * @param client a {@link OidcClient} object
     */
    public OidcCredentialsExtractor(final OidcConfiguration configuration, final OidcClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        val logoutToken = webContext.getRequestParameter("logout_token");
        val sid = webContext.getRequestParameter(Pac4jConstants.OIDC_CLAIM_SESSIONID);

        // back-channel logout
        if (logoutToken.isPresent()) {
            try {
                val jwt = JWTParser.parse(logoutToken.get());
                if (jwt instanceof EncryptedJWT) {
                    LOGGER.error("Encrypted JWTs are not accepted for logout requests");
                    throw new BadRequestAction();
                }
                String sessionId;
                if (configuration.isLogoutValidation()) {
                    val claims = configuration.getOpMetadataResolver().getTokenValidator().validateLogoutToken(jwt);
                    if (claims.getClaim(OidcConfiguration.NONCE) != null) {
                        LOGGER.error("The nonce claim should not exist for logout requests");
                        throw new BadRequestAction();
                    }
                    val events = claims.getClaim("events");
                    if (!(events instanceof Map mapEvents)
                        || !mapEvents.containsKey("http://schemas.openid.net/event/backchannel-logout")) {
                        LOGGER.error("The events claim should contain the 'http://schemas.openid.net/event/backchannel-logout'"
                            + " member name for logout requests");
                        throw new BadRequestAction();
                    }
                    sessionId = (String) claims.getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                    if (StringUtils.isBlank(sessionId)) {
                        LOGGER.error("The sid claim is mandatory for logout requests");
                        throw new BadRequestAction();
                    }
                } else {
                    sessionId = (String) jwt.getJWTClaimsSet().getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                }
                LOGGER.debug("Handling back-channel logout for sessionId: {}", sessionId);
                return Optional.of(new SessionKeyCredentials(LogoutType.BACK, sessionId));
            } catch (final java.text.ParseException | BadJOSEException | JOSEException e) {
                LOGGER.error("Cannot validate JWT logout token", e);
                throw new BadRequestAction();
            }

        // front channel logout
        } else if (sid.isPresent()) {
            val sessionId = sid.get();
            LOGGER.debug("Handling front-channel logout for sessionId: {}", sessionId);
            return Optional.of(new SessionKeyCredentials(LogoutType.FRONT, sessionId));

        // authentication
        } else {
            val computedCallbackUrl = client.computeFinalCallbackUrl(webContext);
            val parameters = retrieveParameters(webContext);
            AuthenticationResponse response;
            try {
                response = AuthenticationResponseParser.parse(new URI(computedCallbackUrl), parameters);
            } catch (final URISyntaxException | ParseException e) {
                throw new OidcException(e);
            }

            if (response instanceof AuthenticationErrorResponse) {
                LOGGER.error("Bad authentication response, error={}",
                    ((AuthenticationErrorResponse) response).getErrorObject());
                return Optional.empty();
            }

            LOGGER.debug("Authentication response successful");
            var successResponse = (AuthenticationSuccessResponse) response;

            var metadata = configuration.getOpMetadataResolver().load();
            if (metadata.supportsAuthorizationResponseIssuerParam() &&
                !metadata.getIssuer().equals(successResponse.getIssuer())) {
                throw new OidcIssuerMismatchException("Issuer mismatch, possible mix-up attack.");
            }

            if (configuration.isWithState()) {
                // Validate state for CSRF mitigation
                val requestState = (State) configuration.getValueRetriever()
                    .retrieve(ctx, client.getStateSessionAttributeName(), client)
                    .orElseThrow(() -> new OidcMissingSessionStateException("State cannot be determined"));

                val responseState = successResponse.getState();
                if (responseState == null) {
                    throw new OidcMissingStateParameterException("Missing state parameter");
                }

                LOGGER.debug("Request state: {}/response state: {}", requestState, responseState);
                if (!requestState.equals(responseState)) {
                    throw new OidcStateMismatchException(
                        "State parameter is different from the one sent in authentication request.");
                }
            }

            val credentials = new OidcCredentials();
            // get authorization code
            val code = successResponse.getAuthorizationCode();
            if (code != null) {
                credentials.setCode(code.getValue());
            }
            // get ID token
            val idToken = successResponse.getIDToken();
            if (idToken != null) {
                credentials.setIdToken(idToken.serialize());
            }
            // get access token
            val accessToken = successResponse.getAccessToken();
            if (accessToken != null) {
                credentials.setAccessTokenObject(accessToken);
            }
            if (code == null && idToken == null && accessToken == null) {
                throw new TechnicalException("Cannot accept empty OIDC credentials");
            }

            return Optional.of(credentials);
        }
    }

    /**
     * <p>retrieveParameters.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link Map} object
     */
    protected Map<String, List<String>> retrieveParameters(final WebContext context) {
        val requestParameters = context.getRequestParameters();
        final Map<String, List<String>> map = new HashMap<>();
        for (val entry : requestParameters.entrySet()) {
            map.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }
        return map;
    }
}
