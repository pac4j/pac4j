package org.pac4j.core.resource;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.InitializableObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

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

    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean byteArrayHasChanged = new AtomicBoolean(true);
    @Getter
    private long lastModified = NO_LAST_MODIFIED;

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
                if (hasChanged()) {
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
            val hasChanged = lastModified != newLastModified;
            LOGGER.debug("lastModified: {} / newLastModified: {} -> hasChanged: {}", lastModified, newLastModified, hasChanged);
            lastModified = newLastModified;
            return hasChanged;
        }
        return false;
    }

    /**
     * <p>internalLoad.</p>
     */
    protected abstract void internalLoad();
}
