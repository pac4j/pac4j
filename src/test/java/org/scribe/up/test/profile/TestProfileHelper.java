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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.profile.yahoo.YahooProfile;

/**
 * This class tests the {@link org.scribe.up.profile.ProfileHelper} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public final class TestProfileHelper extends TestCase {
    
    private static final String TYPED_ID = "FacebookProfile#1234";
    
    private static final String NAME = "name";
    
    private static final String VALUE = "value";
    
    private static final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();
    
    public void testIsTypedIdOfNull() {
        assertFalse(ProfileHelper.isTypedIdOf(null, null));
    }
    
    public void testIsTypedIdOfNullId() {
        assertFalse(ProfileHelper.isTypedIdOf(null, FacebookProfile.class));
    }
    
    public void testIsTypedIdOfNullClass() {
        assertFalse(ProfileHelper.isTypedIdOf(TYPED_ID, null));
    }
    
    public void testIsTypedIdOfWrongClass() {
        assertFalse(ProfileHelper.isTypedIdOf(TYPED_ID, YahooProfile.class));
    }
    
    public void testIsTypedIdOfRightClass() {
        assertTrue(ProfileHelper.isTypedIdOf(TYPED_ID, FacebookProfile.class));
    }
    
    public void testBuildProfileNullId() {
        assertNull(ProfileHelper.buildProfile(null, null));
    }
    
    public void testBuildProfileWrongFormat() {
        assertNull(ProfileHelper.buildProfile("FacebookProfile*1234", null));
    }
    
    public void testBuildProfileWrongProfileClass() {
        assertNull(ProfileHelper.buildProfile("Facebook2Profile#1234", null));
    }
    
    public void testBuildProfileDropBoxProfile() {
        assertNotNull(ProfileHelper.buildProfile("DropBoxProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileFacebookProfile() {
        assertNotNull(ProfileHelper.buildProfile("FacebookProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileGitHubProfile() {
        assertNotNull(ProfileHelper.buildProfile("GitHubProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileGoogleProfile() {
        assertNotNull(ProfileHelper.buildProfile("GoogleProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileGoogle2Profile() {
        assertNotNull(ProfileHelper.buildProfile("Google2Profile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileLinkedInProfile() {
        assertNotNull(ProfileHelper.buildProfile("LinkedInProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileTwitterProfile() {
        assertNotNull(ProfileHelper.buildProfile("TwitterProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileWindowsLiveProfile() {
        assertNotNull(ProfileHelper.buildProfile("WindowsLiveProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileWordPressProfile() {
        assertNotNull(ProfileHelper.buildProfile("WordPressProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileYahooProfile() {
        assertNotNull(ProfileHelper.buildProfile("YahooProfile#1234", EMPTY_MAP));
    }
    
    public void testBuildProfileOK() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(NAME, VALUE);
        UserProfile userProfile = ProfileHelper.buildProfile(TYPED_ID, attributes);
        assertNotNull(userProfile);
        assertEquals("1234", userProfile.getId());
        assertEquals(TYPED_ID, userProfile.getTypedId());
        Map<String, Object> attributesProfile = userProfile.getAttributes();
        assertEquals(1, attributesProfile.size());
        assertEquals(VALUE, attributesProfile.get(NAME));
    }
}
