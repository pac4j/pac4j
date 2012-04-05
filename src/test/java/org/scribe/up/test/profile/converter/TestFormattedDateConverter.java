package org.scribe.up.test.profile.converter;

import java.util.Locale;

import junit.framework.TestCase;

import org.scribe.up.profile.FormattedDate;
import org.scribe.up.profile.converter.FormattedDateConverter;
import org.scribe.up.test.util.CommonHelper;

public final class TestFormattedDateConverter extends TestCase {
    
    private final static String FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    
    private final static Locale LOCALE = Locale.FRANCE;
    
    private FormattedDateConverter converter = new FormattedDateConverter(FORMAT, LOCALE);
    
    private final static String DATE = CommonHelper.getFormattedDate(0, FORMAT, LOCALE);
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(converter.convert(1));
    }
    
    public void testDate() {
        FormattedDate d = converter.convert(DATE);
        assertEquals(DATE, d.toString());
    }
}
