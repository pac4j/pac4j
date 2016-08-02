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
     * Invalidate the whole session.
     *
     * @param context the web context
     */
    default void invalidateSession(C context) {
        throw new UnsupportedOperationException("To be implemented");
    }
}
