package org.pac4j.oauth.redirect;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * OAuth 1.0 redirect action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10RedirectActionBuilder extends InitializableWebObject implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OAuth10RedirectActionBuilder.class);

    protected final OAuth10Configuration configuration;

    public OAuth10RedirectActionBuilder(final OAuth10Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init(context);
    }

    @Override
    public RedirectAction redirect(final WebContext context) {
        init(context);

        try {

            final OAuth10aService service = this.configuration.buildService(context, null);
            final OAuth1RequestToken requestToken;
            try {
                requestToken = service.getRequestToken();
            } catch (final IOException | InterruptedException | ExecutionException e) {
                throw new HttpCommunicationException("Error getting token: " + e.getMessage());
            }
            logger.debug("requestToken: {}", requestToken);
            // save requestToken in user session
            context.getSessionStore().set(context, configuration.getRequestTokenSessionAttributeName(), requestToken);
            final String authorizationUrl = service.getAuthorizationUrl(requestToken);
            logger.debug("authorizationUrl: {}", authorizationUrl);
            return RedirectAction.redirect(authorizationUrl);

        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration);
    }
}
