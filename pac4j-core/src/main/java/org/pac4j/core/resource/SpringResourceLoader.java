package org.pac4j.core.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    private static final long RETRY_DELAY_BEFORE_FIRST_LOAD = 2_000;
    private static final long RETRY_DELAY_AFTER_FIRST_LOAD = 60_000;

    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean byteArrayHasChanged = new AtomicBoolean(true);
    private final AtomicLong lastModified = new AtomicLong(NO_LAST_MODIFIED);
    private final AtomicLong lastTimeCheckedForChanges = new AtomicLong(0);

    protected final Resource resource;

    protected M loaded;

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
                    try {
                        internalLoad();
                    } catch (final RuntimeException e) {
                        if (loaded == null) {
                            throw e;
                        }
                        // keep serving the last known value (graceful degradation) instead of failing hard
                        LOGGER.error("Failed to reload resource, serving last known value", e);
                    }
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

    protected boolean shouldCheckForChanges() {
        val now = System.currentTimeMillis();
        val elapsed = now - lastTimeCheckedForChanges.get();
        var minimumDelay = RETRY_DELAY_AFTER_FIRST_LOAD;
        if (loaded == null) {
            minimumDelay = RETRY_DELAY_BEFORE_FIRST_LOAD;
        }
        val shouldCheck = elapsed >= minimumDelay;
        LOGGER.debug("elapsed: {} / checkInterval: {} -> shouldCheck: {}", elapsed, minimumDelay, shouldCheck);
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

    @Deprecated
    public long getMinimumDelayBetweenChangeDetectionInMilliseconds() {
        throw new UnsupportedOperationException("Cannot get minimumDelayBetweenChangeDetectionInMilliseconds for SpringResourceLoader");
    }

    @Deprecated
    public void setMinimumDelayBetweenChangeDetectionInMilliseconds(long minimumDelayBetweenChangeDetectionInMilliseconds) {
        throw new UnsupportedOperationException("Cannot set minimumDelayBetweenChangeDetectionInMilliseconds for SpringResourceLoader");
    }
}
