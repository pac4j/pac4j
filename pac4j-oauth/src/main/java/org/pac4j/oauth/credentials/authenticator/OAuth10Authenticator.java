package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import java.io.IOException;

/**
 * OAuth 1.0 authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Authenticator extends OAuthAuthenticator<OAuth10Credentials, OAuth10Configuration> {

    public OAuth10Authenticator(final OAuth10Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void retrieveAccessToken(final OAuthCredentials credentials) throws HttpAction, OAuthCredentialsException {
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
        final OAuth1Token accessToken;
        try {
            accessToken = this.configuration.getService().getAccessToken(tokenRequest, verifier);
        } catch (IOException ex) {
            throw new HttpCommunicationException("Error getting token:" + ex.getMessage());
        }
        logger.debug("accessToken: {}", accessToken);
        oAuth10Credentials.setAccessToken(accessToken);
    }
}
