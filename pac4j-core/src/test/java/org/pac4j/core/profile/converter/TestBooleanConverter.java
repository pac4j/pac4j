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

/**
 * This class tests the {@link BooleanConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestBooleanConverter extends TestCase {
    
    private final BooleanConverter converter = new BooleanConverter();
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAStringNotABoolean() {
        assertNull(this.converter.convert(1));
    }
    
    public void testBooleanFalse() {
        assertEquals(Boolean.FALSE, this.converter.convert(Boolean.FALSE));
    }
    
    public void testBooleanTrue() {
        assertEquals(Boolean.TRUE, this.converter.convert(Boolean.TRUE));
    }
    
    public void testFalse() {
        assertEquals(Boolean.FALSE, this.converter.convert("false"));
    }
    
    public void testTrue() {
        assertEquals(Boolean.TRUE, this.converter.convert("true"));
    }
}
