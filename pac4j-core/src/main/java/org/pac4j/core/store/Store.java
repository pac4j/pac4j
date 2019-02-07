package org.pac4j.core.store;

import java.util.Optional;

/**
 * Store data.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface Store<K, O> {

    /**
     * Get a value by key.
     *
     * @param key the key
     * @return the object
     */
    Optional<O> get(K key);

    /**
     * Set a value by its key.
     *
     * @param key the key
     * @param value the value
     */
    void set(K key, O value);

    /**
     * Remove the value associated to the key.
     *
     * @param key the key
     */
    void remove(K key);
}
