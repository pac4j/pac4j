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

    @Override
    protected void internalInit(final boolean forceReinit) {
    }

    @Override
    public Optional<O> get(final K key) {
        CommonHelper.assertNotNull("key", key);
        init();

        return internalGet(key);
    }

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

    @Override
    public void remove(final K key) {
        CommonHelper.assertNotNull("key", key);
        init();

        internalRemove(key);
    }

    protected abstract Optional<O> internalGet(final K key);

    protected abstract void internalSet(final K key, final O value);

    protected abstract void internalRemove(final K key);
}
