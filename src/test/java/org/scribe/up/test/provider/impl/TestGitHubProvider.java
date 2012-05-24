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

import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.github.GitHubPlan;
import org.scribe.up.profile.github.GitHubProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.GitHubProvider;
import org.scribe.up.test.util.CommonHelper;

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
        GitHubProvider githubProvider = new GitHubProvider();
        githubProvider.setKey("62374f5573a89a8f9900");
        githubProvider.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        githubProvider.setCallbackUrl("http://www.google.com/");
        githubProvider.init();
        return githubProvider;
    }
    
    @Override
    protected String getCallbackUrl(HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getForms().get(0);
        HtmlTextInput login = form.getInputByName("login");
        login.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdscribeup1");
        HtmlSubmitInput submit = form.getInputByName("commit");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(UserProfile userProfile) {
        GitHubProfile profile = (GitHubProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("1412558", profile.getId());
        assertEquals(GitHubProfile.class.getSimpleName() + UserProfile.SEPARATOR + "1412558", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GitHubProfile.class));
        assertEquals("Company", profile.getCompany());
        assertEquals("Test", profile.getName());
        assertEquals(0, profile.getFollowingCount());
        assertEquals("ScribeUp", profile.getBlog());
        assertEquals(0, profile.getPublicRepoCount());
        assertEquals(0, profile.getPublicGistCount());
        assertEquals(0, profile.getDiskUsage());
        assertEquals(0, profile.getCollaborators());
        GitHubPlan plan = profile.getPlan();
        assertEquals("free", plan.getName());
        assertEquals(0, plan.getCollaborators());
        assertEquals(307200, plan.getSpace());
        assertEquals(0, plan.getPrivateRepos());
        assertEquals(0, profile.getOwnedPrivateRepoCount());
        assertEquals(0, profile.getTotalPrivateRepoCount());
        assertEquals(0, profile.getPrivateGistCount());
        assertEquals("testscribeup", profile.getLogin());
        assertEquals(0, profile.getFollowersCount());
        assertEquals(CommonHelper.getFormattedDate(1328533521000L, "yyyy/MM/dd HH:mm:ss z", null), profile
            .getCreatedAt().toString());
        assertEquals("testscribeup@gmail.com", profile.getEmail());
        assertEquals("Paris", profile.getLocation());
        assertEquals("User", profile.getType());
        assertNull(profile.getPermission());
        assertEquals("67c3844a672979889c1e3abbd8c4eb22", profile.getGravatarId());
        assertEquals(20, profile.getAttributes().size());
    }
}
