package org.pac4j.core.store;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        final S store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY));
        store.remove(KEY);
        assertNull(store.get(KEY));
    }

    @Test
    public void testSetExpiredGet() {
        final S store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY));
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertNull(store.get(KEY));
    }

    @Test
    public void testSetNullValue() {
        final S store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY));
        store.set(KEY, null);
        assertNull(store.get(KEY));
    }

    @Test
    public void testMissingObject() {
        final S store = buildStore();
        assertNull(store.get(KEY));
    }

    @Test
    public void testNullKeyGet() {
        final S store = buildStore();
        TestsHelper.expectException(() -> store.get(null), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeySet() {
        final S store = buildStore();
        TestsHelper.expectException(() -> store.set(null, VALUE), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeyRemove() {
        final S store = buildStore();
        TestsHelper.expectException(() -> store.remove(null), TechnicalException.class, "key cannot be null");
    }
}
