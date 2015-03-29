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
 * This class tests the {@link FacebookMusicListen} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookMusicListen extends TestCase implements TestsConstants {
    
    private static final String DATE = "2012-09-25T12:52:07+0000";
    
    private static final boolean NO_FEED_STORY = true;
    
    private static final String GOOD_JSON = "{\"id\": \"" + STRING_ID + "\", \"from\": { \"name\": \"" + NAME
                                            + "\", \"id\": \"" + STRING_ID + "\" }, \"start_time\": \"" + DATE
                                            + "\", \"end_time\": \"" + DATE + "\", \"publish_time\": \"" + DATE
                                            + "\", \"application\": { \"name\": \"" + NAME + "\", \"namespace\": \""
                                            + NAMESPACE + "\", \"id\": \"" + STRING_ID
                                            + "\" }, \"data\": { \"song\": { \"id\": \"" + STRING_ID + "\", \"url\": \""
                                            + CALLBACK_URL + "\", \"type\": \"" + TYPE + "\",  \"title\": \"" + TITLE
                                            + "\" }, \"musician\": { \"id\": \"" + STRING_ID + "\", \"url\": \""
                                            + CALLBACK_URL + "\", \"type\": \"" + TYPE + "\", \"title\": \"" + TITLE
                                            + "\" } , \"radio_station\": { \"id\": \"" + STRING_ID + "\", \"url\": \""
                                            + CALLBACK_URL + "\", \"type\": \"" + TYPE + "\", \"title\": \"" + TITLE
                                            + "\" } }, \"type\": \"" + TYPE + "\", \"no_feed_story\": " + NO_FEED_STORY
                                            + " }";
    
    public void testNull() {
        final FacebookMusicListen facebookMusicListen = new FacebookMusicListen();
        facebookMusicListen.buildFrom(null);
        assertNull(facebookMusicListen.getId());
        assertNull(facebookMusicListen.getType());
        assertNull(facebookMusicListen.getApplication());
        assertNull(facebookMusicListen.getEndTime());
        assertNull(facebookMusicListen.getFrom());
        assertNull(facebookMusicListen.getMusician());
        assertNull(facebookMusicListen.getNoFeedStory());
        assertNull(facebookMusicListen.getPublishTime());
        assertNull(facebookMusicListen.getRadioStation());
        assertNull(facebookMusicListen.getSong());
        assertNull(facebookMusicListen.getStartTime());
    }
    
    public void testBadJson() {
        final FacebookMusicListen facebookMusicListen = new FacebookMusicListen();
        facebookMusicListen.buildFrom(BAD_JSON);
        assertNull(facebookMusicListen.getId());
        assertNull(facebookMusicListen.getType());
        assertNull(facebookMusicListen.getApplication());
        assertNull(facebookMusicListen.getEndTime());
        assertNull(facebookMusicListen.getFrom());
        assertNull(facebookMusicListen.getMusician());
        assertNull(facebookMusicListen.getNoFeedStory());
        assertNull(facebookMusicListen.getPublishTime());
        assertNull(facebookMusicListen.getRadioStation());
        assertNull(facebookMusicListen.getSong());
        assertNull(facebookMusicListen.getStartTime());
    }
    
    public void testGoodJson() {
        final FacebookMusicListen facebookMusicListen = new FacebookMusicListen();
        facebookMusicListen.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookMusicListen.getId());
        final FacebookObject from = facebookMusicListen.getFrom();
        assertEquals(STRING_ID, from.getId());
        assertEquals(NAME, from.getName());
        assertNotNull(facebookMusicListen.getStartTime());
        assertNotNull(facebookMusicListen.getEndTime());
        assertNotNull(facebookMusicListen.getPublishTime());
        final FacebookApplication facebookApplication = facebookMusicListen.getApplication();
        assertEquals(STRING_ID, facebookApplication.getId());
        assertEquals(NAME, facebookApplication.getName());
        assertEquals(NAMESPACE, facebookApplication.getNamespace());
        assertData(facebookMusicListen.getSong());
        assertData(facebookMusicListen.getMusician());
        assertData(facebookMusicListen.getRadioStation());
        assertEquals(TYPE, facebookMusicListen.getType());
        assertTrue(facebookMusicListen.getNoFeedStory());
    }
    
    private void assertData(final FacebookMusicData facebookMusicData) {
        assertEquals(STRING_ID, facebookMusicData.getId());
        assertEquals(TITLE, facebookMusicData.getTitle());
        assertEquals(TYPE, facebookMusicData.getType());
        assertEquals(CALLBACK_URL, facebookMusicData.getUrl());
    }
}
