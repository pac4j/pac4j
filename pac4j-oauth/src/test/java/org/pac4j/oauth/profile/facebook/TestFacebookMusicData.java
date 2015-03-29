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
package org.pac4j.oauth.profile.facebook;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link FacebookMusicData} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookMusicData extends TestCase implements TestsConstants {
    
    private static final String GOOD_JSON = "{\"id\" : \"" + STRING_ID + "\", \"url\" : \"" + CALLBACK_URL
                                            + "\", \"type\" : \"" + TYPE + "\", \"title\" : \"" + TITLE + "\"}";
    
    public void testNull() {
        final FacebookMusicData facebookMusicData = new FacebookMusicData();
        facebookMusicData.buildFrom(null);
        assertNull(facebookMusicData.getId());
        assertNull(facebookMusicData.getUrl());
        assertNull(facebookMusicData.getType());
        assertNull(facebookMusicData.getTitle());
    }
    
    public void testBadJson() {
        final FacebookMusicData facebookMusicData = new FacebookMusicData();
        facebookMusicData.buildFrom(BAD_JSON);
        assertNull(facebookMusicData.getId());
        assertNull(facebookMusicData.getUrl());
        assertNull(facebookMusicData.getType());
        assertNull(facebookMusicData.getTitle());
    }
    
    public void testGoodJson() {
        final FacebookMusicData facebookMusicData = new FacebookMusicData();
        facebookMusicData.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookMusicData.getId());
        assertEquals(CALLBACK_URL, facebookMusicData.getUrl());
        assertEquals(TYPE, facebookMusicData.getType());
        assertEquals(TITLE, facebookMusicData.getTitle());
    }
}
