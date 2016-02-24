package org.pac4j.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link InitializableWebObject} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class InitializableObjectTests {

    @Test
    public void testInit() {
        CounterInitializableWebObject counterInitializableObject = new CounterInitializableWebObject();
        assertEquals(0, counterInitializableObject.getCounter());
        counterInitializableObject.init(null);
        assertEquals(1, counterInitializableObject.getCounter());
        counterInitializableObject.init(null);
        assertEquals(1, counterInitializableObject.getCounter());
    }

    @Test
    public void testReinit() {
        CounterInitializableWebObject counterInitializableObject = new CounterInitializableWebObject();
        assertEquals(0, counterInitializableObject.getCounter());
        counterInitializableObject.reinit(null);
        assertEquals(1, counterInitializableObject.getCounter());
        counterInitializableObject.reinit(null);
        assertEquals(2, counterInitializableObject.getCounter());
    }
}
