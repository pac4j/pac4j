package org.pac4j.core.profile.converter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.StringConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverterTests {
    
    private final StringConverter converter = new StringConverter();
    
    private static final String VALUE = "value";

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testString() {
        assertEquals(VALUE, this.converter.convert(VALUE));
    }
}
