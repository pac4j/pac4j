package org.pac4j.core.store;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * Test a store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractStoreTests<S extends Store> implements TestsConstants {

    protected abstract S buildStore();

    @Test
    public void testSetRemoveGet() {
        final var store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY).get());
        store.remove(KEY);
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testSetExpiredGet() {
        final var store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY).get());
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testSetNullValue() {
        final var store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY).get());
        store.set(KEY, null);
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testMissingObject() {
        final var store = buildStore();
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testNullKeyGet() {
        final var store = buildStore();
        TestsHelper.expectException(() -> store.get(null), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeySet() {
        final var store = buildStore();
        TestsHelper.expectException(() -> store.set(null, VALUE), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeyRemove() {
        final var store = buildStore();
        TestsHelper.expectException(() -> store.remove(null), TechnicalException.class, "key cannot be null");
    }
}
