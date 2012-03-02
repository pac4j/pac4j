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
package org.scribe.up.test.util;

import junit.framework.TestCase;

import org.scribe.up.util.StringHelper;

/**
 * This class tests the StringHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestStringHelper extends TestCase {
    
    private static final String PART1 = "5645646547";
    
    private static final String PART2 = "86431231848684534";
    
    private static final String PART3 = "156446316448645";
    
    private static final String BEGIN = "begin";
    
    private static final String END = "end";
    
    public void testSubstringNoBeginNoEnd() {
        String s = PART1 + PART2 + PART3;
        assertNull(StringHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoEnd() {
        String s = PART1 + BEGIN + PART2 + PART3;
        assertNull(StringHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoBegin() {
        String s = PART1 + PART2 + END + PART3;
        assertNull(StringHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringOk() {
        String s = PART1 + BEGIN + PART2 + END + PART3;
        assertEquals(PART2, StringHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testNull() {
        assertTrue(StringHelper.isBlank(null));
    }
    
    public void testEmptyString() {
        assertTrue(StringHelper.isBlank(""));
    }
    
    public void testBlankString() {
        assertTrue(StringHelper.isBlank("       "));
    }
    
    public void testNotBlankString() {
        assertFalse(StringHelper.isBlank("string"));
    }
}
