package org.pac4j.core.store;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;

import java.util.concurrent.TimeUnit;

/**
 * Test {@link GuavaStore}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class GuavaStoreTests extends AbstractStoreTests<GuavaStore> {

    @Override
    protected GuavaStore buildStore() {
        return new GuavaStore(10, 1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testBadSize() {
        val store = new GuavaStore();
        store.setTimeout(15);
        store.setTimeUnit(TimeUnit.SECONDS);
        TestsHelper.expectException(store::init, TechnicalException.class, "size mut be greater than zero");
    }

    @Test
    public void testBadTimeout() {
        val store = new GuavaStore();
        store.setSize(15);
        store.setTimeUnit(TimeUnit.SECONDS);
        TestsHelper.expectException(store::init, TechnicalException.class, "timeout must be greater than zero");
    }

    @Test
    public void testBadTimeUnit() {
        val store = new GuavaStore();
        store.setSize(15);
        store.setTimeout(20);
        TestsHelper.expectException(store::init, TechnicalException.class, "timeUnit cannot be null");
    }
}
