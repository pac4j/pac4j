package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Direct client: credentials are passed and authentication occurs for every HTTP request.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClient extends BaseClient {

    @Override
    protected void beforeInternalInit() {
        if (saveProfileInSession == null) {
            saveProfileInSession = false;
        }
    }

    @Override
    protected final void afterInternalInit() {
        // ensures components have been properly initialized
        assertNotNull("credentialsExtractor", getCredentialsExtractor());
        assertNotNull("authenticator", getAuthenticator());
        assertNotNull("profileCreator", getProfileCreator());
    }

    @Override
    public final Optional<RedirectionAction> getRedirectionAction(final WebContext context) {
        return Optional.empty();
    }

    @Override
    public final Optional<Credentials> getCredentials(final WebContext context) {
        init();
        return retrieveCredentials(context);
    }

    @Override
    public final Optional<RedirectionAction> getLogoutAction(final WebContext context, final UserProfile currentProfile,
                                                             final String targetUrl) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators());
    }
}
