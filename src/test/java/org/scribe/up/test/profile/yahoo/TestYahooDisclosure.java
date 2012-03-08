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
import org.scribe.up.profile.yahoo.YahooDisclosure;

/**
 * This class tests the {@link org.scribe.up.profile.yahoo.YahooDisclosure} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooDisclosure extends TestCase {
    
    private final static String ACCEPTANCE = "acceptance";
    
    private final static String NAME = "name";
    
    private final static String SEEN = "2012-03-06T12:35:20Z";
    
    private final static String VERSION = "version";
    
    private static final String GOOD_JSON = "{\"acceptance\" : \"" + ACCEPTANCE + "\", \"name\" : \"" + NAME
                                            + "\", \"seen\" : \"" + SEEN + "\", \"version\" : \"" + VERSION + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        YahooDisclosure yahooDisclosure = new YahooDisclosure(null);
        assertNull(yahooDisclosure.getAcceptance());
        assertNull(yahooDisclosure.getName());
        assertNull(yahooDisclosure.getSeen());
        assertNull(yahooDisclosure.getVersion());
    }
    
    public void testBadJson() {
        YahooDisclosure yahooDisclosure = new YahooDisclosure(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooDisclosure.getAcceptance());
        assertNull(yahooDisclosure.getName());
        assertNull(yahooDisclosure.getSeen());
        assertNull(yahooDisclosure.getVersion());
    }
    
    public void testGoodJson() {
        YahooDisclosure yahooDisclosure = new YahooDisclosure(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(ACCEPTANCE, yahooDisclosure.getAcceptance());
        assertEquals(NAME, yahooDisclosure.getName());
        assertNotNull(yahooDisclosure.getSeen());
        assertEquals(VERSION, yahooDisclosure.getVersion());
    }
}
