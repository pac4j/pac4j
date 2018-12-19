package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;

/**
 * Direct client: credentials are passed and authentication occurs for every HTTP request.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClient<C extends Credentials> extends BaseClient<C> {

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
    public final HttpAction redirect(final WebContext context) {
        throw new TechnicalException("direct clients do not support redirections");
    }

    @Override
    public final C getCredentials(final WebContext context) {
        init();
        return retrieveCredentials(context);
    }

    @Override
    public final RedirectAction getLogoutAction(final WebContext context, final CommonProfile currentProfile, final String targetUrl) {
        return null;
    }
}
