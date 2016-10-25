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
     * Associates a ticket with the current web session.
     *
     * @param context the web context
     * @param ticket the ticket
     */
    default void recordSession(C context, String ticket) {
        // do nothing by default
    }

    /**
     * Destroys the current web session for the given ticket for a front channel logout.
     *
     * @param context the web context
     * @param ticket the ticket
     */
    default void destroySessionFront(C context, String ticket) {
        // use the back channel way
        destroySessionBack(context, ticket);
    }

    /**
     * Destroys the current web session for the given ticket for a back channel logout.
     * 
     * @param context the web context
     * @param ticket the ticket
     */
    default void destroySessionBack(C context, String ticket) {}
}
