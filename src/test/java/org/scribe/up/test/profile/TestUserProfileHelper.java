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
import org.scribe.up.profile.facebook.FacebookEducation;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookWork;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.profile.yahoo.YahooAddress;
import org.scribe.up.profile.yahoo.YahooDisclosure;
import org.scribe.up.profile.yahoo.YahooEmail;
import org.scribe.up.profile.yahoo.YahooInterest;

/**
 * This class tests the {@link org.scribe.up.profile.UserProfileHelper} class.
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
    
    private static final String GOOD_FACEBOOK_OBJECT_LIST_JSON = "[ {\"id\" : \"" + ID + "\", \"name\" : \"" + VALUE
                                                                 + "\"} ]";
    
    private static final String EMPTY_JSON = "[ { } ]";
    
    private static final String TYPE = "type";
    
    private static final String GOOD_GOOGLE_OBJECT_LIST_JSON = "[ {\"value\" : \"" + VALUE + "\", \"type\" : \"" + TYPE
                                                               + "\"} ]";
    
    private static final String GOOD_STRING_LIST_JSON = "[ \"" + VALUE + "\" ]";
    
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
    
    public void testGetListNull() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(null, FacebookObject.class);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListEmptyJsonFacebookObject() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), FacebookObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof FacebookObject);
    }
    
    public void testGetListEmptyJsonGoogleObject() {
        List<GoogleObject> list = (List<GoogleObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), GoogleObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof GoogleObject);
    }
    
    public void testGetListEmptyJsonFacebookEducation() {
        List<FacebookEducation> list = (List<FacebookEducation>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), FacebookEducation.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof FacebookEducation);
    }
    
    public void testGetListEmptyJsonFacebookWork() {
        List<FacebookWork> list = (List<FacebookWork>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), FacebookWork.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof FacebookWork);
    }
    
    public void testGetListEmptyJsonYahooAddress() {
        List<YahooAddress> list = (List<YahooAddress>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), YahooAddress.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof YahooAddress);
    }
    
    public void testGetListEmptyJsonYahooDisclosure() {
        List<YahooDisclosure> list = (List<YahooDisclosure>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), YahooDisclosure.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof YahooDisclosure);
    }
    
    public void testGetListEmptyJsonYahooEmail() {
        List<YahooEmail> list = (List<YahooEmail>) UserProfileHelper.getListObject(JsonHelper.getFirstNode(EMPTY_JSON),
                                                                                   YahooEmail.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof YahooEmail);
    }
    
    public void testGetListEmptyJsonYahooInterest() {
        List<YahooInterest> list = (List<YahooInterest>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(EMPTY_JSON), YahooInterest.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertTrue(list.get(0) instanceof YahooInterest);
    }
    
    public void testGetListEmptyJsonBadType() {
        List<Boolean> list = (List<Boolean>) UserProfileHelper.getListObject(JsonHelper.getFirstNode(EMPTY_JSON),
                                                                             Boolean.class);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListFacebookObjectOk() {
        List<FacebookObject> list = (List<FacebookObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(GOOD_FACEBOOK_OBJECT_LIST_JSON), FacebookObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        FacebookObject facebookObject = list.get(0);
        assertEquals(ID, facebookObject.getId());
        assertEquals(VALUE, facebookObject.getName());
    }
    
    public void testGetListGoogleObjectOk() {
        List<GoogleObject> list = (List<GoogleObject>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(GOOD_GOOGLE_OBJECT_LIST_JSON), GoogleObject.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        GoogleObject googleObject = list.get(0);
        assertEquals(VALUE, googleObject.getValue());
        assertEquals(TYPE, googleObject.getType());
    }
    
    public void testGetListStringOk() {
        List<String> list = (List<String>) UserProfileHelper.getListObject(JsonHelper
            .getFirstNode(GOOD_STRING_LIST_JSON), String.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(VALUE, list.get(0));
    }
}
