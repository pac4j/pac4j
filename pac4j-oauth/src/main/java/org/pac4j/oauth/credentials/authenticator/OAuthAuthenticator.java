package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.exceptions.OAuthException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthAuthenticator<C extends OAuthCredentials, O extends OAuthConfiguration> extends InitializableObject
    implements Authenticator<C> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final O configuration;

    protected OAuthAuthenticator(final O configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init();
    }

    @Override
    public void validate(final C credentials, final WebContext context) {
        init();

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
    protected abstract void retrieveAccessToken(WebContext context, OAuthCredentials credentials);

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration);
    }
}
