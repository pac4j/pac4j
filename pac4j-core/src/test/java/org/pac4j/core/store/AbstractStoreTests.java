package org.pac4j.core.store;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;
import org.pac4j.test.util.TestsConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        val store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY).get());
        store.remove(KEY);
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testSetExpiredGet() {
        val store = buildStore();
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
        val store = buildStore();
        store.set(KEY, VALUE);
        assertEquals(VALUE, store.get(KEY).get());
        store.set(KEY, null);
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testMissingObject() {
        val store = buildStore();
        assertFalse(store.get(KEY).isPresent());
    }

    @Test
    public void testNullKeyGet() {
        val store = buildStore();
        TestsHelper.expectException(() -> store.get(null), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeySet() {
        val store = buildStore();
        TestsHelper.expectException(() -> store.set(null, VALUE), TechnicalException.class, "key cannot be null");
    }

    @Test
    public void testNullKeyRemove() {
        val store = buildStore();
        TestsHelper.expectException(() -> store.remove(null), TechnicalException.class, "key cannot be null");
    }
}
