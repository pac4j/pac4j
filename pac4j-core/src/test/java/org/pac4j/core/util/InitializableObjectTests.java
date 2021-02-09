package org.pac4j.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link InitializableObject} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class InitializableObjectTests {

    @Test
    public void testInit() {
        var counterInitializableObject = new CounterInitializableObject();
        assertEquals(0, counterInitializableObject.getCounter());
        counterInitializableObject.init();
        assertEquals(1, counterInitializableObject.getCounter());
        counterInitializableObject.init();
        assertEquals(1, counterInitializableObject.getCounter());
    }
}
