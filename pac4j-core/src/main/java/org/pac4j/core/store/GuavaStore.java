package org.pac4j.core.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Store data in a Guava cache.
 *
 * Add the <code>guava</code> dependency to use this store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString
public class GuavaStore<K, O> extends AbstractStore<K, O> {

    @Getter
    private Cache<K, O> cache;

    @Getter
    @Setter
    private int size = 0;

    @Getter
    @Setter
    private int timeout = -1;

    @Getter
    @Setter
    private TimeUnit timeUnit;

    /**
     * <p>Constructor for GuavaStore.</p>
     */
    public GuavaStore() {}

    /**
     * <p>Constructor for GuavaStore.</p>
     *
     * @param size a int
     * @param timeout a int
     * @param timeUnit a {@link TimeUnit} object
     */
    public GuavaStore(final int size, final int timeout, final TimeUnit timeUnit) {
        this.size = size;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertTrue(this.size > 0, "size mut be greater than zero");
        CommonHelper.assertTrue(this.timeout >= 0, "timeout must be greater than zero");
        CommonHelper.assertNotNull("timeUnit", this.timeUnit);

        this.cache = CacheBuilder.newBuilder().maximumSize(this.size)
                .expireAfterWrite(this.timeout, this.timeUnit).build();
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<O> internalGet(final K key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    /** {@inheritDoc} */
    @Override
    protected void internalSet(final K key, final O value) {
        cache.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalRemove(final K key) {
        cache.invalidate(key);
    }
}
