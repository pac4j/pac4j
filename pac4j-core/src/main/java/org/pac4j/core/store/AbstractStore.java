package org.pac4j.core.store;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.util.Optional;

/**
 * Abstract store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractStore<K, O> extends InitializableObject implements Store<K, O> {

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
    }

    /** {@inheritDoc} */
    @Override
    public Optional<O> get(final K key) {
        CommonHelper.assertNotNull("key", key);
        init();

        return internalGet(key);
    }

    /** {@inheritDoc} */
    @Override
    public void set(final K key, final O value) {
        CommonHelper.assertNotNull("key", key);
        init();

        if (value == null) {
            internalRemove(key);
        } else {
            internalSet(key, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void remove(final K key) {
        CommonHelper.assertNotNull("key", key);
        init();

        internalRemove(key);
    }

    /**
     * <p>internalGet.</p>
     *
     * @param key a K object
     * @return a {@link java.util.Optional} object
     */
    protected abstract Optional<O> internalGet(final K key);

    /**
     * <p>internalSet.</p>
     *
     * @param key a K object
     * @param value a O object
     */
    protected abstract void internalSet(final K key, final O value);

    /**
     * <p>internalRemove.</p>
     *
     * @param key a K object
     */
    protected abstract void internalRemove(final K key);
}
