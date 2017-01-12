package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;

/**
 * To store data in session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface SessionStore<C extends WebContext> {

    /**
     * Get or create the session identifier and initialize the session with it if necessary.
     *
     * @param context the web context
     * @return the session identifier
     */
    String getOrCreateSessionId(C context);

    /**
     * Get the object from its key in store.
     *
     * @param context the web context
     * @param key the key of the object
     * @return the object in store
     */
    Object get(C context, String key);

    /**
     * Save an object in the store by its key.
     *
     * @param context the web context
     * @param key the key of the object
     * @param value the value to save in store
     */
    void set(C context, String key, Object value);

    /**
     * Destroy the web session.
     *
     * @param context the web context
     * @return whether the session has been destroyed
     */
    default boolean destroySession(C context) {
        // by default, the session has not been destroyed
        return false;
    }

    /**
     * Get the native session as a trackable object.
     *
     * @param context the web context
     * @return the trackable object
     */
    default Object getTrackableSession(C context) {
        // by default, the session store does not know how to keep track the native session
        return null;
    }

    /**
     * Build a new session store from a trackable session.
     *
     * @param context the web context
     * @param trackableSession the trackable session
     * @return the new session store
     */
    default SessionStore<C> buildFromTrackableSession(C context, Object trackableSession) {
        // by default, the session store does not know how to build a new session store
        return null;
    }

    /**
     * Renew the native session by copying all data to a new one.
     *
     * @param context the web context
     * @return whether the session store has renewed the session
     */
    default boolean renewSession(C context) {
        // by default, the session store does not know how to renew the native session
        return false;
    }
}
