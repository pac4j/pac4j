package org.pac4j.core.engine.savedrequest;

import org.pac4j.core.context.CallContext;
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
     * @param ctx the context
     */
    void save(CallContext ctx);

    /**
     * Restore the saved request.
     *
     * @param ctx the context
     * @param defaultUrl the default URL
     * @return the originally requested URL
     */
    HttpAction restore(CallContext ctx, String defaultUrl);
}
