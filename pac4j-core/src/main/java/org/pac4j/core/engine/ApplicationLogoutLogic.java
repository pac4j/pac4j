package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.HttpActionAdapter;

/**
 * Aplication logout logic.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface ApplicationLogoutLogic<R, C extends WebContext> {

    /**
     * Perform the application logout logic.
     *
     * @param context the web context
     * @param config the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl the default url
     * @param logoutUrlPattern the logout url pattern
     * @return the resulting action for logout
     */
    R perform(C context, Config config, HttpActionAdapter<R, C> httpActionAdapter,
                       String defaultUrl, String logoutUrlPattern);
}
