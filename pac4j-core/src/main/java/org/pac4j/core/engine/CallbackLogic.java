package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.adapter.HttpActionAdapter;

/**
 * Callback logic to finish the login process for an indirect client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface CallbackLogic<R, C extends WebContext> {

    /**
     * Perform the callback logic.
     *
     * @param context the web context
     * @param config the security configuration
     * @param httpActionAdapter the HTTP action adapter
     * @param defaultUrl the default url
     * @param multiProfile whether multi profiles are supported
     * @param renewSession whether the session must be renewed
     * @param client the default client
     * @return the resulting action of the callback
     */
    R perform(C context, Config config, HttpActionAdapter<R, C> httpActionAdapter,
                     String defaultUrl, Boolean multiProfile, Boolean renewSession, String client);
}
