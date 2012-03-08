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
import org.scribe.up.profile.yahoo.YahooInterest;

/**
 * This class tests the {@link org.scribe.up.profile.yahoo.YahooInterest} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */

public final class TestYahooInterest extends TestCase {
    
    private final static String DECLARED_INTERESTS = "declaredInterests";
    
    private final static String INTEREST_CATEGORY = "interestCategory";
    
    private static final String GOOD_JSON = "{\"declaredInterests\" : [\"" + DECLARED_INTERESTS
                                            + "\"], \"interestCategory\" : \"" + INTEREST_CATEGORY + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        YahooInterest yahooInterest = new YahooInterest(null);
        assertEquals(0, yahooInterest.getDeclaredInterests().size());
        assertNull(yahooInterest.getInterestCategory());
    }
    
    public void testBadJson() {
        YahooInterest yahooInterest = new YahooInterest(JsonHelper.getFirstNode(BAD_JSON));
        assertEquals(0, yahooInterest.getDeclaredInterests().size());
        assertNull(yahooInterest.getInterestCategory());
    }
    
    public void testGoodJson() {
        YahooInterest yahooInterest = new YahooInterest(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(DECLARED_INTERESTS, yahooInterest.getDeclaredInterests().get(0));
        assertEquals(INTEREST_CATEGORY, yahooInterest.getInterestCategory());
    }
}
