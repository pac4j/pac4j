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

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookPhoto;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookPhoto} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookPhoto extends TestCase {
    
    private static final String ID = "12345";
    
    private static final String NAME = "name";
    
    private static final String FROM = "{ \"id:\": \"" + ID + "\", \"name\": \"" + NAME + "\" }";
    
    private static final String LINK = "http://thelink";
    
    private static final String COVER_PHOTO = "67890";
    
    private static final String PRIVACY = "friends";
    
    private static final int COUNT = 1;
    
    private static final String TYPE = "profile";
    
    private static final String CREATED_TIME = "2012-02-23T20:52:30+0000";
    
    private static final String UPDATED_TIME = "2012-02-28T20:52:30+0000";
    
    private static final boolean CAN_UPLOAD = true;
    
    private static final String GOOD_JSON = "{\"id\": \"" + ID + "\", \"from\": " + FROM + ", \"name\": \"" + NAME
                                            + "\", \"link\" : \"" + LINK + "\", \"cover_photo\": \"" + COVER_PHOTO
                                            + "\", \"privacy\": \"" + PRIVACY + "\", \"count\": " + COUNT
                                            + ", \"type\": \"" + TYPE + "\", \"created_time\": \"" + CREATED_TIME
                                            + "\", \"updated_time\": \"" + UPDATED_TIME + "\", \"can_upload\": "
                                            + CAN_UPLOAD + " }";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        final FacebookPhoto facebookPhoto = new FacebookPhoto();
        facebookPhoto.buildFrom(null);
        assertNull(facebookPhoto.getId());
        assertNull(facebookPhoto.getFrom());
        assertNull(facebookPhoto.getName());
        assertNull(facebookPhoto.getLink());
        assertNull(facebookPhoto.getCoverPhoto());
        assertNull(facebookPhoto.getPrivacy());
        assertEquals(0, facebookPhoto.getCount());
        assertFalse(facebookPhoto.isCountDefined());
        assertNull(facebookPhoto.getType());
        assertNull(facebookPhoto.getCreatedTime());
        assertNull(facebookPhoto.getUpdatedTime());
        assertFalse(facebookPhoto.isCanUpload());
        assertFalse(facebookPhoto.isCanUploadDefined());
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
        assertEquals(0, facebookPhoto.getCount());
        assertFalse(facebookPhoto.isCountDefined());
        assertNull(facebookPhoto.getType());
        assertNull(facebookPhoto.getCreatedTime());
        assertNull(facebookPhoto.getUpdatedTime());
        assertFalse(facebookPhoto.isCanUpload());
        assertFalse(facebookPhoto.isCanUploadDefined());
    }
    
    public void testGoodJson() {
        final FacebookPhoto facebookPhoto = new FacebookPhoto();
        facebookPhoto.buildFrom(GOOD_JSON);
        final FacebookObject fromObject = new FacebookObject();
        fromObject.buildFrom(JsonHelper.getFirstNode(FROM));
        assertEquals(ID, facebookPhoto.getId());
        assertEquals(fromObject.toString(), facebookPhoto.getFrom().toString());
        assertEquals(NAME, facebookPhoto.getName());
        assertEquals(LINK, facebookPhoto.getLink());
        assertEquals(COVER_PHOTO, facebookPhoto.getCoverPhoto());
        assertEquals(PRIVACY, facebookPhoto.getPrivacy());
        assertEquals(1, facebookPhoto.getCount());
        assertTrue(facebookPhoto.isCountDefined());
        assertEquals(TYPE, facebookPhoto.getType());
        assertEquals(Converters.dateConverter.convert(CREATED_TIME).toString(), facebookPhoto.getCreatedTime()
            .toString());
        assertEquals(Converters.dateConverter.convert(UPDATED_TIME).toString(), facebookPhoto.getUpdatedTime()
            .toString());
        assertTrue(facebookPhoto.isCanUpload());
        assertTrue(facebookPhoto.isCanUploadDefined());
    }
}
