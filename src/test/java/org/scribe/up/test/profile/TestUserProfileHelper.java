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
package org.scribe.up.test.profile;

import java.util.List;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.test.util.MockAttributeConverter;

/**
 * This class tests the UserProfileHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class TestUserProfileHelper extends TestCase {
    
    private static final String ID = "id";
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    private static final String GOOD_JSON = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
    
    private static final String GOOD_FACEBOOK_LIST_JSON = "[ {\"id\" : \"" + ID + "\", \"name\" : \"" + VALUE + "\"} ]";
    
    private static final String BAD_JSON = "[ {\"key\" : \"" + ID + "\", \"key2\" : \"" + VALUE + "\"} ]";
    
    private static final String TYPE = "type";
    
    private static final String GOOD_GOOGLE_LIST_JSON = "[ {\"value\" : \"" + VALUE + "\", \"type\" : \"" + TYPE
                                                        + "\"} ]";
    
    public void testAddIdentifier() {
        UserProfile userProfile = new UserProfile();
        assertNull(userProfile.getId());
        UserProfileHelper.addIdentifier(userProfile, ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testAddIdentifierJson() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(GOOD_JSON);
        UserProfileHelper.addIdentifier(userProfile, json, KEY);
        assertEquals(VALUE, userProfile.getId());
    }
    
    public void testAddAttribute() {
        UserProfile userProfile = new UserProfile();
        assertNull(userProfile.getAttributes().get(KEY));
        UserProfileHelper.addAttribute(userProfile, KEY, VALUE);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeConversion() {
        UserProfile userProfile = new UserProfile();
        assertNull(userProfile.getAttributes().get(KEY));
        UserProfileHelper.addAttribute(userProfile, KEY, VALUE, new MockAttributeConverter());
        assertEquals(MockAttributeConverter.CONVERTED_VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeJson() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(GOOD_JSON);
        UserProfileHelper.addAttribute(userProfile, json, KEY);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeJsonConversion() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(GOOD_JSON);
        UserProfileHelper.addAttribute(userProfile, json, KEY, new MockAttributeConverter());
        assertEquals(MockAttributeConverter.CONVERTED_VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testGetListFacebookNull() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(null, FacebookObject.class);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListFacebookBadKeys() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(BAD_JSON), FacebookObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        FacebookObject facebookObject = list.get(0);
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
    }
    
    public void testGetListFacebookOk() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(GOOD_FACEBOOK_LIST_JSON), FacebookObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        FacebookObject facebookObject = list.get(0);
        assertEquals(ID, facebookObject.getId());
        assertEquals(VALUE, facebookObject.getName());
    }
    
    public void testGetListGoogleNull() {
        List<GoogleObject> list = (List<GoogleObject>) UserProfileHelper.getListObject(null, GoogleObject.class);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListGoogleBadKeys() {
        List<GoogleObject> list = (List<GoogleObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(BAD_JSON), GoogleObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        GoogleObject googleObject = list.get(0);
        assertNull(googleObject.getValue());
        assertNull(googleObject.getType());
    }
    
    public void testGetListGoogleOk() {
        List<GoogleObject> list = (List<GoogleObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(GOOD_GOOGLE_LIST_JSON), GoogleObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        GoogleObject googleObject = list.get(0);
        assertEquals(VALUE, googleObject.getValue());
        assertEquals(TYPE, googleObject.getType());
    }
    
}
