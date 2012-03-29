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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.scribe.up.profile.converter.DateConverter;

/**
 * This class tests the {@link org.scribe.up.profile.converter.DateConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestDateConverter extends TestCase {
    
    private static final String FORMAT = "yyyy.MM.dd";
    
    private DateConverter converter = new DateConverter(FORMAT);
    
    private static final String GOOD_DATE = "2012.01.01";
    
    private static final String BAD_DATE = "2012/01/01";
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(converter.convert(Boolean.TRUE));
    }
    
    public void testGoodDate() {
        Date d = converter.convert(GOOD_DATE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        assertEquals(GOOD_DATE, simpleDateFormat.format(d));
    }
    
    public void testBadDate() {
        assertNull(converter.convert(BAD_DATE));
    }
}
