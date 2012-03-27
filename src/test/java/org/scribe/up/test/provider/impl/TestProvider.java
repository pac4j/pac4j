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
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.session.UserSession;
import org.scribe.up.test.util.SingleUserSession;
import org.scribe.up.test.util.WebHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class is the generic test case for OAuth provider.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class TestProvider extends TestCase {
    
    protected static final Logger logger = LoggerFactory.getLogger(TestProvider.class);
    
    public void testProvider() throws Exception {
        OAuthProvider provider = getProvider();
        
        SingleUserSession session = new SingleUserSession();
        WebClient webClient = WebHelper.newClient();
        
        HtmlPage authorizationPage = getAuhtorizationPage(webClient, provider, session);
        
        String callbackUrl = getCallbackUrl(authorizationPage);
        
        UserProfile profile = getProfile(provider, session, callbackUrl);
        
        verifyProfile(profile);
    }
    
    protected abstract OAuthProvider getProvider();
    
    protected HtmlPage getAuhtorizationPage(WebClient webClient, OAuthProvider provider, UserSession session)
        throws Exception {
        String authorizationUrl = provider.getAuthorizationUrl(session);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        return loginPage;
    }
    
    protected abstract String getCallbackUrl(HtmlPage authorizationPage) throws Exception;
    
    protected UserProfile getProfile(OAuthProvider provider, UserSession session, String callbackUrl) {
        OAuthCredential credential = provider.getCredential(session, WebHelper.getParametersFromUrl(callbackUrl));
        logger.debug("credential : {}", credential);
        
        Token accessToken = provider.getAccessToken(credential);
        logger.debug("accessToken : {}", accessToken);
        
        UserProfile profile = provider.getUserProfile(accessToken);
        logger.debug("profile : {}", profile);
        return profile;
    }
    
    protected abstract void verifyProfile(UserProfile userProfile);
}
