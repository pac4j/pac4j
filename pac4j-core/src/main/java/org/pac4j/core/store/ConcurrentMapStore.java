package org.pac4j.core.store;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.util.CommonHelper;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Store data in a concurrent map, dropping every entry after the same timeout.
 *
 * <p>This is the dependency-free counterpart of the {@link GuavaStore}. When the stored values carry their
 * own expiration date, extend {@link AbstractConcurrentMapStore} instead and answer from the value.</p>
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class ConcurrentMapStore<K, O> extends AbstractConcurrentMapStore<K, O> {

    @Getter
    @Setter
    private int timeout = -1;

    @Getter
    @Setter
    private TimeUnit timeUnit;

    /**
     * <p>Constructor for ConcurrentMapStore.</p>
     */
    public ConcurrentMapStore() {}

    /**
     * <p>Constructor for ConcurrentMapStore.</p>
     *
     * @param timeout a int
     * @param timeUnit a {@link TimeUnit} object
     */
    public ConcurrentMapStore(final int timeout, final TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertTrue(this.timeout >= 0, "timeout must be greater than zero");
        CommonHelper.assertNotNull("timeUnit", this.timeUnit);
    }

    /** {@inheritDoc} */
    @Override
    protected Instant expiresAt(final O value) {
        return Instant.now().plusMillis(timeUnit.toMillis(timeout));
    }
}
