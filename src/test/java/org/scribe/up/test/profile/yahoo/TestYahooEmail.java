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
package org.scribe.up.test.profile.yahoo;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.yahoo.YahooEmail;

/**
 * This class tests the {@link org.scribe.up.profile.yahoo.YahooEmail} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooEmail extends TestCase {
    
    private static final int ID = 1;
    
    private static final boolean PRIMARY = true;
    
    private static final String HANDLE = "testscribeup@gmail.com";
    
    private static final String TYPE = "HOME";
    
    private static final String GOOD_JSON = "{\"id\" : " + ID + ", \"primary\" : " + PRIMARY + ", \"handle\" : \""
                                            + HANDLE + "\", \"type\" : \"" + TYPE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        YahooEmail yahooEmail = new YahooEmail(null);
        assertEquals(0, yahooEmail.getId());
        assertFalse(yahooEmail.isPrimary());
        assertNull(yahooEmail.getHandle());
        assertNull(yahooEmail.getType());
    }
    
    public void testBadJson() {
        YahooEmail yahooEmail = new YahooEmail(JsonHelper.getFirstNode(BAD_JSON));
        assertEquals(0, yahooEmail.getId());
        assertFalse(yahooEmail.isPrimary());
        assertNull(yahooEmail.getHandle());
        assertNull(yahooEmail.getType());
    }
    
    public void testGoodJson() {
        YahooEmail yahooEmail = new YahooEmail(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(ID, yahooEmail.getId());
        assertTrue(yahooEmail.isPrimary());
        assertEquals(HANDLE, yahooEmail.getHandle());
        assertEquals(TYPE, yahooEmail.getType());
    }
}
