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

import java.util.Locale;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class tests the {@link YahooAddress} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooAddress extends TestCase implements TestsConstants {
    
    private static final boolean CURRENT = true;
    
    private static final Locale COUNTRY = Locale.FRANCE;
    
    private static final String STATE = "Arizona";
    
    private static final String CITY = "Phoenix";
    
    private static final String POSTAL_CODE = "78400";
    
    private static final String STREET = "rue des jardiniers";
    
    private static final String GOOD_JSON = "{\"id\" : " + INT_ID + ", \"current\" : " + CURRENT + ", \"country\" : \""
                                            + COUNTRY + "\", \"state\" : \"" + STATE + "\", \"city\" : \"" + CITY
                                            + "\", \"postalCode\" : \"" + POSTAL_CODE + "\", \"street\" : \"" + STREET
                                            + "\", \"type\" : \"" + TYPE + "\"}";
    
    public void testNull() {
        final YahooAddress yahooAddress = new YahooAddress();
        yahooAddress.buildFrom(null);
        assertNull(yahooAddress.getId());
        assertNull(yahooAddress.getCurrent());
        assertNull(yahooAddress.getCountry());
        assertNull(yahooAddress.getState());
        assertNull(yahooAddress.getCity());
        assertNull(yahooAddress.getPostalCode());
        assertNull(yahooAddress.getStreet());
        assertNull(yahooAddress.getType());
    }
    
    public void testBadJson() {
        final YahooAddress yahooAddress = new YahooAddress();
        yahooAddress.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooAddress.getId());
        assertNull(yahooAddress.getCurrent());
        assertNull(yahooAddress.getCountry());
        assertNull(yahooAddress.getState());
        assertNull(yahooAddress.getCity());
        assertNull(yahooAddress.getPostalCode());
        assertNull(yahooAddress.getStreet());
        assertNull(yahooAddress.getType());
    }
    
    public void testGoodJson() {
        final YahooAddress yahooAddress = new YahooAddress();
        yahooAddress.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(INT_ID, yahooAddress.getId().intValue());
        assertTrue(yahooAddress.getCurrent());
        assertEquals(COUNTRY, yahooAddress.getCountry());
        assertEquals(STATE, yahooAddress.getState());
        assertEquals(CITY, yahooAddress.getCity());
        assertEquals(POSTAL_CODE, yahooAddress.getPostalCode());
        assertEquals(STREET, yahooAddress.getStreet());
        assertEquals(TYPE, yahooAddress.getType());
    }
}
