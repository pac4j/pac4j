package org.pac4j.core.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString
@Getter
public abstract class InitializableObject {

    private static final String UNKNOWN = "unknown";

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private volatile boolean initializing;

    @Setter
    private volatile int maxAttempts = 3;

    private final AtomicInteger nbAttempts = new AtomicInteger(0);

    private volatile Long lastAttempt;

    @Setter
    private volatile long minTimeIntervalBetweenAttemptsInMilliseconds = 5000;

    /**
     * Initialize the object.
     *
     * @param caller the caller
     */
    public void init(final String caller) {
        init(caller, false);
    }

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
        init(UNKNOWN, forceReinit);
    }

    /**
     * (Re)-initialize the object.
     *
     * @param caller the caller
     * @param forceReinit whether the object should be re-initialized
     */
    public void init(final String caller, final boolean forceReinit) {
        if (initializing) {
            LOGGER.info("Initializing: {}, ignoring caller: {}",
                this.getClass().getSimpleName(), caller);
            return;
        }
        if (shouldInitialize(forceReinit)) {
            synchronized (this) {
                if (initializing) {
                    LOGGER.info("Initializing: {}, ignoring caller: {}",
                        this.getClass().getSimpleName(), caller);
                    return;
                }
                if (shouldInitialize(forceReinit)) {
                    LOGGER.debug("Initializing: {} (nb: {}, last: {})", this.getClass().getSimpleName(), nbAttempts, lastAttempt);
                    initializing = true;
                    nbAttempts.incrementAndGet();
                    lastAttempt = System.currentTimeMillis();
                    try {
                        beforeInternalInit(forceReinit);
                        internalInit(forceReinit);
                        afterInternalInit(forceReinit);
                        initialized.set(true);
                    } finally {
                        initializing = false;
                    }
                }
            }
        }
    }

    /**
     * <p>isInitialized.</p>
     *
     * @return a boolean
     */
    public final boolean isInitialized() {
        return initialized.get();
    }

    /**
     * <p>shouldInitialize.</p>
     *
     * @param forceReinit a boolean
     * @return a boolean
     */
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
     *
     * @param forceReinit a boolean
     */
    protected abstract void internalInit(final boolean forceReinit);

    /**
     * <p>beforeInternalInit.</p>
     *
     * @param forceReinit a boolean
     */
    protected void beforeInternalInit(final boolean forceReinit) {}

    /**
     * <p>afterInternalInit.</p>
     *
     * @param forceReinit a boolean
     */
    protected void afterInternalInit(final boolean forceReinit) {}

    /**
     * <p>Getter for the field <code>nbAttempts</code>.</p>
     *
     * @return a int
     */
    public int getNbAttempts() {
        return nbAttempts.get();
    }
}
