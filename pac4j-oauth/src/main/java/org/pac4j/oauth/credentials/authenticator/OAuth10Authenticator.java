package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 1.0 authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10Authenticator extends OAuthAuthenticator {

    /**
     * <p>Constructor for OAuth10Authenticator.</p>
     *
     * @param configuration a {@link OAuth10Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public OAuth10Authenticator(final OAuth10Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected void retrieveAccessToken(final WebContext context, final Credentials credentials) {
        var oAuth10Credentials = (OAuth10Credentials) credentials;
        val tokenRequest = oAuth10Credentials.getRequestToken();
        val token = oAuth10Credentials.getToken();
        val verifier = oAuth10Credentials.getVerifier();
        logger.debug("tokenRequest: {}", tokenRequest);
        logger.debug("token: {}", token);
        logger.debug("verifier: {}", verifier);
        if (tokenRequest == null) {
            val message = "Token request expired";
            throw new OAuthCredentialsException(message);
        }
        val savedToken = tokenRequest.getToken();
        logger.debug("savedToken: {}", savedToken);
        if (savedToken == null || !savedToken.equals(token)) {
            val message = "Token received: " + token + " is different from saved token: " + savedToken;
            throw new OAuthCredentialsException(message);
        }
        final OAuth1AccessToken accessToken;
        try {
            accessToken = ((OAuth10aService) this.configuration.buildService(context, client)).getAccessToken(tokenRequest, verifier);
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting token:" + e.getMessage());
        }
        logger.debug("accessToken: {}", accessToken);
        oAuth10Credentials.setAccessToken(accessToken);
    }
}
