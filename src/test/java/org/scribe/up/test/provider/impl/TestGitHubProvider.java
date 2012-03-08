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

import java.util.Date;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.github.GitHubPlan;
import org.scribe.up.profile.github.GitHubProfile;
import org.scribe.up.provider.impl.GitHubProvider;
import org.scribe.up.test.util.SingleUserSession;
import org.scribe.up.test.util.WebHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
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
public final class TestGitHubProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestGitHubProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        GitHubProvider githubProvider = new GitHubProvider();
        githubProvider.setKey("62374f5573a89a8f9900");
        githubProvider.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        githubProvider.setCallbackUrl("http://www.google.com/");
        githubProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = githubProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput login = form.getInputByName("login");
        login.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute("testpwdscribeup1");
        HtmlSubmitInput submit = form.getInputByName("commit");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = githubProvider.getCredential(WebHelper.getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = githubProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        GitHubProfile profile = (GitHubProfile) githubProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", profile);
        assertEquals("1412558", profile.getId());
        assertEquals(20, profile.getAttributes().size());
        assertEquals(0, profile.getDiskUsage());
        assertEquals(0, profile.getTotalPrivateRepoCount());
        assertEquals("67c3844a672979889c1e3abbd8c4eb22", profile.getGravatarId());
        assertEquals("Paris", profile.getLocation());
        assertNull(profile.getPermission());
        assertEquals(0, profile.getPrivateGistCount());
        assertEquals("User", profile.getType());
        assertEquals(0, profile.getFollowingCount());
        assertEquals("ScribeUp", profile.getBlog());
        assertEquals(0, profile.getPublicGistCount());
        assertEquals(0, profile.getCollaborators());
        assertEquals("testscribeup@gmail.com", profile.getEmail());
        assertEquals("Company", profile.getCompany());
        assertEquals("Test", profile.getName());
        assertTrue(profile.getCreatedAt() instanceof Date);
        assertEquals(0, profile.getOwnedPrivateRepoCount());
        assertEquals("testscribeup", profile.getLogin());
        assertEquals(0, profile.getPublicRepoCount());
        assertEquals(0, profile.getFollowersCount());
        GitHubPlan plan = profile.getPlan();
        assertEquals("free", plan.getName());
        assertEquals(0, plan.getCollaborators());
        assertEquals(307200, plan.getSpace());
        assertEquals(0, plan.getPrivateRepos());
    }
}
