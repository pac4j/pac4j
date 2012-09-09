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

import org.apache.commons.lang3.StringUtils;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.session.UserSession;
import org.scribe.up.test.util.CommonHelper;
import org.scribe.up.test.util.SingleUserSession;
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
    
    protected boolean isJavascriptEnabled() {
        return false;
    }
    
    public void testProvider() throws Exception {
        final OAuthProvider provider = getProvider();
        
        final SingleUserSession session = new SingleUserSession();
        final WebClient webClient = CommonHelper.newWebClient(isJavascriptEnabled());
        
        final HtmlPage authorizationPage = getAuhtorizationPage(webClient, provider, session);
        
        final String callbackUrl = getCallbackUrl(authorizationPage);
        
        final UserProfile profile = getProfile(provider, session, callbackUrl);
        
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        verifyProfile(profile);
        
        final byte[] bytes = CommonHelper.serialize(profile);
        final UserProfile profile2 = (UserProfile) CommonHelper.unserialize(bytes);
        
        verifyProfile(profile2);
    }
    
    protected abstract OAuthProvider getProvider();
    
    protected HtmlPage getAuhtorizationPage(final WebClient webClient, final OAuthProvider provider,
                                            final UserSession session) throws Exception {
        final String authorizationUrl = provider.getAuthorizationUrl(session);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        final HtmlPage loginPage = webClient.getPage(authorizationUrl);
        return loginPage;
    }
    
    protected abstract String getCallbackUrl(HtmlPage authorizationPage) throws Exception;
    
    protected UserProfile getProfile(final OAuthProvider provider, final UserSession session, final String callbackUrl) {
        final OAuthCredential credential = provider.getCredential(session,
                                                                  CommonHelper.getParametersFromUrl(callbackUrl));
        logger.debug("credential : {}", credential);
        
        final UserProfile profile = provider.getUserProfile(credential);
        return profile;
    }
    
    protected abstract void verifyProfile(UserProfile userProfile);
}
