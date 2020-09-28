package org.pac4j.core.logout.handler;

import org.pac4j.core.context.WebContext;

/**
 * This interface defines how to handle logout requests on client side.
 * For the CAS support, the key is the service ticket.
 * For the SAML support, the key is the session index.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface LogoutHandler {

    /**
     * Associates a key with the current web session.
     *
     * @param context the web context
     * @param key the key
     */
    default void recordSession(WebContext context, String key) {
        // do nothing by default
    }

    /**
     * Destroys the current web session for the given key for a front channel logout.
     *
     * @param context the web context
     * @param key the key
     */
    default void destroySessionFront(WebContext context, String key) {
        // do nothing by default
    }

    /**
     * Destroys the current web session for the given key for a back channel logout.
     *
     * @param context the web context
     * @param key the key
     */
    default void destroySessionBack(WebContext context, String key) {
        // do nothing by default
    }

    /**
     * Renew the web session.
     *
     * @param oldSessionId the old session identifier
     * @param context the web context
     */
    default void renewSession(String oldSessionId, WebContext context) {
        // do nothing by default
    }
}
