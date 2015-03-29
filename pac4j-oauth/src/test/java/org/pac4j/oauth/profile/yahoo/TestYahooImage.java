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
 * This class tests the {@link YahooImage} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooImage extends TestCase implements TestsConstants {
    
    private final static int WIDTH = 150;
    
    private final static int HEIGHT = 200;
    
    private final static String SIZE = "150x200";
    
    private static final String GOOD_JSON = "{\"imageUrl\" : \"" + CALLBACK_URL + "\", \"width\" : " + WIDTH
                                            + ", \"height\" : " + HEIGHT + ", \"size\" : \"" + SIZE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(null);
        assertNull(yahooImage.getImageUrl());
        assertNull(yahooImage.getWidth());
        assertNull(yahooImage.getHeight());
        assertNull(yahooImage.getSize());
    }
    
    public void testBadJson() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooImage.getImageUrl());
        assertNull(yahooImage.getWidth());
        assertNull(yahooImage.getHeight());
        assertNull(yahooImage.getSize());
    }
    
    public void testGoodJson() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(CALLBACK_URL, yahooImage.getImageUrl());
        assertEquals(WIDTH, yahooImage.getWidth().intValue());
        assertEquals(HEIGHT, yahooImage.getHeight().intValue());
        assertEquals(SIZE, yahooImage.getSize());
    }
}
