/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.profile.converter;

import java.awt.Color;

import junit.framework.TestCase;

import org.scribe.up.profile.converter.ColorConverter;

/**
 * This class tests the {@link org.scribe.up.profile.converter.ColorConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestColorConverter extends TestCase {
    
    private ColorConverter converter = new ColorConverter();
    
    private final static String BAD_LENGTH_STRING = "12345";
    
    private final static String BAD_STRING = "zzzzzz";
    
    private final static String GOOD_STRING = "FF0005";
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(converter.convert(Boolean.TRUE));
    }
    
    public void testStringBadLength() {
        assertNull(converter.convert(BAD_LENGTH_STRING));
    }
    
    public void testBadString() {
        assertNull(converter.convert(BAD_STRING));
    }
    
    public void testGoodString() {
        Color color = converter.convert(GOOD_STRING);
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(5, color.getBlue());
    }
}
