package org.pac4j.core.profile.converter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.StringReplaceConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestStringReplaceConverter {
    
    private final static String GOOD_REGEX = "aa";
    
    private final static String GOOD_REPLACEMENT = "bb";
    
    private final static String BAD_STRING = "11111111";
    
    private final static String GOOD_STRING = "1111" + GOOD_REGEX + "1111";
    
    private final static String GOOD_STRING_REPLACED = "1111" + GOOD_REPLACEMENT + "1111";
    
    private final static StringReplaceConverter converter = new StringReplaceConverter(GOOD_REGEX, GOOD_REPLACEMENT);

    @Test
    public void testNull() {
        assertNull(converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(converter.convert(Boolean.TRUE));
    }

    @Test
    public void testBadRegex() {
        final StringReplaceConverter converter = new StringReplaceConverter(null, GOOD_REPLACEMENT);
        assertNull(converter.convert(GOOD_STRING));
    }

    @Test
    public void testBadReplacement() {
        final StringReplaceConverter converter = new StringReplaceConverter(GOOD_REGEX, null);
        assertNull(converter.convert(GOOD_STRING));
    }

    @Test
    public void testBadString() {
        assertEquals(BAD_STRING, converter.convert(BAD_STRING));
    }

    @Test
    public void testGoodString() {
        assertEquals(GOOD_STRING_REPLACED, converter.convert(GOOD_STRING));
    }
}
