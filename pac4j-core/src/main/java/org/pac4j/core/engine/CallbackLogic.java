package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

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
     * @param webContext the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param config the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl the default url
     * @param renewSession whether the session must be renewed
     * @param defaultClient the default client
     * @return the resulting action of the callback
     */
    Object perform(WebContext webContext, SessionStore sessionStore, ProfileManagerFactory profileManagerFactory, Config config,
                   HttpActionAdapter httpActionAdapter, String defaultUrl, Boolean renewSession, String defaultClient);
}
