package org.pac4j.core.logout.handler;

import org.pac4j.core.context.CallContext;

/**
 * This interface defines how to handle logout requests on client side.
 * For the CAS support, the key is the service ticket.
 * For the SAML support, the key is the session index or the nameID.
 * For the OIDC support, the key is the ssid claim.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface SessionLogoutHandler {

    /**
     * Associates a key with the current web session.
     *
     * @param ctx the context
     * @param key the key
     */
    default void recordSession(CallContext ctx, String key) {
        // do nothing by default
    }

    /**
     * Destroys the current web session for the given key for a front or back channel logout.
     *
     * @param ctx the context
     * @param key the key
     */
    default void destroySession(CallContext ctx, String key) {
        // do nothing by default
    }

    /**
     * Renew the web session.
     *
     * @param ctx the context
     * @param oldSessionId the old session identifier
     */
    default void renewSession(CallContext ctx, String oldSessionId) {
        // do nothing by default
    }
}
