package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.config.OAuth10Configuration;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 1.0 redirection action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Slf4j
public class OAuth10RedirectionActionBuilder implements RedirectionActionBuilder {

    protected OAuth10Configuration configuration;

    protected IndirectClient client;

    public OAuth10RedirectionActionBuilder(final OAuth10Configuration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context, final SessionStore sessionStore) {
        try {

            val service = (OAuth10aService) this.configuration.buildService(context, client);
            final OAuth1RequestToken requestToken;
            try {
                requestToken = service.getRequestToken();
            } catch (final IOException | InterruptedException | ExecutionException e) {
                throw new HttpCommunicationException("Error getting token: " + e.getMessage());
            }
            LOGGER.debug("requestToken: {}", requestToken);
            // save requestToken in user session
            sessionStore.set(context, configuration.getRequestTokenSessionAttributeName(client.getName()), requestToken);
            val authorizationUrl = service.getAuthorizationUrl(requestToken);
            LOGGER.debug("authorizationUrl: {}", authorizationUrl);
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, authorizationUrl));

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }
}
