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
public interface LogoutHandler<C extends WebContext> {

    /**
     * Associates a key with the current web session.
     *
     * @param context the web context
     * @param key the key
     */
    void recordSession(C context, String key);

    /**
     * Destroys the current web session for the given key for a front channel logout.
     *
     * @param context the web context
     * @param key the key
     */
    void destroySessionFront(C context, String key);

    /**
     * Destroys the current web session for the given key for a back channel logout.
     * 
     * @param context the web context
     * @param key the key
     */
    void destroySessionBack(C context, String key);

    /**
     * Renew the web session.
     *
     * @param oldSessionId the old session identifier
     * @param context the web context
     */
    void renewSession(String oldSessionId, C context);
}
