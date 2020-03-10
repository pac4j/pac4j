package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 2.0 authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20Authenticator extends OAuthAuthenticator<OAuth20Credentials, OAuth20Configuration> {

    public OAuth20Authenticator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void retrieveAccessToken(final WebContext context, final OAuthCredentials credentials) {
        final OAuth20Credentials oAuth20Credentials = (OAuth20Credentials) credentials;
        // no request token saved in context and no token (OAuth v2.0)
        final String code = oAuth20Credentials.getCode();
        logger.debug("code: {}", code);
        final OAuth2AccessToken accessToken;
        try {
            accessToken = this.configuration.buildService(context, client).getAccessToken(code);
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting token:" + e.getMessage());
        }
        logger.debug("accessToken: {}", accessToken);
        oAuth20Credentials.setAccessToken(accessToken);
    }
}
