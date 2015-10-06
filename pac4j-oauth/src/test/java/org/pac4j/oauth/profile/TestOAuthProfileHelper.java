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
package org.pac4j.oauth.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.TestCaseProfileHelper;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

/**
 * This class tests the {@link ProfileHelper} class for all the OAuth profiles.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public final class TestOAuthProfileHelper extends TestCaseProfileHelper {
    
    public void testBuildProfileCasOAuthWrapperProfile() {
        assertNotNull(ProfileHelper.buildProfile("CasOAuthWrapperProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileDropBoxProfile() {
        assertNotNull(ProfileHelper.buildProfile("DropBoxProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileFacebookProfile() {
        assertNotNull(ProfileHelper.buildProfile("FacebookProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileGitHubProfile() {
        assertNotNull(ProfileHelper.buildProfile("GitHubProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileGoogle2Profile() {
        assertNotNull(ProfileHelper.buildProfile("Google2Profile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileTwitterProfile() {
        assertNotNull(ProfileHelper.buildProfile("TwitterProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileWindowsLiveProfile() {
        assertNotNull(ProfileHelper.buildProfile("WindowsLiveProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileWordPressProfile() {
        assertNotNull(ProfileHelper.buildProfile("WordPressProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    public void testBuildProfileYahooProfile() {
        assertNotNull(ProfileHelper.buildProfile("YahooProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return FacebookProfile.class;
    }
    
    @Override
    protected String getProfileType() {
        return "FacebookProfile";
    }
    
    @Override
    protected String getAttributeName() {
        return "name";
    }
}
