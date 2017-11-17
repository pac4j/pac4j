package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * Direct client: credentials are passed and authentication occurs for every HTTP request.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    @Override
    protected final void internalInit() {
        clientInit();

        // ensures components have been properly initialized
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
    }

    /**
     * Initialize the client.
     */
    protected abstract void clientInit();

    @Override
    public final Optional<HttpAction> redirect(final WebContext context) {
        return Optional.empty();
    }

    @Override
    public final Optional<C> getCredentials(final WebContext context) {
        init();
        return retrieveCredentials(context);
    }

    @Override
    public final Optional<RedirectAction> getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        return Optional.empty();
    }
}
