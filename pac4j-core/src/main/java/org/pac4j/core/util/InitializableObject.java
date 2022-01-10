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
        if (shouldInitialize()) {
            synchronized (this) {
                if (shouldInitialize()) {
                    LOGGER.debug("Initializing: {} (nb: {}, last: {})", this.getClass().getSimpleName(), nbAttempts, lastAttempt);
                    nbAttempts++;
                    lastAttempt = System.currentTimeMillis();
                    beforeInternalInit();
                    internalInit();
                    afterInternalInit();
                    this.initialized = true;
                }
            }
        }
    }

    public final boolean isInitialized() {
        return this.initialized;
    }

    protected boolean shouldInitialize() {
        final boolean notInitialized = !this.initialized;
        final boolean notTooManyAttempts = maxAttempts == -1 || nbAttempts < maxAttempts;
        final boolean enoughTimeSinceLastAttempt = lastAttempt == null
            || (System.currentTimeMillis() - lastAttempt) > minTimeIntervalBetweenAttemptsInMilliseconds;

        return notInitialized && notTooManyAttempts && enoughTimeSinceLastAttempt;
    }

    /**
     * Internal initialization of the object.
     */
    protected abstract void internalInit();

    protected void beforeInternalInit() {}

    protected void afterInternalInit() {}

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
