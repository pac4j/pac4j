package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.adapter.HttpActionAdapter;

/**
 * Security logic to protect an url.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface SecurityLogic<R, C extends WebContext> {

    /**
     * Perform the security logic.
     *
     * @param context the web context
     * @param config the configuration
     * @param securityGrantedAccessAdapter the success adapter
     * @param httpActionAdapter the HTTP action adapter
     * @param clients the defined clients
     * @param authorizers the defined authorizers
     * @param matchers the defined matchers
     * @param multiProfile whether multi profiles are supported
     * @param parameters additional parameters
     * @return the resulting action of the security
     */
    R perform(C context, Config config, SecurityGrantedAccessAdapter<R, C> securityGrantedAccessAdapter,
              HttpActionAdapter<R, C> httpActionAdapter,
              String clients, String authorizers, String matchers, Boolean multiProfile, Object... parameters);
}
