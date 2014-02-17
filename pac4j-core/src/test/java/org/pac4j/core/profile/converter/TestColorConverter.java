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

import org.pac4j.core.profile.Color;

/**
 * This class tests the {@link ColorConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestColorConverter extends TestCase {
    
    private final static String BAD_LENGTH_COLOR = "12345";
    
    private final static String BAD_COLOR = "zzzzzz";
    
    private final static String GOOD_COLOR = "FF0005";
    
    private final ColorConverter converter = new ColorConverter();
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }
    
    public void testStringBadLength() {
        assertNull(this.converter.convert(BAD_LENGTH_COLOR));
    }
    
    public void testBadString() {
        assertNull(this.converter.convert(BAD_COLOR));
    }
    
    public void testGoodString() {
        final Color color = this.converter.convert(GOOD_COLOR);
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(5, color.getBlue());
    }
    
    public void testColorToString() {
        final Color color = new Color(10, 20, 30);
        final Color color2 = this.converter.convert(color.toString());
        assertEquals(color.getRed(), color2.getRed());
        assertEquals(color.getGreen(), color2.getGreen());
        assertEquals(color.getBlue(), color2.getBlue());
    }
}
