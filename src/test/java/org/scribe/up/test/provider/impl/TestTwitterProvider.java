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
import org.scribe.up.provider.impl.TwitterProvider;
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
 * This class tests the TwitterProvider by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestTwitterProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestTwitterProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        TwitterProvider twitterProvider = new TwitterProvider();
        twitterProvider.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterProvider.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterProvider.setCallbackUrl("http://www.google.com/");
        twitterProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = twitterProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput sessionUsernameOrEmail = form.getInputByName("session[username_or_email]");
        sessionUsernameOrEmail.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput sessionPassword = form.getInputByName("session[password]");
        sessionPassword.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getElementById("allow");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = twitterProvider.getCredential(WebHelper.getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = twitterProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        UserProfile userProfile = twitterProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", userProfile);
        assertEquals("488358057", userProfile.getId());
        assertEquals("test scribeUP", userProfile.getAttributes().get("name"));
        assertEquals(36, userProfile.getAttributes().size());
    }
}
