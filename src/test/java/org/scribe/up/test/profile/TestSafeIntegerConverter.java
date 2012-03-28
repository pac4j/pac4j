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
package org.scribe.up.test.profile;

import junit.framework.TestCase;

import org.scribe.up.profile.SafeIntegerConverter;

/**
 * This class tests the {@link org.scribe.up.profile.SafeIntegerConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestSafeIntegerConverter extends TestCase {
    
    private SafeIntegerConverter converter = new SafeIntegerConverter();
    
    private static final int VALUE = 12;
    
    public void testNull() {
        assertEquals(0, (int) converter.convert(null));
    }
    
    public void testInteger() {
        assertEquals(VALUE, (int) converter.convert("" + VALUE));
    }
}
