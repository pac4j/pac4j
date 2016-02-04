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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link DateConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class DateConverterTests {
    
    private static final String FORMAT = "yyyy.MM.dd";
    
    private final DateConverter converter = new DateConverter(FORMAT);
    
    private static final String GOOD_DATE = "2012.01.01";
    
    private static final String BAD_DATE = "2012/01/01";

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testGoodDate() {
        final Date d = this.converter.convert(GOOD_DATE);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        assertEquals(GOOD_DATE, simpleDateFormat.format(d));
    }

    @Test
    public void testBadDate() {
        assertNull(this.converter.convert(BAD_DATE));
    }
}
