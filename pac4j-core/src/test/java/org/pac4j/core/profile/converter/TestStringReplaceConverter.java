/*
  Copyright 2012 - 2014 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.profile.converter;

import junit.framework.TestCase;

import org.pac4j.core.profile.converter.StringReplaceConverter;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.StringReplaceConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestStringReplaceConverter extends TestCase {
    
    private final static String GOOD_REGEX = "aa";
    
    private final static String GOOD_REPLACEMENT = "bb";
    
    private final static String BAD_STRING = "11111111";
    
    private final static String GOOD_STRING = "1111" + GOOD_REGEX + "1111";
    
    private final static String GOOD_STRING_REPLACED = "1111" + GOOD_REPLACEMENT + "1111";
    
    private final static StringReplaceConverter converter = new StringReplaceConverter(GOOD_REGEX, GOOD_REPLACEMENT);
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(converter.convert(Boolean.TRUE));
    }
    
    public void testBadRegex() {
        final StringReplaceConverter converter = new StringReplaceConverter(null, GOOD_REPLACEMENT);
        assertNull(converter.convert(GOOD_STRING));
    }
    
    public void testBadReplacement() {
        final StringReplaceConverter converter = new StringReplaceConverter(GOOD_REGEX, null);
        assertNull(converter.convert(GOOD_STRING));
    }
    
    public void testBadString() {
        assertEquals(BAD_STRING, converter.convert(BAD_STRING));
    }
    
    public void testGoodString() {
        assertEquals(GOOD_STRING_REPLACED, converter.convert(GOOD_STRING));
    }
}
