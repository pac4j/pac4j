package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

import java.util.Optional;

/**
 * The {@link RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@FunctionalInterface
public interface LogoutActionBuilder {

    /**
     * Return the {@link RedirectionAction} for logout.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param currentProfile the current profile
     * @param targetUrl the target URL after logout
     * @return the redirection (optional)
     */
    Optional<RedirectionAction> getLogoutAction(WebContext context, SessionStore sessionStore,
                                                ProfileManagerFactory profileManagerFactory, UserProfile currentProfile, String targetUrl);
}
