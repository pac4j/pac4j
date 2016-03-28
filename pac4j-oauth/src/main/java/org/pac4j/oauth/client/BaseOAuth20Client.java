package org.pac4j.oauth.client;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the base implementation for client supporting OAuth protocol version 2.0.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth20Client<U extends OAuth20Profile> extends BaseOAuthClient<U> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth20Client.class);

    public static final String OAUTH_CODE = "code";

    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) throws RequiresHttpAction {
        // no request token for OAuth 2.0 -> no need to save it in the context
        final String authorizationUrl = ((OAuth20Service) this.service).getAuthorizationUrl();
        logger.debug("authorizationUrl: {}", authorizationUrl);
        return authorizationUrl;
    }

    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws RequiresHttpAction {
        final String verifierParameter = context.getRequestParameter(OAUTH_CODE);
        if (verifierParameter != null) {
            final String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("verifier: {}", verifier);
            return new OAuthCredentials(verifier, getName());
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }

    @Override
    protected Token getAccessToken(final OAuthCredentials credentials) throws RequiresHttpAction {
        // no request token saved in context and no token (OAuth v2.0)
        final String verifier = credentials.getVerifier();
        logger.debug("verifier: {}", verifier);
        final Verifier clientVerifier = new Verifier(verifier);
        final Token accessToken = ((OAuth20Service) this.service).getAccessToken(clientVerifier);
        logger.debug("accessToken: {}", accessToken);
        return accessToken;
    }
}
