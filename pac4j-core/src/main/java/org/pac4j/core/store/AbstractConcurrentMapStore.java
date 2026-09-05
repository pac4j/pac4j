package org.pac4j.core.store;

import lombok.ToString;
import lombok.val;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store data in a concurrent map, the expiration date of an entry being left to the subclasses.
 *
 * <p>Two policies make sense: the same duration for every entry, which the {@link ConcurrentMapStore}
 * implements, or a date the stored value knows about itself, when it carries its own expiration.</p>
 *
 * <p>The date is computed once, when the value is stored. A subclass returning {@code null} keeps its
 * entries forever.</p>
 *
 * <p>This store needs no dependency at all, guava being optional. It has no maximum size though: entries are
 * only dropped when they expire, on write and when read. Use the {@link GuavaStore} when the number of
 * entries must be capped.</p>
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
@ToString(exclude = "map")
public abstract class AbstractConcurrentMapStore<K, O> extends AbstractStore<K, O> {

    private final Map<K, Entry<O>> map = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    protected Optional<O> internalGet(final K key) {
        val entry = map.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            map.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.value());
    }

    /** {@inheritDoc} */
    @Override
    protected void internalSet(final K key, final O value) {
        purgeExpired();
        map.put(key, new Entry<>(value, expiresAt(value)));
    }

    /** {@inheritDoc} */
    @Override
    protected void internalRemove(final K key) {
        map.remove(key);
    }

    /**
     * When a value must be dropped from this store.
     *
     * @param value the value being stored
     * @return the expiration date, or null for a value which never expires
     */
    protected abstract Instant expiresAt(O value);

    /**
     * <p>Drop every expired entry.</p>
     */
    protected void purgeExpired() {
        map.values().removeIf(Entry::isExpired);
    }

    /**
     * A stored value and the date it expires at.
     *
     * @param value the stored value
     * @param expiresAt the expiration date, null for a value which never expires
     */
    private record Entry<O>(O value, Instant expiresAt) {

        private boolean isExpired() {
            return expiresAt != null && Instant.now().isAfter(expiresAt);
        }
    }
}
