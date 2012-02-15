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

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.GitHubProvider;
import org.scribe.up.test.util.PrivateData;
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
 * This class tests the GitHubProvider by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestGitHubProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestGitHubProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        GitHubProvider githubProvider = new GitHubProvider();
        githubProvider.setKey(PrivateData.get("github.key"));
        githubProvider.setSecret(PrivateData.get("github.secret"));
        githubProvider.setCallbackUrl(PrivateData.get("callbackUrl"));
        githubProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = githubProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput login = form.getInputByName("login");
        login.setValueAttribute(PrivateData.get("github.login"));
        HtmlPasswordInput password = form.getInputByName("password");
        password.setValueAttribute(PrivateData.get("github.password"));
        HtmlSubmitInput submit = form.getInputByName("commit");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = githubProvider.getCredentialFromParameters(WebHelper
            .getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = githubProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        UserProfile userProfile = githubProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", userProfile);
        assertEquals(PrivateData.get("github.id"), userProfile.getId());
        assertEquals(PrivateData.get("github.attributeValue1"),
                     userProfile.getAttributes().get(PrivateData.get("github.attributeName1")));
        assertEquals(PrivateData.get("github.nbAttributes"), "" + userProfile.getAttributes().size());
    }
}
