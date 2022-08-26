package org.pac4j.core.profile.converter;

import org.junit.Test;
import org.pac4j.core.util.Pac4jConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.LongConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class LongConverterTests {

    private final LongConverter converter = new LongConverter();

    private static final int INT_VALUE = 5;
    private static final long LONG_VALUE = 1234567890123L;

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
        assertEquals(LONG_VALUE, (long) this.converter.convert(LONG_VALUE));
    }

    @Test
    public void testLongString() {
        assertEquals(LONG_VALUE, (long) this.converter.convert(Pac4jConstants.EMPTY_STRING + LONG_VALUE));
    }

    @Test
    public void testInteger() {
        assertEquals((long) INT_VALUE, (long) this.converter.convert(Integer.valueOf(INT_VALUE)));
    }
}
