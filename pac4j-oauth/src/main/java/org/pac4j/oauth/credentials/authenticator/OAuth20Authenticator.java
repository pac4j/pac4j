package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import java.io.IOException;

/**
 * OAuth 2.0 authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20Authenticator extends OAuthAuthenticator<OAuth20Credentials, OAuth20Configuration> {

    public OAuth20Authenticator(final OAuth20Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void retrieveAccessToken(final OAuthCredentials credentials) throws HttpAction, OAuthCredentialsException {
        OAuth20Credentials oAuth20Credentials = (OAuth20Credentials) credentials;
        // no request token saved in context and no token (OAuth v2.0)
        final String code = oAuth20Credentials.getCode();
        logger.debug("code: {}", code);
        final OAuth2AccessToken accessToken;
        try {
            accessToken = this.configuration.getService().getAccessToken(code);
        } catch (IOException ex) {
            throw new HttpCommunicationException("Error getting token:" + ex.getMessage());
        }
        logger.debug("accessToken: {}", accessToken);
        oAuth20Credentials.setAccessToken(accessToken);
    }
}
