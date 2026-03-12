package org.pac4j.core.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A loader based on a Spring Resource.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SpringResourceLoader<M> extends InitializableObject {
    private static final long NO_LAST_MODIFIED = -1;
    private static final long DEFAULT_MINIMUM_DELAY_BETWEEN_CHANGE_DETECTION_IN_MILLISECONDS = 60_000;

    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean byteArrayHasChanged = new AtomicBoolean(true);
    private final AtomicLong lastModified = new AtomicLong(NO_LAST_MODIFIED);
    private final AtomicLong lastTimeCheckedForChanges = new AtomicLong(0);

    protected final Resource resource;

    protected M loaded;
    private volatile long minimumDelayBetweenChangeDetectionInMilliseconds = DEFAULT_MINIMUM_DELAY_BETWEEN_CHANGE_DETECTION_IN_MILLISECONDS;

    /** {@inheritDoc} */
    protected final void internalInit(final boolean forceReinit) {
        internalLoad();
        hasChanged();
    }

    /**
     * <p>load.</p>
     *
     * @return a M object
     */
    public final M load() {
        if (lock.tryLock()) {
            try {
                if (shouldCheckForChanges() && hasChanged()) {
                    internalLoad();
                }
            } finally {
                lock.unlock();
            }
        }
        return loaded;
    }

    /**
     * <p>hasChanged.</p>
     *
     * @return a boolean
     */
    public boolean hasChanged() {
        lastTimeCheckedForChanges.set(System.currentTimeMillis());
        if (resource != null) {
            if (resource instanceof ByteArrayResource) {
                return byteArrayHasChanged.getAndSet(false);
            }

            long newLastModified;
            try {
                newLastModified = resource.lastModified();
            } catch (final Exception e) {
                newLastModified = NO_LAST_MODIFIED;
            }
            val hasChanged = lastModified.get() != newLastModified;
            LOGGER.debug("lastModified: {} / newLastModified: {} -> hasChanged: {}", lastModified.get(), newLastModified, hasChanged);
            lastModified.set(newLastModified);
            return hasChanged;
        }
        return false;
    }

    private boolean shouldCheckForChanges() {
        val now = System.currentTimeMillis();
        val elapsed = now - lastTimeCheckedForChanges.get();
        val shouldCheck = elapsed >= minimumDelayBetweenChangeDetectionInMilliseconds;
        LOGGER.debug("elapsed: {} / checkInterval: {} -> shouldCheck: {}",
            elapsed, minimumDelayBetweenChangeDetectionInMilliseconds, shouldCheck);
        return shouldCheck;
    }

    /**
     * <p>internalLoad.</p>
     */
    protected abstract void internalLoad();

    /**
     * <p>Getter for the field <code>lastModified</code>.</p>
     *
     * @return a long
     */
    public long getLastModified() {
        return lastModified.get();
    }

    /**
     * <p>Getter for the field <code>minimumDelayBetweenChangeDetectionInMilliseconds</code>.</p>
     *
     * @return a long
     */
    public long getMinimumDelayBetweenChangeDetectionInMilliseconds() {
        return minimumDelayBetweenChangeDetectionInMilliseconds;
    }

    /**
     * <p>Setter for the field <code>minimumDelayBetweenChangeDetectionInMilliseconds</code>.</p>
     *
     * @param minimumDelayBetweenChangeDetectionInMilliseconds a long
     */
    public void setMinimumDelayBetweenChangeDetectionInMilliseconds(long minimumDelayBetweenChangeDetectionInMilliseconds) {
        CommonHelper.assertTrue(minimumDelayBetweenChangeDetectionInMilliseconds >= 0,
            "minimumDelayBetweenChangeDetectionInMilliseconds must be greater than or equal to zero");
        this.minimumDelayBetweenChangeDetectionInMilliseconds = minimumDelayBetweenChangeDetectionInMilliseconds;
    }
}
