package org.pac4j.core.engine;

import org.pac4j.core.config.Config;

/**
 * Callback logic to finish the login process for an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface CallbackLogic {

    /**
     * Perform the callback logic.
     *
     * @param config the security configuration
     * @param defaultUrl the default url
     * @param renewSession whether the session must be renewed
     * @param defaultClient the default client
     * @param parameters additional parameters
     * @return the resulting action of the callback
     */
    Object perform(Config config, String defaultUrl, Boolean renewSession, String defaultClient, Object... parameters);
}
