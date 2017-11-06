package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;


/**
 * To store data in session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface SessionStore<C extends WebContext> {

    SessionStore<? extends WebContext> EMPTY = new SessionStore<WebContext>() {};

    /**
     * Get or create the session identifier and initialize the session with it if necessary.
     *
     * @param context the web context
     * @return the session identifier
     */
    default String getOrCreateSessionId(C context) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Get the object from its key in store.
     *
     * @param context the web context
     * @param key     the key of the object
     * @return the object in store
     */
    default Object get(C context, String key) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Save an object in the store by its key.
     *
     * @param context the web context
     * @param key     the key of the object
     * @param value   the value to save in store
     */
    default void set(C context, String key, Object value) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Destroy the web session.
     *
     * @param context the web context
     * @return whether the session has been destroyed
     */
    default boolean destroySession(C context) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Get the native session as a trackable object.
     *
     * @param context the web context
     * @return the trackable object or <code>null</code> if this is not supported
     */
    default Object getTrackableSession(C context) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Build a new session store from a trackable session.
     *
     * @param context          the web context
     * @param trackableSession the trackable session
     * @return the new session store or <code>null</code> if this is not supported
     */
    default SessionStore<C> buildFromTrackableSession(C context, Object trackableSession) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Renew the native session by copying all data to a new one.
     *
     * @param context the web context
     * @return whether the session store has renewed the session
     */
    default boolean renewSession(C context) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
