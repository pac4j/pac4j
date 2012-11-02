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
import org.scribe.up.profile.dropbox.DropBoxProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.DropBoxProvider;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link org.scribe.up.provider.impl.DropBoxProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class TestDropBoxProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        final DropBoxProvider dropBoxProvider = new DropBoxProvider();
        dropBoxProvider.setKey("0194c6m79qll0ia");
        dropBoxProvider.setSecret("a0ylze9a0bhsvxv");
        dropBoxProvider.setCallbackUrl("http://www.google.com/");
        return dropBoxProvider;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getForms().get(1);
        final HtmlTextInput login = form.getInputByName("login_email");
        login.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("login_password");
        passwd.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("login_submit_dummy");
        final HtmlPage confirmPage = submit.click();
        form = confirmPage.getForms().get(1);
        submit = form.getInputByName("allow_access");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final DropBoxProfile profile = (DropBoxProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("75206624", profile.getId());
        assertEquals(DropBoxProfile.class.getSimpleName() + UserProfile.SEPARATOR + "75206624", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), DropBoxProfile.class));
        assertCommonProfile(userProfile, null, null, null, "Test ScribeUP", null, Gender.UNSPECIFIED, Locale.FRENCH,
                            null, "https://www.dropbox.com/referrals/NTc1MjA2NjI0OQ", null);
        assertEquals(0L, profile.getShared());
        assertEquals(1410412L, profile.getNormal());
        assertEquals(2147483648L, profile.getQuota());
        assertEquals(7, profile.getAttributes().size());
    }
}
