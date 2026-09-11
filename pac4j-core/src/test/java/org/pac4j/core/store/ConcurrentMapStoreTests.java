package org.pac4j.core.store;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;

import java.util.concurrent.TimeUnit;

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
