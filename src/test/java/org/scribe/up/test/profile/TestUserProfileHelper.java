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

import junit.framework.TestCase;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.test.util.MockAttributeConverter;

/**
 * This class tests the UserProfileHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestUserProfileHelper extends TestCase {
    
    private UserProfileHelper profileHelper = new UserProfileHelper();
    
    private static final String PART1 = "5645646547";
    
    private static final String PART2 = "86431231848684534";
    
    private static final String PART3 = "156446316448645";
    
    private static final String BEGIN = "begin";
    
    private static final String END = "end";
    
    private static final String ID = "id";
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    private static final String GOOD_JSON = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
    
    private static final String BAD_JSON = "this_is_definitively_not_a_json_texte";
    
    public void testSubstringNoBeginNoEnd() {
        String s = PART1 + PART2 + PART3;
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoEnd() {
        String s = PART1 + BEGIN + PART2 + PART3;
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoBegin() {
        String s = PART1 + PART2 + END + PART3;
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringOk() {
        String s = PART1 + BEGIN + PART2 + END + PART3;
        assertEquals(PART2, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testAddIdentifier() {
        UserProfile userProfile = new UserProfile();
        assertEquals(null, userProfile.getId());
        profileHelper.addIdentifier(userProfile, ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testAddIdentifierJson() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = profileHelper.getFirstJsonNode(GOOD_JSON);
        profileHelper.addIdentifier(userProfile, json, KEY);
        assertEquals(VALUE, userProfile.getId());
    }
    
    public void testAddAttribute() {
        UserProfile userProfile = new UserProfile();
        assertEquals(null, userProfile.getAttributes().get(KEY));
        profileHelper.addAttribute(userProfile, KEY, VALUE);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeConversion() {
        UserProfile userProfile = new UserProfile();
        assertEquals(null, userProfile.getAttributes().get(KEY));
        profileHelper.addAttribute(userProfile, KEY, VALUE, new MockAttributeConverter());
        assertEquals(MockAttributeConverter.CONVERTED_VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeJson() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = profileHelper.getFirstJsonNode(GOOD_JSON);
        profileHelper.addAttribute(userProfile, json, KEY);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testAddAttributeJsonConversion() {
        UserProfile userProfile = new UserProfile();
        JsonNode json = profileHelper.getFirstJsonNode(GOOD_JSON);
        profileHelper.addAttribute(userProfile, json, KEY, new MockAttributeConverter());
        assertEquals(MockAttributeConverter.CONVERTED_VALUE, userProfile.getAttributes().get(KEY));
    }
    
    public void testGetFirstJsonNodeOk() {
        assertNotNull(profileHelper.getFirstJsonNode(GOOD_JSON));
    }
    
    public void testGetFirstJsonNodeKo() {
        assertNull(profileHelper.getFirstJsonNode(BAD_JSON));
    }
}
