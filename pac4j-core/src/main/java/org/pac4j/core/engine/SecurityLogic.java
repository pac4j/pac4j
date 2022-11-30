package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

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
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param config the configuration
     * @param securityGrantedAccessAdapter the success adapter
     * @param httpActionAdapter the HTTP action adapter
     * @param clients the defined clients
     * @param authorizers the defined authorizers
     * @param matchers the defined matchers
     * @param parameters additional parameters
     * @return the resulting action of the security
     */
    Object perform(WebContext context, SessionStore sessionStore, ProfileManagerFactory profileManagerFactory, Config config,
                   SecurityGrantedAccessAdapter securityGrantedAccessAdapter, HttpActionAdapter httpActionAdapter, String clients,
                   String authorizers, String matchers, Object... parameters);
}
