package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;

/**
 * Security logic to protect an url.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface SecurityLogic {

    /**
     * Perform the security logic.
     *
     * @param config the configuration
     * @param securityGrantedAccessAdapter the success adapter
     * @param clients the defined clients
     * @param authorizers the defined authorizers
     * @param matchers the defined matchers
     * @param parameters framework parameters
     * @return the resulting action of the security
     */
    Object perform(Config config, SecurityGrantedAccessAdapter securityGrantedAccessAdapter, String clients,
                   String authorizers, String matchers, FrameworkParameters parameters);
}
