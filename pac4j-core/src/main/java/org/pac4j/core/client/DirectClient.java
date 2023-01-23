package org.pac4j.core.client;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.LogoutCredentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Direct client: credentials are passed and authentication occurs for every HTTP request.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClient extends BaseClient {

    @Override
    protected void beforeInternalInit(final boolean forceReinit) {
        if (saveProfileInSession == null) {
            saveProfileInSession = false;
        }
    }

    @Override
    protected final void afterInternalInit(final boolean forceReinit) {
        // ensures components have been properly initialized
        assertNotNull("credentialsExtractor", getCredentialsExtractor());
        assertNotNull("authenticator", getAuthenticator());
        assertNotNull("profileCreator", getProfileCreator());
    }

    @Override
    public final Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        throw new UnsupportedOperationException("Direct clients cannot redirect for login");
    }

    @Override
    public final HttpAction processLogout(final CallContext ctx, final LogoutCredentials credentials) {
        throw new UnsupportedOperationException("Direct clients cannot process logout");
    }

    @Override
    public final Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile,
                                                             final String targetUrl) {
        throw new UnsupportedOperationException("Direct clients cannot redirect for logout");
    }
}
