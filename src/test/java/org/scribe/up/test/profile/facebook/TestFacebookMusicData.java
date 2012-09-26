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
package org.scribe.up.test.profile.facebook;

import junit.framework.TestCase;

import org.scribe.up.profile.facebook.FacebookMusicData;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookMusicData} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookMusicData extends TestCase {
    
    private static final String ID = "1456";
    
    private static final String URL = "http://url";
    
    private static final String TYPE = "type";
    
    private static final String TITLE = "title";
    
    private static final String GOOD_JSON = "{\"id\" : \"" + ID + "\", \"url\" : \"" + URL + "\", \"type\" : \"" + TYPE
                                            + "\", \"title\" : \"" + TITLE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        FacebookMusicData facebookMusicData = new FacebookMusicData(null);
        assertNull(facebookMusicData.getId());
        assertNull(facebookMusicData.getUrl());
        assertNull(facebookMusicData.getType());
        assertNull(facebookMusicData.getTitle());
    }
    
    public void testBadJson() {
        FacebookMusicData facebookMusicData = new FacebookMusicData(BAD_JSON);
        assertNull(facebookMusicData.getId());
        assertNull(facebookMusicData.getUrl());
        assertNull(facebookMusicData.getType());
        assertNull(facebookMusicData.getTitle());
    }
    
    public void testGoodJson() {
        FacebookMusicData facebookMusicData = new FacebookMusicData(GOOD_JSON);
        assertEquals(ID, facebookMusicData.getId());
        assertEquals(URL, facebookMusicData.getUrl());
        assertEquals(TYPE, facebookMusicData.getType());
        assertEquals(TITLE, facebookMusicData.getTitle());
    }
}
