package org.pac4j.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Object that can be (re-)initialized.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@Slf4j
public abstract class InitializableObject {

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private int maxAttempts = 3;

    private AtomicInteger nbAttempts = new AtomicInteger(0);

    private volatile Long lastAttempt;

    private long minTimeIntervalBetweenAttemptsInMilliseconds = 5000;

    /**
     * Initialize the object.
     */
    public void init() {
        init(false);
    }

    /**
     * Re-initialize the object.
     */
    public void reinit() {
        init(true);
    }

    /**
     * (Re)-initialize the object.
     *
     * @param forceReinit whether the object should be re-initialized
     */
    public void init(final boolean forceReinit) {
        if (shouldInitialize(forceReinit)) {
            synchronized (this) {
                if (shouldInitialize(forceReinit)) {
                    LOGGER.debug("Initializing: {} (nb: {}, last: {})", this.getClass().getSimpleName(), nbAttempts, lastAttempt);
                    nbAttempts.incrementAndGet();
                    lastAttempt = System.currentTimeMillis();
                    beforeInternalInit(forceReinit);
                    internalInit(forceReinit);
                    afterInternalInit(forceReinit);
                    initialized.set(true);
                }
            }
        }
    }

    public final boolean isInitialized() {
        return initialized.get();
    }

    protected boolean shouldInitialize(final boolean forceReinit) {
        if (forceReinit) {
            return true;
        }

        final boolean notInitialized = !initialized.get();
        final boolean notTooManyAttempts = maxAttempts == -1 || nbAttempts.get() < maxAttempts;
        final boolean enoughTimeSinceLastAttempt = lastAttempt == null
            || (System.currentTimeMillis() - lastAttempt) > minTimeIntervalBetweenAttemptsInMilliseconds;

        return notInitialized && notTooManyAttempts && enoughTimeSinceLastAttempt;
    }

    /**
     * Internal initialization of the object.
     */
    protected abstract void internalInit(final boolean forceReinit);

    protected void beforeInternalInit(final boolean forceReinit) {}

    protected void afterInternalInit(final boolean forceReinit) {}

    public final int getMaxAttempts() {
        return maxAttempts;
    }

    public final void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public final int getNbAttempts() {
        return nbAttempts.get();
    }

    public final Long getLastAttempt() {
        return lastAttempt;
    }

    public final long getMinTimeIntervalBetweenAttemptsInMilliseconds() {
        return minTimeIntervalBetweenAttemptsInMilliseconds;
    }

    public final void setMinTimeIntervalBetweenAttemptsInMilliseconds(long minTimeIntervalBetweenAttemptsInMilliseconds) {
        this.minTimeIntervalBetweenAttemptsInMilliseconds = minTimeIntervalBetweenAttemptsInMilliseconds;
    }
}
