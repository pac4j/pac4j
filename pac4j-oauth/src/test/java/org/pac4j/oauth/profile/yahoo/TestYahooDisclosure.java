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
 * This class tests the {@link YahooDisclosure} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooDisclosure extends TestCase implements TestsConstants {
    
    private final static String ACCEPTANCE = "acceptance";
    
    private final static String SEEN = "2012-03-06T12:35:20Z";
    
    private final static String VERSION = "version";
    
    private static final String GOOD_JSON = "{\"acceptance\" : \"" + ACCEPTANCE + "\", \"name\" : \"" + NAME
                                            + "\", \"seen\" : \"" + SEEN + "\", \"version\" : \"" + VERSION + "\"}";
    
    public void testNull() {
        final YahooDisclosure yahooDisclosure = new YahooDisclosure();
        yahooDisclosure.buildFrom(null);
        assertNull(yahooDisclosure.getAcceptance());
        assertNull(yahooDisclosure.getName());
        assertNull(yahooDisclosure.getSeen());
        assertNull(yahooDisclosure.getVersion());
    }
    
    public void testBadJson() {
        final YahooDisclosure yahooDisclosure = new YahooDisclosure();
        yahooDisclosure.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooDisclosure.getAcceptance());
        assertNull(yahooDisclosure.getName());
        assertNull(yahooDisclosure.getSeen());
        assertNull(yahooDisclosure.getVersion());
    }
    
    public void testGoodJson() {
        final YahooDisclosure yahooDisclosure = new YahooDisclosure();
        yahooDisclosure.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(ACCEPTANCE, yahooDisclosure.getAcceptance());
        assertEquals(NAME, yahooDisclosure.getName());
        assertNotNull(yahooDisclosure.getSeen());
        assertEquals(VERSION, yahooDisclosure.getVersion());
    }
}
