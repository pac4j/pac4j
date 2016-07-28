package org.pac4j.cas.logout;

import org.pac4j.core.context.WebContext;

/**
 * This interface defines how to handle CAS logout request on client side.
 * 
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface CasLogoutHandler<C extends WebContext> {

    /**
     * Associates a token request with the current web session.
     *
     * @param context the web context
     * @param ticket the service ticket
     */
    default void recordSession(C context, String ticket) {
        // do nothing by default
    }

    /**
     * Destroys the current web session for the given ticket.
     * 
     * @param context the web context
     * @param ticket the ticket
     */
    default void destroySession(C context, String ticket) {
        // for backward compatibility
        destroySession(context);
    }

    /**
     * Destroys the current web session for the given CAS logout request.
     *
     * @param context the web context
     * @deprecated to be removed, replaced by: {@link #destroySession(WebContext, String)}
     */
    @Deprecated
    default void destroySession(C context) {
        // do nothing by default
    }
}
