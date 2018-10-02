package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth 2.0 redirect action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20RedirectActionBuilder implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OAuth20RedirectActionBuilder.class);

    protected OAuth20Configuration configuration;

    protected IndirectClient client;

    public OAuth20RedirectActionBuilder(final OAuth20Configuration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public RedirectAction redirect(final WebContext context) {
        try {

            final String state;
            if (configuration.isWithState()) {
                state = this.configuration.getStateGenerator().generateState(context);
                logger.debug("save sessionState: {}", state);
                context.getSessionStore().set(context, configuration.getStateSessionAttributeName(client.getName()), state);
            } else {
                state = null;
            }
            final OAuth20Service service = this.configuration.buildService(context, client, state);
            final String authorizationUrl = service.getAuthorizationUrl(this.configuration.getCustomParams());
            logger.debug("authorizationUrl: {}", authorizationUrl);
            return RedirectAction.redirect(authorizationUrl);

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }
}
