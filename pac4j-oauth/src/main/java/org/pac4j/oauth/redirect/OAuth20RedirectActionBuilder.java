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

            final OAuth20Service service;
            // with state: generate a state, save it in session and build a new service with this state
            if (this.configuration.isWithState()) {
                final String state = getStateParameter();
                logger.debug("save sessionState: {}", state);
                context.getSessionStore().set(context, this.configuration.getStateSessionAttributeName(client.getName()), state);

                service = this.configuration.buildService(context, client, state);
            } else {

                service = this.configuration.buildService(context, client, null);
            }
            final String authorizationUrl = service.getAuthorizationUrl(this.configuration.getCustomParams());
            logger.debug("authorizationUrl: {}", authorizationUrl);
            return RedirectAction.redirect(authorizationUrl);

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    protected String getStateParameter() {
        final String stateData = this.configuration.getStateData();
        final String stateParameter;
        if (CommonHelper.isNotBlank(stateData)) {
            stateParameter = stateData;
        } else {
            stateParameter = CommonHelper.randomString(10);
        }
        return stateParameter;
    }
}
