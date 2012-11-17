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
package org.scribe.up.test.provider.impl;

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.github.GitHubPlan;
import org.scribe.up.profile.github.GitHubProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.GitHubProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.GitHubProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestGitHubProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        final GitHubProvider githubProvider = new GitHubProvider();
        githubProvider.setKey("62374f5573a89a8f9900");
        githubProvider.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        githubProvider.setCallbackUrl("http://www.google.com/");
        return githubProvider;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput login = form.getInputByName("login");
        login.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdscribeup1");
        final HtmlSubmitInput submit = form.getInputByName("commit");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final GitHubProfile profile = (GitHubProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("1412558", profile.getId());
        assertEquals(GitHubProfile.class.getSimpleName() + UserProfile.SEPARATOR + "1412558", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GitHubProfile.class));
        assertCommonProfile(userProfile,
                            "testscribeup@gmail.com",
                            null,
                            null,
                            "Test",
                            "testscribeup",
                            Gender.UNSPECIFIED,
                            null,
                            "https://secure.gravatar.com/avatar/67c3844a672979889c1e3abbd8c4eb22?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars",
                            "https://github.com/testscribeup", "Paris");
        assertEquals("User", profile.getType());
        assertEquals("ScribeUp", profile.getBlog());
        assertEquals("https://api.github.com/users/testscribeup", profile.getUrl());
        assertEquals(0, profile.getPublicGists().intValue());
        assertEquals(0, profile.getFollowing().intValue());
        assertEquals(0, profile.getPrivateGists().intValue());
        assertEquals(0, profile.getPublicRepos().intValue());
        assertEquals("67c3844a672979889c1e3abbd8c4eb22", profile.getGravatarId());
        assertEquals(0, profile.getFollowers().intValue());
        assertEquals("Company", profile.getCompany());
        assertFalse(profile.getHireable());
        assertEquals(0, profile.getCollaborators().intValue());
        assertEquals("Java developper", profile.getBio());
        assertEquals(0, profile.getTotalPrivateRepos().intValue());
        assertEquals("2012-02-06T13:05:21Z", profile.getCreatedAt().toString());
        assertEquals(0, profile.getDiskUsage().intValue());
        assertEquals(0, profile.getOwnedPrivateRepos().intValue());
        final GitHubPlan plan = profile.getPlan();
        assertEquals("free", plan.getName());
        assertEquals(0, plan.getCollaborators().intValue());
        assertEquals(307200, plan.getSpace().intValue());
        assertEquals(0, plan.getPrivateRepos().intValue());
        assertEquals(25, profile.getAttributes().size());
    }
}
