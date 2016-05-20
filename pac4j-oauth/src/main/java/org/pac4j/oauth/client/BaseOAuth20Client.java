package org.pac4j.oauth.client;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the base implementation for client supporting OAuth protocol version 2.0.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth20Client<U extends OAuth20Profile> extends BaseOAuthClient<U, OAuth20Service, OAuth2AccessToken> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth20Client.class);

    public static final String OAUTH_CODE = "code";

    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) throws HttpAction {
        // no request token for OAuth 2.0 -> no need to save it in the context
        final String authorizationUrl = this.service.getAuthorizationUrl();
        logger.debug("authorizationUrl: {}", authorizationUrl);
        return authorizationUrl;
    }

    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws HttpAction {
        final String codeParameter = context.getRequestParameter(OAUTH_CODE);
        if (codeParameter != null) {
            final String code = OAuthEncoder.decode(codeParameter);
            logger.debug("code: {}", code);
            return new OAuth20Credentials(code, getName());
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }

    @Override
    protected OAuth2AccessToken getAccessToken(final OAuthCredentials credentials) throws HttpAction {
        OAuth20Credentials oAuth20Credentials = (OAuth20Credentials) credentials;
        // no request token saved in context and no token (OAuth v2.0)
        final String code = oAuth20Credentials.getCode();
        logger.debug("code: {}", code);
        final OAuth2AccessToken accessToken = this.service.getAccessToken(code);
        logger.debug("accessToken: {}", accessToken);
        return accessToken;
    }

    @Override
    protected void addAccessTokenToProfile(U profile, OAuth2AccessToken accessToken) {
        if (profile != null) {
            final String token = accessToken.getAccessToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
        }
    }

    @Override
    protected void signRequest(OAuth2AccessToken accessToken, OAuthRequest request) {
        this.service.signRequest(accessToken, request);
        if (this.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + accessToken.getAccessToken());
        }
    }
}
