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

import org.pac4j.core.profile.converter.StringConverter;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.StringConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestStringConverter extends TestCase {
    
    private final StringConverter converter = new StringConverter();
    
    private static final String VALUE = "value";
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }
    
    public void testString() {
        assertEquals(VALUE, this.converter.convert(VALUE));
    }
}
