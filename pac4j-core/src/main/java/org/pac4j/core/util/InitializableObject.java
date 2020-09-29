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

    /**
     * Initialize the object.
     */
    public void init() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    LOGGER.debug("Initializing: {}", this.getClass().getSimpleName());
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

    /**
     * Internal initialization of the object.
     */
    protected abstract void internalInit();

    protected void beforeInternalInit() {}

    protected void afterInternalInit() {}
}
