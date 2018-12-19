package org.pac4j.oidc.credentials.extractor;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.*;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Extract the authorization code on the callback.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcExtractor implements CredentialsExtractor<OidcCredentials> {

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
    public OidcCredentials extract(final WebContext context) {
        final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
        final Map<String, String> parameters = retrieveParameters(context);
        AuthenticationResponse response;
        try {
            response = AuthenticationResponseParser.parse(new URI(computedCallbackUrl),
                    parameters.entrySet().stream().collect(
                            Collectors.toMap(Map.Entry::getKey, e -> Collections.singletonList(e.getValue()))));
        } catch (final URISyntaxException | ParseException e) {
            throw new TechnicalException(e);
        }

        if (response instanceof AuthenticationErrorResponse) {
            logger.error("Bad authentication response, error={}",
                    ((AuthenticationErrorResponse) response).getErrorObject());
            return null;
        }

        logger.debug("Authentication response successful");
        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) response;

        final State state = successResponse.getState();
        if (state == null) {
            throw new TechnicalException("Missing state parameter");
        }
        if (!state.equals(context.getSessionStore().get(context, OidcConfiguration.STATE_SESSION_ATTRIBUTE))) {
            throw new TechnicalException("State parameter is different from the one sent in authentication request. "
                    + "Session expired or possible threat of cross-site request forgery");
        }

        final OidcCredentials credentials = new OidcCredentials();
        // get authorization code
        final AuthorizationCode code = successResponse.getAuthorizationCode();
        if (code != null) {
            credentials.setCode(code);
        }
        // get ID token
        final JWT idToken = successResponse.getIDToken();
        if (idToken != null) {
            credentials.setIdToken(idToken);
        }
        // get access token
        final AccessToken accessToken = successResponse.getAccessToken();
        if (accessToken != null) {
            credentials.setAccessToken(accessToken);
        }

        return credentials;
    }

    protected Map<String, String> retrieveParameters(final WebContext context) {
        final Map<String, String[]> requestParameters = context.getRequestParameters();
        Map<String, String> map = new HashMap<>();
        for (final Map.Entry<String, String[]> entry : requestParameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }
        return map;
    }
}
