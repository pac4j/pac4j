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
import org.scribe.up.profile.linkedin.LinkedInProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.LinkedInProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.LinkedInProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestLinkedInProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        LinkedInProvider linkedinProvider = new LinkedInProvider();
        linkedinProvider.setKey("gsqj8dn56ayn");
        linkedinProvider.setSecret("kUFAZ2oYvwMQ6HFl");
        linkedinProvider.setCallbackUrl("http://www.google.com/");
        linkedinProvider.init();
        return linkedinProvider;
    }
    
    @Override
    protected String getCallbackUrl(HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getFormByName("oauthAuthorizeForm");
        HtmlTextInput sessionKey = form.getInputByName("session_key");
        sessionKey.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput sessionPassword = form.getInputByName("session_password");
        sessionPassword.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("authorize");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(UserProfile userProfile) {
        LinkedInProfile profile = (LinkedInProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("167439971", profile.getId());
        assertEquals(LinkedInProfile.class.getSimpleName() + UserProfile.SEPARATOR + "167439971", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedInProfile.class));
        assertEquals("test", profile.getFirstName());
        assertEquals("scribeUp", profile.getLastName());
        assertEquals("ScribeUP développeur chez OpenSource", profile.getHeadline());
        assertTrue(profile
            .getUrl()
            .startsWith("http://www.linkedin.com/profile?viewProfile=&amp;key=167439971&amp;authToken=_IWF&amp;authType=name&amp;trk=api*"));
        assertEquals(4, profile.getAttributes().size());
    }
}
