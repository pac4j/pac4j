package org.pac4j.core.util;

import org.pac4j.core.context.WebContext;

/**
 * This class is a counter as {@link InitializableWebObject} for tests purpose.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CounterInitializableWebObject extends InitializableWebObject {
    
    private int counter = 0;
    
    @Override
    protected void internalInit(final WebContext context) {
        this.counter++;
    }
    
    public int getCounter() {
        return this.counter;
    }
}
