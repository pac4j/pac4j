package org.pac4j.oauth.client;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the base implementation for client supporting OAuth protocol version 1.0.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth10Client<U extends OAuth10Profile> extends BaseOAuthClient<U, OAuth10aService, OAuth1Token> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth10Client.class);

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String REQUEST_TOKEN = "requestToken";

    /**
     * Return the name of the attribute storing in session the request token.
     *
     * @return the name of the attribute storing in session the request token
     */
    protected String getRequestTokenSessionAttributeName() {
        return getName() + "#" + REQUEST_TOKEN;
    }

    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) throws HttpAction {
        final OAuth1RequestToken requestToken = this.service.getRequestToken();
        logger.debug("requestToken: {}", requestToken);
        // save requestToken in user session
        context.setSessionAttribute(getRequestTokenSessionAttributeName(), requestToken);
        final String authorizationUrl = this.service.getAuthorizationUrl(requestToken);
        logger.debug("authorizationUrl: {}", authorizationUrl);
        return authorizationUrl;
    }

    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws HttpAction {
        final String tokenParameter = context.getRequestParameter(OAUTH_TOKEN);
        final String verifierParameter = context.getRequestParameter(OAUTH_VERIFIER);
        if (tokenParameter != null && verifierParameter != null) {
            // get request token from session
            final OAuth1RequestToken tokenSession = (OAuth1RequestToken) context.getSessionAttribute(getRequestTokenSessionAttributeName());
            logger.debug("tokenRequest: {}", tokenSession);
            final String token = OAuthEncoder.decode(tokenParameter);
            final String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("token: {} / verifier: {}", token, verifier);
            return new OAuth10Credentials(tokenSession, token, verifier, getName());
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }

    @Override
    protected OAuth1Token getAccessToken(final OAuthCredentials credentials) throws HttpAction {
        OAuth10Credentials oAuth10Credentials = (OAuth10Credentials) credentials;
        final OAuth1RequestToken tokenRequest = oAuth10Credentials.getRequestToken();
        final String token = oAuth10Credentials.getToken();
        final String verifier = oAuth10Credentials.getVerifier();
        logger.debug("tokenRequest: {}", tokenRequest);
        logger.debug("token: {}", token);
        logger.debug("verifier: {}", verifier);
        if (tokenRequest == null) {
            final String message = "Token request expired";
            throw new OAuthCredentialsException(message);
        }
        final String savedToken = tokenRequest.getToken();
        logger.debug("savedToken: {}", savedToken);
        if (savedToken == null || !savedToken.equals(token)) {
            final String message = "Token received: " + token + " is different from saved token: " + savedToken;
            throw new OAuthCredentialsException(message);
        }
        final OAuth1Token accessToken = this.service.getAccessToken(tokenRequest, verifier);
        logger.debug("accessToken: {}", accessToken);
        return accessToken;
    }

    @Override
    protected void addAccessTokenToProfile(final U profile, final OAuth1Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
            profile.setAccessSecret(accessToken.getTokenSecret());
        }
    }

    @Override
    protected void signRequest(OAuth1Token token, OAuthRequest request) {
        this.service.signRequest((OAuth1AccessToken) token, request);
        if (this.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + token.getToken());
        }
    }
}
