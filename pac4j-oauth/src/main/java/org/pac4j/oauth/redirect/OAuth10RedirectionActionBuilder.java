package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 1.0 redirection action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10RedirectionActionBuilder implements RedirectionActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OAuth10RedirectionActionBuilder.class);

    protected OAuth10Configuration configuration;

    protected IndirectClient client;

    public OAuth10RedirectionActionBuilder(final OAuth10Configuration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context) {
        try {

            final OAuth10aService service = (OAuth10aService) this.configuration.buildService(context, client);
            final OAuth1RequestToken requestToken;
            try {
                requestToken = service.getRequestToken();
            } catch (final IOException | InterruptedException | ExecutionException e) {
                throw new HttpCommunicationException("Error getting token: " + e.getMessage());
            }
            logger.debug("requestToken: {}", requestToken);
            // save requestToken in user session
            context.getSessionStore().set(context, configuration.getRequestTokenSessionAttributeName(client.getName()), requestToken);
            final String authorizationUrl = service.getAuthorizationUrl(requestToken);
            logger.debug("authorizationUrl: {}", authorizationUrl);
            return Optional.of(RedirectionActionHelper.buildRedirectUrlAction(context, authorizationUrl));

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }
}
