package org.pac4j.core.profile.converter;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.profile.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link ColorConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ColorConverterTests {

    private final static String BAD_LENGTH_COLOR = "12345";

    private final static String BAD_COLOR = "zzzzzz";

    private final static String GOOD_COLOR = "FF0005";

    private final ColorConverter converter = new ColorConverter();

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testStringBadLength() {
        assertNull(this.converter.convert(BAD_LENGTH_COLOR));
    }

    @Test
    public void testBadString() {
        assertNull(this.converter.convert(BAD_COLOR));
    }

    @Test
    public void testGoodString() {
        val color = (Color) this.converter.convert(GOOD_COLOR);
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(5, color.getBlue());
    }

    @Test
    public void testColorToString() {
        val color = new Color(10, 20, 30);
        val color2 = (Color) this.converter.convert(color.toString());
        assertEquals(color.getRed(), color2.getRed());
        assertEquals(color.getGreen(), color2.getGreen());
        assertEquals(color.getBlue(), color2.getBlue());
    }
}
