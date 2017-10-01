package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.exceptions.OAuthException;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth credentials extractor.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthCredentialsExtractor<C extends OAuthCredentials, O extends OAuthConfiguration> implements CredentialsExtractor<C> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected O configuration;

    protected IndirectClient client;

    protected OAuthCredentialsExtractor(final O configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public C extract(final WebContext context) {
        final boolean hasBeenCancelled = (Boolean) configuration.getHasBeenCancelledFactory().apply(context);
        // check if the authentication has been cancelled
        if (hasBeenCancelled) {
            logger.debug("authentication has been cancelled by user");
            return null;
        }
        // check errors
        try {
            boolean errorFound = false;
            final OAuthCredentialsException oauthCredentialsException =
                new OAuthCredentialsException("Failed to retrieve OAuth credentials, error parameters found");
            for (final String key : OAuthCredentialsException.ERROR_NAMES) {
                final String value = context.getRequestParameter(key);
                if (value != null) {
                    errorFound = true;
                    oauthCredentialsException.setErrorMessage(key, value);
                }
            }
            if (errorFound) {
                throw oauthCredentialsException;
            } else {
                return getOAuthCredentials(context);
            }
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the OAuth credentials from the web context.
     *
     * @param context the web context
     * @return the OAuth credentials
     */
    protected abstract C getOAuthCredentials(final WebContext context);
}
