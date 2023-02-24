package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.exceptions.OAuthException;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
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

    /**
     * <p>Constructor for OAuthCredentialsExtractor.</p>
     *
     * @param configuration a {@link org.pac4j.oauth.config.OAuthConfiguration} object
     * @param client a {@link org.pac4j.core.client.IndirectClient} object
     */
    protected OAuthCredentialsExtractor(final OAuthConfiguration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();

        val hasBeenCancelled = (Boolean) configuration.getHasBeenCancelledFactory().apply(webContext);
        // check if the authentication has been cancelled
        if (hasBeenCancelled) {
            logger.debug("authentication has been cancelled by user");
            return Optional.empty();
        }
        // check errors
        try {
            var errorFound = false;
            val oauthCredentialsException =
                new OAuthCredentialsException("Failed to retrieve OAuth credentials, error parameters found");
            for (val key : OAuthCredentialsException.ERROR_NAMES) {
                val value = webContext.getRequestParameter(key);
                if (value.isPresent()) {
                    errorFound = true;
                    oauthCredentialsException.setErrorMessage(key, value.get());
                }
            }
            if (errorFound) {
                throw oauthCredentialsException;
            } else {
                return getOAuthCredentials(webContext, ctx.sessionStore());
            }
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the OAuth credentials from the web context.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @return the OAuth credentials
     */
    protected abstract Optional<Credentials> getOAuthCredentials(WebContext context, SessionStore sessionStore);
}
