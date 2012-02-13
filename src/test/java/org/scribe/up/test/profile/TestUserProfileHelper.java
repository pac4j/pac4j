/*
  Copyright 2012 Jérôme Leleu

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

import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;

/**
 * This class tests the UserProfileHelper class.
 * 
 * @author Jérôme Leleu
 */
public class TestUserProfileHelper extends TestCase {
    
    private UserProfileHelper profileHelper = new UserProfileHelper();
    
    private static final String BEGIN = "begin";
    
    private static final String END = "end";
    
    private static final String ID = "id";
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    public void testSubstringNoBeginNoEnd() {
        String s = "564564654786431231848684534156446316448645";
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoEnd() {
        String s = "564564654" + BEGIN + "786431231848684534156446316448645";
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringNoBegin() {
        String s = "564564654786431231848684534" + END + "156446316448645";
        assertEquals(null, profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testSubstringOk() {
        String s = "5645646547" + BEGIN + "86431231848684534" + END + "156446316448645";
        assertEquals("86431231848684534", profileHelper.substringBetween(s, BEGIN, END));
    }
    
    public void testAddIdentifier() {
        UserProfile userProfile = new UserProfile();
        assertEquals(null, userProfile.getId());
        profileHelper.addIdentifier(userProfile, ID);
        assertEquals(ID, userProfile.getId());
    }
    
    public void testAddAttribute() {
        UserProfile userProfile = new UserProfile();
        assertEquals(null, userProfile.getAttributes().get(KEY));
        profileHelper.addAttribute(userProfile, KEY, VALUE, null);
        assertEquals(VALUE, userProfile.getAttributes().get(KEY));
    }
}
