package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.exceptions.OAuthException;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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

    /**
     * <p>Constructor for OAuthAuthenticator.</p>
     *
     * @param configuration a {@link OAuthConfiguration} object
     * @param client a {@link IndirectClient} object
     */
    protected OAuthAuthenticator(final OAuthConfiguration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
        try {
            retrieveAccessToken(ctx.webContext(), credentials);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
        return Optional.of(credentials);
    }

    /**
     * Retrieve the access token from OAuth credentials.
     *
     * @param context the web context
     * @param credentials credentials
     */
    protected abstract void retrieveAccessToken(WebContext context, Credentials credentials);
}
