package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth 2.0 redirect action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20RedirectActionBuilder extends InitializableWebObject implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OAuth20RedirectActionBuilder.class);

    protected final OAuth20Configuration configuration;

    public OAuth20RedirectActionBuilder(final OAuth20Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init(context);
    }

    @Override
    public RedirectAction redirect(final WebContext context) throws HttpAction {
        init(context);

        try {

            final OAuth20Service service;
            // with state: generate a state, save it in session and build a new service with this state
            if (this.configuration.isWithState()) {
                final String state = getStateParameter();
                logger.debug("save sessionState: {}", state);
                context.setSessionAttribute(this.configuration.getStateSessionAttributeName(), state);

                service = this.configuration.buildService(context, state);
            } else {

                service = this.configuration.getService();
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

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration);
    }
}
