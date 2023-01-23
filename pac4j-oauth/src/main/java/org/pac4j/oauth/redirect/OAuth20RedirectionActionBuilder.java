package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.oauth.AuthorizationUrlBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.config.OAuth20Configuration;

import java.util.Optional;

/**
 * OAuth 2.0 redirection action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Slf4j
public class OAuth20RedirectionActionBuilder implements RedirectionActionBuilder {

    protected OAuth20Configuration configuration;

    protected IndirectClient client;

    public OAuth20RedirectionActionBuilder(final OAuth20Configuration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val webContext = ctx.webContext();

        try {

            final String state;
            if (configuration.isWithState()) {
                state = this.configuration.getStateGenerator().generateValue(ctx);
                LOGGER.debug("save sessionState: {}", state);
                ctx.sessionStore().set(webContext, client.getStateSessionAttributeName(), state);
            } else {
                state = null;
            }
            val service = (OAuth20Service) this.configuration.buildService(webContext, client);
            val authorizationUrl = new AuthorizationUrlBuilder(service)
                .state(state).additionalParams(this.configuration.getCustomParams()).build();
            LOGGER.debug("authorizationUrl: {}", authorizationUrl);
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, authorizationUrl));

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }
}
