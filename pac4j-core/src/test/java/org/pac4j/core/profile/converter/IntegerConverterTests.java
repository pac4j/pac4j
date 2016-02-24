package org.pac4j.core.profile.converter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.IntegerConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class IntegerConverterTests {
    
    private final IntegerConverter converter = new IntegerConverter();
    
    private static final int VALUE = 12;

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotAnInteger() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testInteger() {
        assertEquals(VALUE, (int) this.converter.convert(VALUE));
    }

    @Test
    public void testIntegerString() {
        assertEquals(VALUE, (int) this.converter.convert("" + VALUE));
    }
}
