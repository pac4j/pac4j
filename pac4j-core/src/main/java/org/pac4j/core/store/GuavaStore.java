package org.pac4j.core.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
public class GuavaStore<K, O> extends AbstractStore<K, O> {

    private Cache<K, O> cache;

    private int size = 0;

    private int timeout = -1;

    private TimeUnit timeUnit;

    public GuavaStore() {}

    public GuavaStore(final int size, final int timeout, final TimeUnit timeUnit) {
        this.size = size;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertTrue(this.size > 0, "size mut be greater than zero");
        CommonHelper.assertTrue(this.timeout >= 0, "timeout must be greater than zero");
        CommonHelper.assertNotNull("timeUnit", this.timeUnit);

        this.cache = CacheBuilder.newBuilder().maximumSize(this.size)
                .expireAfterWrite(this.timeout, this.timeUnit).build();
    }

    @Override
    protected Optional<O> internalGet(final K key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    protected void internalSet(final K key, final O value) {
        cache.put(key, value);
    }

    @Override
    protected void internalRemove(final K key) {
        cache.invalidate(key);
    }

    public Cache<K, O> getCache() {
        return cache;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "size", size, "timeout", timeout, "timeUnit", timeUnit);
    }
}
