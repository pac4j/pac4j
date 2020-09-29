package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;

import java.util.Optional;

/**
 * To store data in session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface SessionStore {

    /**
     * Get or create the session identifier and initialize the session with it if necessary.
     *
     * @param context the web context
     * @return the session identifier
     */
    String getOrCreateSessionId(WebContext context);

    /**
     * Get the object from its key in store.
     *
     * @param context the web context
     * @param key the key of the object
     * @return the optional object in store
     */
    Optional<Object> get(WebContext context, String key);

    /**
     * Save an object in the store by its key.
     *
     * @param context the web context
     * @param key the key of the object
     * @param value the value to save in store
     */
    void set(WebContext context, String key, Object value);

    /**
     * Destroy the web session.
     *
     * @param context the web context
     * @return whether the session has been destroyed
     */
    boolean destroySession(WebContext context);

    /**
     * Get the native session as a trackable object.
     *
     * @param context the web context
     * @return the optional trackable object
     */
    Optional getTrackableSession(WebContext context);

    /**
     * Build a new session store from a trackable session.
     *
     * @param context the web context
     * @param trackableSession the trackable session
     * @return the optional new session store
     */
    Optional<SessionStore> buildFromTrackableSession(WebContext context, Object trackableSession);

    /**
     * Renew the native session by copying all data to a new one.
     *
     * @param context the web context
     * @return whether the session store has renewed the session
     */
    boolean renewSession(WebContext context);
}
