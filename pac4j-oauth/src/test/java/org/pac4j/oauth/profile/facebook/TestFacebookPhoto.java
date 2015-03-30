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

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class tests the {@link FacebookPhoto} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookPhoto extends TestCase implements TestsConstants {
    
    private static final String FROM = "{ \"id:\": \"" + STRING_ID + "\", \"name\": \"" + NAME + "\" }";
    
    private static final String COVER_PHOTO = "67890";
    
    private static final String PRIVACY = "friends";
    
    private static final int COUNT = 1;
    
    private static final String CREATED_TIME = "2012-02-23T20:52:30+0000";
    
    private static final String UPDATED_TIME = "2012-02-28T20:52:30+0000";
    
    private static final boolean CAN_UPLOAD = true;
    
    private static final String GOOD_JSON = "{\"id\": \"" + STRING_ID + "\", \"from\": " + FROM + ", \"name\": \"" + NAME
                                            + "\", \"link\" : \"" + CALLBACK_URL + "\", \"cover_photo\": \""
                                            + COVER_PHOTO + "\", \"privacy\": \"" + PRIVACY + "\", \"count\": " + COUNT
                                            + ", \"type\": \"" + TYPE + "\", \"created_time\": \"" + CREATED_TIME
                                            + "\", \"updated_time\": \"" + UPDATED_TIME + "\", \"can_upload\": "
                                            + CAN_UPLOAD + " }";
    
    public void testNull() {
        final FacebookPhoto facebookPhoto = new FacebookPhoto();
        facebookPhoto.buildFrom(null);
        assertNull(facebookPhoto.getId());
        assertNull(facebookPhoto.getFrom());
        assertNull(facebookPhoto.getName());
        assertNull(facebookPhoto.getLink());
        assertNull(facebookPhoto.getCoverPhoto());
        assertNull(facebookPhoto.getPrivacy());
        assertNull(facebookPhoto.getCount());
        assertNull(facebookPhoto.getType());
        assertNull(facebookPhoto.getCreatedTime());
        assertNull(facebookPhoto.getUpdatedTime());
        assertNull(facebookPhoto.getCanUpload());
    }
    
    public void testBadJson() {
        final FacebookPhoto facebookPhoto = new FacebookPhoto();
        facebookPhoto.buildFrom(BAD_JSON);
        assertNull(facebookPhoto.getId());
        assertNull(facebookPhoto.getFrom());
        assertNull(facebookPhoto.getName());
        assertNull(facebookPhoto.getLink());
        assertNull(facebookPhoto.getCoverPhoto());
        assertNull(facebookPhoto.getPrivacy());
        assertNull(facebookPhoto.getCount());
        assertNull(facebookPhoto.getType());
        assertNull(facebookPhoto.getCreatedTime());
        assertNull(facebookPhoto.getUpdatedTime());
        assertNull(facebookPhoto.getCanUpload());
    }
    
    public void testGoodJson() {
        final FacebookPhoto facebookPhoto = new FacebookPhoto();
        facebookPhoto.buildFrom(GOOD_JSON);
        final FacebookObject fromObject = new FacebookObject();
        fromObject.buildFrom(JsonHelper.getFirstNode(FROM));
        assertEquals(STRING_ID, facebookPhoto.getId());
        assertEquals(fromObject.toString(), facebookPhoto.getFrom().toString());
        assertEquals(NAME, facebookPhoto.getName());
        assertEquals(CALLBACK_URL, facebookPhoto.getLink());
        assertEquals(COVER_PHOTO, facebookPhoto.getCoverPhoto());
        assertEquals(PRIVACY, facebookPhoto.getPrivacy());
        assertEquals(1, facebookPhoto.getCount().intValue());
        assertEquals(TYPE, facebookPhoto.getType());
        assertEquals(Converters.dateConverter.convert(CREATED_TIME).toString(), facebookPhoto.getCreatedTime()
            .toString());
        assertEquals(Converters.dateConverter.convert(UPDATED_TIME).toString(), facebookPhoto.getUpdatedTime()
            .toString());
        assertTrue(facebookPhoto.getCanUpload());
    }
}
