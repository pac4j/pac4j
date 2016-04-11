package org.pac4j.core.util;

import org.pac4j.core.context.WebContext;

/**
 * Object that can be (re-)initialized, taking the web context into account.
 * 
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class InitializableWebObject {
    
    private volatile boolean initialized = false;
    
    /**
     * Initialize the object.
     *
     * @param context the web context
     */
    public void init(final WebContext context) {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    internalInit(context);
                    this.initialized = true;
                }
            }
        }
    }
    
    /**
     * Force (again) the initialization of the object.
     *
     * @param context the web context
     */
    public synchronized void reinit(final WebContext context) {
        internalInit(context);
        this.initialized = true;
    }
    
    /**
     * Internal initialization of the object.
     *
     * @param context the web context
     */
    protected abstract void internalInit(final WebContext context);
}
