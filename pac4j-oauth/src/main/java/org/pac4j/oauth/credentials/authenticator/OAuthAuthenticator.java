package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.exceptions.OAuthException;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthAuthenticator implements Authenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected OAuthConfiguration configuration;

    protected IndirectClient client;

    protected OAuthAuthenticator(final OAuthConfiguration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public void validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        try {
            retrieveAccessToken(context, credentials);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Retrieve the access token from OAuth credentials.
     *
     * @param context the web context
     * @param credentials credentials
     */
    protected abstract void retrieveAccessToken(WebContext context, Credentials credentials);
}
