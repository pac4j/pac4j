package org.pac4j.core.profile.converter;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * This class tests the {@link BooleanConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class BooleanConverterTests {

    private final BooleanConverter converter = new BooleanConverter();

    @Test
    public void testNull() {
        assertFalse((Boolean) this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotABoolean() {
        assertFalse((Boolean) this.converter.convert(new Date()));
    }

    @Test
    public void testBooleanFalse() {
        assertEquals(Boolean.FALSE, this.converter.convert(Boolean.FALSE));
    }

    @Test
    public void testBooleanTrue() {
        assertEquals(Boolean.TRUE, this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testFalse() {
        assertEquals(Boolean.FALSE, this.converter.convert("false"));
    }

    @Test
    public void testTrue() {
        assertEquals(Boolean.TRUE, this.converter.convert("true"));
    }

    @Test
    public void testOneString() {
        assertEquals(Boolean.TRUE, this.converter.convert("1"));
    }

    @Test
    public void testOneNumber() {
        assertEquals(Boolean.TRUE, this.converter.convert(1));
    }
}
