package org.pac4j.core.util;

/**
 * Object that can be (re-)initialized.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class InitializableObject {

    private volatile boolean initialized = false;

    /**
     * Initialize the object.
     */
    public void init() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    internalInit();
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
}
