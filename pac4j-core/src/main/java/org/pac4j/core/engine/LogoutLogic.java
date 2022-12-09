package org.pac4j.core.engine;

import org.pac4j.core.config.Config;

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
     * @param config the security configuration
     * @param defaultUrl the default url
     * @param logoutUrlPattern the logout url pattern
     * @param localLogout whether a local logout is required
     * @param destroySession whether the web session must be destroyed
     * @param centralLogout whether a central logout is required
     * @param parameters additional parameters
     * @return the resulting action for logout
     */
    Object perform(Config config, String defaultUrl, String logoutUrlPattern, Boolean localLogout, Boolean destroySession,
                   Boolean centralLogout, Object... parameters);
}
