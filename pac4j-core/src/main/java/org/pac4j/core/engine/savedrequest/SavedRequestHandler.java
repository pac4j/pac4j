package org.pac4j.core.engine.savedrequest;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;

/**
 * Saves a request before a login process and restores it after a successfull login.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface SavedRequestHandler {

    /**
     * Saves the current web context.
     *
     * @param webContext the web context
     */
    void save(WebContext webContext);

    /**
     * Restore the saved request.
     *
     * @param webContext the web context
     * @param defaultUrl the default URL
     * @return the originally requested URL
     */
    HttpAction restore(WebContext webContext, String defaultUrl);
}
