/*
  Copyright 2012 - 2015 pac4j organization

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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.LongConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class LongConverterTests {
    
    private final LongConverter converter = new LongConverter();
    
    private static final long VALUE = 1234567890123L;

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAStringNotAnInteger() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testLong() {
        assertEquals(VALUE, (long) this.converter.convert(VALUE));
    }

    @Test
    public void testLongString() {
        assertEquals(VALUE, (long) this.converter.convert("" + VALUE));
    }
}
