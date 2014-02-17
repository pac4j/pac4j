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

import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.converter.GenderConverter;

/**
 * This class tests the {@link GenderConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestGenderConverter extends TestCase {
    
    private static final String MALE = "m";
    
    private static final String FEMALE = "f";
    
    private static final String UNSPECIFIED = "unspecified";
    
    private final GenderConverter converter = new GenderConverter(MALE, FEMALE);
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }
    
    public void testMale() {
        assertEquals(Gender.MALE, this.converter.convert(MALE));
    }
    
    public void testFemale() {
        assertEquals(Gender.FEMALE, this.converter.convert(FEMALE));
    }
    
    public void testUnspecified() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(UNSPECIFIED));
    }
    
    public void testMaleEnum() {
        assertEquals(Gender.MALE, this.converter.convert(Gender.MALE.toString()));
    }
    
    public void testFemaleEnum() {
        assertEquals(Gender.FEMALE, this.converter.convert(Gender.FEMALE.toString()));
    }
    
    public void testUnspecifiedEnum() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(Gender.UNSPECIFIED.toString()));
    }
}
