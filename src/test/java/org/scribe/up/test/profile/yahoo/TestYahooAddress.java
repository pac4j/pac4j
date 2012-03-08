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

import java.util.Locale;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.yahoo.YahooAddress;

/**
 * This class tests the {@link org.scribe.up.profile.yahoo.YahooAddress} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooAddress extends TestCase {
    
    private static final int ID = 1;
    
    private static final boolean CURRENT = true;
    
    private static final Locale COUNTRY = Locale.FRANCE;
    
    private static final String STATE = "Arizona";
    
    private static final String CITY = "Phoenix";
    
    private static final String POSTAL_CODE = "78400";
    
    private static final String STREET = "rue des jardiniers";
    
    private static final String TYPE = "WORK";
    
    private static final String GOOD_JSON = "{\"id\" : " + ID + ", \"current\" : " + CURRENT + ", \"country\" : \""
                                            + COUNTRY + "\", \"state\" : \"" + STATE + "\", \"city\" : \"" + CITY
                                            + "\", \"postalCode\" : \"" + POSTAL_CODE + "\", \"street\" : \"" + STREET
                                            + "\", \"type\" : \"" + TYPE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        YahooAddress yahooAddress = new YahooAddress(null);
        assertEquals(0, yahooAddress.getId());
        assertFalse(yahooAddress.isCurrent());
        assertNull(yahooAddress.getCountry());
        assertNull(yahooAddress.getState());
        assertNull(yahooAddress.getCity());
        assertNull(yahooAddress.getPostalCode());
        assertNull(yahooAddress.getStreet());
        assertNull(yahooAddress.getType());
    }
    
    public void testBadJson() {
        YahooAddress yahooAddress = new YahooAddress(JsonHelper.getFirstNode(BAD_JSON));
        assertEquals(0, yahooAddress.getId());
        assertFalse(yahooAddress.isCurrent());
        assertNull(yahooAddress.getCountry());
        assertNull(yahooAddress.getState());
        assertNull(yahooAddress.getCity());
        assertNull(yahooAddress.getPostalCode());
        assertNull(yahooAddress.getStreet());
        assertNull(yahooAddress.getType());
    }
    
    public void testGoodJson() {
        YahooAddress yahooAddress = new YahooAddress(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(ID, yahooAddress.getId());
        assertTrue(yahooAddress.isCurrent());
        assertEquals(COUNTRY, yahooAddress.getCountry());
        assertEquals(STATE, yahooAddress.getState());
        assertEquals(CITY, yahooAddress.getCity());
        assertEquals(POSTAL_CODE, yahooAddress.getPostalCode());
        assertEquals(STREET, yahooAddress.getStreet());
        assertEquals(TYPE, yahooAddress.getType());
    }
}
