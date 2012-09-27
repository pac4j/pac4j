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
import org.scribe.up.profile.yahoo.YahooImage;

/**
 * This class tests the {@link org.scribe.up.profile.yahoo.YahooImage} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestYahooImage extends TestCase {
    
    private final static String IMAGE_URL = "imageUrl";
    
    private final static int WIDTH = 150;
    
    private final static int HEIGHT = 200;
    
    private final static String SIZE = "150x200";
    
    private static final String GOOD_JSON = "{\"imageUrl\" : \"" + IMAGE_URL + "\", \"width\" : " + WIDTH
                                            + ", \"height\" : " + HEIGHT + ", \"size\" : \"" + SIZE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(null);
        assertNull(yahooImage.getImageUrl());
        assertEquals(0, yahooImage.getWidth());
        assertEquals(0, yahooImage.getHeight());
        assertNull(yahooImage.getSize());
    }
    
    public void testBadJson() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(yahooImage.getImageUrl());
        assertEquals(0, yahooImage.getWidth());
        assertEquals(0, yahooImage.getHeight());
        assertNull(yahooImage.getSize());
    }
    
    public void testGoodJson() {
        final YahooImage yahooImage = new YahooImage();
        yahooImage.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(IMAGE_URL, yahooImage.getImageUrl());
        assertEquals(WIDTH, yahooImage.getWidth());
        assertEquals(HEIGHT, yahooImage.getHeight());
        assertEquals(SIZE, yahooImage.getSize());
    }
}
