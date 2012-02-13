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
package org.scribe.up.test.provider;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.FacebookProvider;
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
 * This class tests the FacebookProvider by simulating a complete authentication.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class TestFacebookProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestFacebookProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.setKey(PrivateData.get("facebook.key"));
        facebookProvider.setSecret(PrivateData.get("facebook.secret"));
        facebookProvider.setCallbackUrl(PrivateData.get("callbackUrl"));
        facebookProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = facebookProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput email = form.getInputByName("email");
        email.setValueAttribute(PrivateData.get("facebook.login"));
        HtmlPasswordInput password = form.getInputByName("pass");
        password.setValueAttribute(PrivateData.get("facebook.password"));
        HtmlSubmitInput submit = form.getInputByName("login");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = facebookProvider.extractCredentialFromParameters(WebHelper
            .extractParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = facebookProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        UserProfile userProfile = facebookProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", userProfile);
        assertEquals(PrivateData.get("facebook.id"), userProfile.getId());
        assertEquals(PrivateData.get("facebook.attributeValue1"),
                     userProfile.getAttributes().get(PrivateData.get("facebook.attributeName1")));
        assertEquals(PrivateData.get("facebook.nbAttributes"), "" + userProfile.getAttributes().size());
    }
}
