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
package org.pac4j.oauth.profile.yahoo;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class tests the {@link YahooEmail} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooEmail extends TestCase implements TestsConstants {
    
    private static final boolean PRIMARY = true;
    
    private static final String HANDLE = "testscribeup@gmail.com";
    
    private static final String GOOD_JSON = "{\"id\" : " + INT_ID + ", \"primary\" : " + PRIMARY + ", \"handle\" : \""
                                            + HANDLE + "\", \"type\" : \"" + TYPE + "\"}";
    
    public void testNull() {
        final YahooEmail yahooEmail = new YahooEmail();
        yahooEmail.buildFrom(null);
        assertNull(yahooEmail.getId());
        assertNull(yahooEmail.getPrimary());
        assertNull(yahooEmail.getHandle());
        assertNull(yahooEmail.getType());
    }
    
    public void testBadJson() {
        final YahooEmail yahooEmail = new YahooEmail();
        yahooEmail.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooEmail.getId());
        assertNull(yahooEmail.getPrimary());
        assertNull(yahooEmail.getHandle());
        assertNull(yahooEmail.getType());
    }
    
    public void testGoodJson() {
        final YahooEmail yahooEmail = new YahooEmail();
        yahooEmail.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(INT_ID, yahooEmail.getId().intValue());
        assertTrue(yahooEmail.getPrimary());
        assertEquals(HANDLE, yahooEmail.getHandle());
        assertEquals(TYPE, yahooEmail.getType());
    }
}
