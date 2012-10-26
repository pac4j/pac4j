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

import java.util.Locale;

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.google.Google2Profile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.Google2Provider;
import org.scribe.up.provider.impl.Google2Provider.Google2Scope;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.Google2Provider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class TestGoogle2Provider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        final Google2Provider google2Provider = new Google2Provider();
        google2Provider.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        google2Provider.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        google2Provider.setCallbackUrl("https://www.google.com/");
        google2Provider.setScope(Google2Scope.EMAIL_AND_PROFILE);
        return google2Provider;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute("testpwdscribeup34");
        final HtmlSubmitInput submit = form.getInputByName("signIn");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final Google2Profile profile = (Google2Profile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(Google2Profile.class.getSimpleName() + UserProfile.SEPARATOR + "113675986756217860428",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), Google2Profile.class));
        assertCommonProfile(userProfile,
                            "testscribeup@gmail.com",
                            "Jérôme",
                            "ScribeUP",
                            "Jérôme ScribeUP",
                            null,
                            Gender.MALE,
                            Locale.ENGLISH,
                            "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                            "https://plus.google.com/113675986756217860428", null);
        assertTrue(profile.isVerifiedEmail());
        assertTrue(profile.isVerifiedEmailDefined());
        assertEquals("0001-03-10", profile.getBirthday().toString());
        assertEquals(11, profile.getAttributes().size());
    }
}
