/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.oauth.client;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link DropBoxClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClientIT extends OAuthClientIT {

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final DropBoxClient dropBoxClient = new DropBoxClient();
        dropBoxClient.setKey("0194c6m79qll0ia");
        dropBoxClient.setSecret("a0ylze9a0bhsvxv");
        dropBoxClient.setCallbackUrl(GOOGLE_URL);
        return dropBoxClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        webClient.waitForBackgroundJavaScript(5000);
        HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput login = form.getInputByName("login_email");
        login.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("login_password");
        passwd.setValueAttribute("testpwdscribeup");
        HtmlButton submit = form.getButtonByName("");
        HtmlPage confirmPage = submit.click();
        confirmPage = (HtmlPage) confirmPage.refresh();
        webClient.waitForBackgroundJavaScript(5000);
        form = confirmPage.getForms().get(0);
        HtmlButton submit2 = form.getButtonByName("allow_access");
        final HtmlPage callbackPage = submit2.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(DropBoxProfile.class);
    }

    protected boolean isJavascriptEnabled() {
        return true;
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final DropBoxProfile profile = (DropBoxProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("75206624", profile.getId());
        assertEquals(DropBoxProfile.class.getSimpleName() + UserProfile.SEPARATOR + "75206624", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), DropBoxProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, null, null, "Test ScribeUP", null, Gender.UNSPECIFIED, Locale.FRENCH,
                null, "https://db.tt/RvmZyvJa", null);
        assertEquals(0L, profile.getShared().longValue());
        assertEquals(1410412L, profile.getNormal().longValue());
        assertEquals(2147483648L, profile.getQuota().longValue());
        assertNotNull(profile.getAccessSecret());
        assertEquals(8, profile.getAttributes().size());
    }
}
