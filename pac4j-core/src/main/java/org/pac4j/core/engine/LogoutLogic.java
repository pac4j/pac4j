package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.HttpActionAdapter;

/**
 * Logout logic.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface LogoutLogic<R, C extends WebContext> {

    /**
     * Perform the application logout logic.
     *
     * @param context the web context
     * @param config the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl the default url
     * @param logoutUrlPattern the logout url pattern
     * @param centralLogout whether a central logout must be performed as well
     * @return the resulting action for logout
     */
    R perform(C context, Config config, HttpActionAdapter<R, C> httpActionAdapter,
                       String defaultUrl, String logoutUrlPattern, Boolean centralLogout);
}
