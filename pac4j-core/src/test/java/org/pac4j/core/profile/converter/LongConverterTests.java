package org.pac4j.core.profile.converter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.LongConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class LongConverterTests {
    
    private final LongConverter converter = new LongConverter();
    
    private static final long VALUE = 1234567890123L;

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotAnInteger() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testLong() {
        assertEquals(VALUE, (long) this.converter.convert(VALUE));
    }

    @Test
    public void testLongString() {
        assertEquals(VALUE, (long) this.converter.convert("" + VALUE));
    }
}
