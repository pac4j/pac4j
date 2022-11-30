package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

/**
 * Logout logic for the application and the identity provider.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface LogoutLogic {

    /**
     * Perform the application logout logic.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param config the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl the default url
     * @param logoutUrlPattern the logout url pattern
     * @param localLogout whether a local logout is required
     * @param destroySession whether the web session must be destroyed
     * @param centralLogout whether a central logout is required
     * @return the resulting action for logout
     */
    Object perform(WebContext context, SessionStore sessionStore, ProfileManagerFactory profileManagerFactory, Config config,
                   HttpActionAdapter httpActionAdapter, String defaultUrl, String logoutUrlPattern, Boolean localLogout,
                   Boolean destroySession, Boolean centralLogout);
}
