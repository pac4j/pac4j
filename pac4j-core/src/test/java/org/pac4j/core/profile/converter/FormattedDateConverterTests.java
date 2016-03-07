package org.pac4j.core.profile.converter;

import java.util.Locale;

import org.junit.Test;
import org.pac4j.core.profile.FormattedDate;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link FormattedDateConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FormattedDateConverterTests {
    
    private final static String FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    
    private final static Locale LOCALE = Locale.FRANCE;
    
    private final FormattedDateConverter converter = new FormattedDateConverter(FORMAT, LOCALE);
    
    private final static String DATE = TestsHelper.getFormattedDate(0, FORMAT, LOCALE);

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(1));
    }

    @Test
    public void testDate() {
        final FormattedDate d = this.converter.convert(DATE);
        assertEquals(DATE, d.toString());
    }
}
