package org.pac4j.core.store;

import lombok.ToString;
import lombok.val;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Store data in a concurrent map, the expiration date of an entry being left to the subclasses.
 *
 * <p>Two policies make sense: the same duration for every entry, which the {@link ConcurrentMapStore}
 * implements, or a date the stored value knows about itself, when it carries its own expiration.</p>
 *
 * <p>The date is computed once, when the value is stored. A subclass returning {@code null} keeps its
 * entries forever.</p>
 *
 * <p>This store needs no dependency at all, guava being optional. It has no maximum size.
 * Each write checks the next of the 256 buckets for expired entries, the buckets being checked in turn:
 * an expired entry is dropped within 256 writes. A read also checks the requested entry's expiration.
 * Use the {@link GuavaStore} when the number of entries must be capped.</p>
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
@ToString(exclude = {"buckets", "nextBucketToPurge"})
public abstract class AbstractConcurrentMapStore<K, O> extends AbstractStore<K, O> {

    private static final int BUCKET_COUNT = 256;

    private final List<ConcurrentHashMap<K, Entry<O>>> buckets = IntStream.range(0, BUCKET_COUNT)
        .mapToObj(i -> new ConcurrentHashMap<K, Entry<O>>())
        .toList();

    private final AtomicInteger nextBucketToPurge = new AtomicInteger();

    private ConcurrentHashMap<K, Entry<O>> bucketFor(final K key) {
        // Fibonacci hashing spreads sequential keys across the 256 buckets using the high byte.
        return buckets.get((key.hashCode() * 0x9E3779B9) >>> 24);
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<O> internalGet(final K key) {
        val bucket = bucketFor(key);
        val entry = bucket.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired(Instant.now())) {
            bucket.remove(key, entry);
            return Optional.empty();
        }
        return Optional.of(entry.value());
    }

    /** {@inheritDoc} */
    @Override
    protected void internalSet(final K key, final O value) {
        purgeExpired();
        bucketFor(key).put(key, new Entry<>(value, expiresAt(value)));
    }

    /** {@inheritDoc} */
    @Override
    protected void internalRemove(final K key) {
        bucketFor(key).remove(key);
    }

    /**
     * When a value must be dropped from this store.
     *
     * @param value the value being stored
     * @return the expiration date, or null for a value which never expires
     */
    protected abstract Instant expiresAt(O value);

    /**
     * <p>Drop the expired entries of the next bucket, the buckets being checked in turn.</p>
     */
    protected void purgeExpired() {
        // floorMod keeps the index in range once the counter overflows
        val bucket = buckets.get(Math.floorMod(nextBucketToPurge.getAndIncrement(), BUCKET_COUNT));
        val now = Instant.now();
        bucket.values().removeIf(entry -> entry.isExpired(now));
    }

    /**
     * A stored value and the date it expires at.
     *
     * @param value the stored value
     * @param expiresAt the expiration date, null for a value which never expires
     */
    private record Entry<O>(O value, Instant expiresAt) {

        private boolean isExpired(final Instant now) {
            return expiresAt != null && now.isAfter(expiresAt);
        }
    }
}
