package org.pac4j.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object that can be (re-)initialized.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class InitializableObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializableObject.class);

    private volatile boolean initialized = false;

    private int maxAttempts = 3;

    private int nbAttempts = 0;

    private Long lastAttempt;

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
                    nbAttempts++;
                    lastAttempt = System.currentTimeMillis();
                    beforeInternalInit(forceReinit);
                    internalInit(forceReinit);
                    afterInternalInit(forceReinit);
                    this.initialized = true;
                }
            }
        }
    }

    public final boolean isInitialized() {
        return this.initialized;
    }

    protected boolean shouldInitialize(final boolean forceReinit) {
        if (forceReinit) {
            return true;
        }

        final boolean notInitialized = !this.initialized;
        final boolean notTooManyAttempts = maxAttempts == -1 || nbAttempts < maxAttempts;
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
        return nbAttempts;
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
