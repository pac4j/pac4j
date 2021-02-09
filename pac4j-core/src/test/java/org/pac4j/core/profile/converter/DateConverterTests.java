package org.pac4j.core.profile.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DateConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class DateConverterTests {

    private static final String FORMAT = "yyyy.MM.dd";

    private final DateConverter converter = new DateConverter(FORMAT);

    private static final String GOOD_DATE = "2012.01.01";

    private static final String BAD_DATE = "2012/01/01";

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testGoodDate() {
        final var d = (Date) this.converter.convert(GOOD_DATE);
        final var simpleDateFormat = new SimpleDateFormat(FORMAT);
        assertEquals(GOOD_DATE, simpleDateFormat.format(d));
    }

    @Test
    public void testBadDate() {
        assertNull(this.converter.convert(BAD_DATE));
    }
}
