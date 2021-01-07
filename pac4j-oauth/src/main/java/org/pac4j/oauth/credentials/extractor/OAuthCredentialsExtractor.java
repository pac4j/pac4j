package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.exceptions.OAuthException;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * OAuth credentials extractor.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthCredentialsExtractor implements CredentialsExtractor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected OAuthConfiguration configuration;

    protected IndirectClient client;

    protected OAuthCredentialsExtractor(final OAuthConfiguration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<Credentials> extract(final WebContext context, final SessionStore sessionStore) {
        final boolean hasBeenCancelled = (Boolean) configuration.getHasBeenCancelledFactory().apply(context);
        // check if the authentication has been cancelled
        if (hasBeenCancelled) {
            logger.debug("authentication has been cancelled by user");
            return Optional.empty();
        }
        // check errors
        try {
            boolean errorFound = false;
            final OAuthCredentialsException oauthCredentialsException =
                new OAuthCredentialsException("Failed to retrieve OAuth credentials, error parameters found");
            for (final String key : OAuthCredentialsException.ERROR_NAMES) {
                final Optional<String> value = context.getRequestParameter(key);
                if (value.isPresent()) {
                    errorFound = true;
                    oauthCredentialsException.setErrorMessage(key, value.get());
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
    protected abstract Optional<Credentials> getOAuthCredentials(final WebContext context);
}
