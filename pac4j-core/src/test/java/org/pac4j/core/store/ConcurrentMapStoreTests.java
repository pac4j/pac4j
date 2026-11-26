package org.pac4j.core.store;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

/**
 * Test {@link ConcurrentMapStore}.
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
public final class ConcurrentMapStoreTests extends AbstractStoreTests<ConcurrentMapStore> {

    @Override
    protected ConcurrentMapStore buildStore() {
        return new ConcurrentMapStore(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testWritePurgesOneBucketAndKeepsRenewedEntries() {
        val store = new ConcurrentMapStore<Integer, String>(10, TimeUnit.SECONDS);
        val start = Instant.EPOCH;
        val afterShortTimeout = start.plusSeconds(11);
        try (val clock = mockStatic(Instant.class, CALLS_REAL_METHODS)) {
            clock.when(Instant::now).thenReturn(start);
            // Varying the high byte gives each group one key in every bucket.
            for (int i = 0; i < 256; i++) {
                store.set(i << 24, VALUE);
                store.set((i << 24) + 1, VALUE);
            }

            store.setTimeout(60);
            for (int i = 0; i < 256; i++) {
                store.set((i << 24) + 1, "renewed");
            }

            clock.when(Instant::now).thenReturn(afterShortTimeout);
            store.set(2, "trigger");
            // Rewind so reads reveal exactly what the write removed, whichever bucket it chose.
            clock.when(Instant::now).thenReturn(start);
            int purged = 0;
            for (int i = 0; i < 256; i++) {
                if (store.get(i << 24).isEmpty()) {
                    purged++;
                }
                assertEquals("renewed", store.get((i << 24) + 1).orElseThrow());
            }
            assertEquals(1, purged);
        }
    }

    @Test
    public void testWritesPurgeEveryBucketWithin256Writes() {
        val store = new ConcurrentMapStore<Integer, String>(10, TimeUnit.SECONDS);
        val start = Instant.EPOCH;
        try (val clock = mockStatic(Instant.class, CALLS_REAL_METHODS)) {
            clock.when(Instant::now).thenReturn(start);
            // Varying the high byte gives one key in every bucket.
            for (int i = 0; i < 256; i++) {
                store.set(i << 24, VALUE);
            }

            clock.when(Instant::now).thenReturn(start.plusSeconds(11));
            for (int i = 0; i < 256; i++) {
                store.set((i << 24) + 1, "later");
            }
            // Rewind so reads reveal what the 256 writes removed.
            clock.when(Instant::now).thenReturn(start);
            for (int i = 0; i < 256; i++) {
                assertTrue(store.get(i << 24).isEmpty());
                assertEquals("later", store.get((i << 24) + 1).orElseThrow());
            }
        }
    }

    @Test
    public void testBadTimeout() {
        val store = new ConcurrentMapStore();
        store.setTimeUnit(TimeUnit.SECONDS);
        TestsHelper.expectException(store::init, TechnicalException.class, "timeout must be greater than zero");
    }

    @Test
    public void testBadTimeUnit() {
        val store = new ConcurrentMapStore();
        store.setTimeout(20);
        TestsHelper.expectException(store::init, TechnicalException.class, "timeUnit cannot be null");
    }
}
