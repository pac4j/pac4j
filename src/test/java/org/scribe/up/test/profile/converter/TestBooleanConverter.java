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

import junit.framework.TestCase;

import org.scribe.up.profile.converter.BooleanConverter;

/**
 * This class tests the {@link org.scribe.up.profile.converter.BooleanConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestBooleanConverter extends TestCase {
    
    private BooleanConverter converter = new BooleanConverter();
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAStringNotABoolean() {
        assertNull(converter.convert(1));
    }
    
    public void testBooleanFalse() {
        assertEquals(Boolean.FALSE, converter.convert(Boolean.FALSE));
    }
    
    public void testBooleanTrue() {
        assertEquals(Boolean.TRUE, converter.convert(Boolean.TRUE));
    }
    
    public void testFalse() {
        assertEquals(Boolean.FALSE, converter.convert("false"));
    }
    
    public void testTrue() {
        assertEquals(Boolean.TRUE, converter.convert("true"));
    }
}
