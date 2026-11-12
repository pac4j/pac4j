package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 2.0 authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20Authenticator extends OAuthAuthenticator {

    /**
     * <p>Constructor for OAuth20Authenticator.</p>
     *
     * @param configuration a {@link OAuth20Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public OAuth20Authenticator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected void retrieveAccessToken(final CallContext context, final Credentials credentials) {
        var oAuth20Credentials = (OAuth20Credentials) credentials;
        // no request token saved in context and no token (OAuth v2.0)
        final OAuth2AccessToken accessToken;
        try {
            val service = (OAuth20Service) configuration.buildService(context, client);
            val request = ((OAuth20Configuration) configuration).getTokenRequestBuilder()
                .build(context, oAuth20Credentials, service);
            configuration.getHttpRequestConfigurer().configure(request, context);
            try (val response = service.execute(request)) {
                accessToken = service.getApi().getAccessTokenExtractor().extract(response);
            }
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting token:" + e.getMessage());
        }
        oAuth20Credentials.setAccessToken(OAuth20Credentials.OAuth20AccessToken.from(accessToken));
    }
}
