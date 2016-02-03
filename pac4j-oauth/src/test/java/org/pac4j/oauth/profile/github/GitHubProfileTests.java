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
package org.pac4j.oauth.profile.github;

import org.junit.Test;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * General test cases for GitHubProfile.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class GitHubProfileTests implements TestsConstants {

    @Test
    public void testClearGitHubProfile() {
        GitHubProfile profile = new GitHubProfile();
        profile.setAccessToken("testToken");
        profile.clearSensitiveData();
        assertNull(profile.getAccessToken());
    }

    @Test
    public void testBuildProfileOldTypedId() {
        final GitHubProfile profile = new GitHubProfile();
        profile.setId(ID);
        final GitHubProfile profile2 = (GitHubProfile) ProfileHelper.buildProfile(profile.getOldTypedId(), profile.getAttributes());
        assertEquals(ID, profile2.getId());
        final GitHubProfile profile3 = (GitHubProfile) ProfileHelper.buildProfile(profile.getOldTypedId(), profile.getAttributes());
        assertEquals(ID, profile3.getId());
    }

    @Test
    public void testBuildProfileTypedId() {
        final GitHubProfile profile = new GitHubProfile();
        profile.setId(ID);
        profile.addAttribute(NAME, VALUE);
        final GitHubProfile profile2 = (GitHubProfile) ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile2.getId());
        final Map<String, Object> attributes = profile2.getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(VALUE, attributes.get(NAME));
        final GitHubProfile profile3 = (GitHubProfile) ProfileHelper.buildProfile(profile.getTypedId(), profile.getAttributes());
        assertEquals(ID, profile3.getId());
    }
}
