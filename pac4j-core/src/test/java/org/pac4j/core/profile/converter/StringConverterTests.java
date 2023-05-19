package org.pac4j.core.profile.converter;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class tests the {@link StringConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverterTests implements TestsConstants {

    private final AttributeConverter converter = new StringConverter();

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testListNull() {
        assertNull(this.converter.convert(new ArrayList<>()));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testString() {
        assertEquals(VALUE, this.converter.convert(VALUE));
    }

    @Test
    public void testListString() {
        assertEquals(VALUE, this.converter.convert(List.of(VALUE)));
    }
}
