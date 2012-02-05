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
import org.scribe.up.provider.impl.GoogleProvider;
import org.scribe.up.test.util.FakeServer;
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
 * This class tests the GoogleProvider by simulating a complete authentication.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class TestGoogleProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestGoogleProvider.class);
    
    public void testProvider() throws Exception {
        FakeServer.start();
        
        // init provider
        GoogleProvider googleProvider = new GoogleProvider();
        googleProvider.setKey(PrivateData.get("google.key"));
        googleProvider.setSecret(PrivateData.get("google.secret"));
        googleProvider.setCallbackUrl(PrivateData.get("callbackUrl"));
        googleProvider.setName("testGoogle");
        googleProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = googleProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute(PrivateData.get("google.login"));
        HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute(PrivateData.get("google.password"));
        HtmlSubmitInput submit = form.getInputByName("signIn");
        HtmlPage confirmPage = submit.click();
        form = confirmPage.getForms().get(0);
        submit = form.getInputByName("allow");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = googleProvider.extractCredentialFromParameters(WebHelper
            .extractParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = googleProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        UserProfile userProfile = googleProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", userProfile);
        assertEquals(PrivateData.get("google.id"), userProfile.getId());
        assertEquals(PrivateData.get("google.attributeValue1"),
                     userProfile.getAttributes().get(PrivateData.get("google.attributeName1")));
        assertEquals(PrivateData.get("google.nbAttributes"), "" + userProfile.getAttributes().size());
    }
}
