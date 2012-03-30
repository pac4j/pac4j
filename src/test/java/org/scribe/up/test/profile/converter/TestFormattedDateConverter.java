package org.scribe.up.test.profile.converter;

import java.util.Locale;

import junit.framework.TestCase;

import org.scribe.up.profile.FormattedDate;
import org.scribe.up.profile.converter.FormattedDateConverter;

public final class TestFormattedDateConverter extends TestCase {
    
    private FormattedDateConverter converter = new FormattedDateConverter("EEE MMM dd HH:mm:ss Z yyyy", Locale.FRANCE);
    
    private final static String DATE = "jeu. janv. 01 01:00:00 +0100 1970";
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(converter.convert(1));
    }
    
    public void testDate() {
        FormattedDate d = (FormattedDate) converter.convert(DATE);
        assertEquals(DATE, d.toString());
    }
}
