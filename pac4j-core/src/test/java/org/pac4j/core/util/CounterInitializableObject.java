package org.pac4j.core.util;

/**
 * This class is a counter as {@link InitializableObject} for tests purpose.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CounterInitializableObject extends InitializableObject {

    private int counter = 0;

    @Override
    protected void internalInit() {
        this.counter++;
    }

    public int getCounter() {
        return this.counter;
    }
}
