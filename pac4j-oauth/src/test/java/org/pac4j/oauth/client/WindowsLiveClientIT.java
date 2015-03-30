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
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link WindowsLiveClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveClientIT extends OAuthClientIT {
    
    @Override
    protected boolean isJavascriptEnabled() {
        return true;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final WindowsLiveClient liveClient = new WindowsLiveClient();
        liveClient.setKey("00000000400BFE75");
        liveClient.setSecret("9yz0WtTIUQVV7HhBV2tccTziETOt4pRG");
        liveClient.setCallbackUrl(PAC4J_URL);
        return liveClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlTextInput login = authorizationPage.getElementByName("login");
        login.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput password = authorizationPage.getElementByName("passwd");
        password.setValueAttribute("testpwdscribe12");
        final HtmlSubmitInput submit = authorizationPage.getElementByName("SI");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(WindowsLiveProfile.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final WindowsLiveProfile profile = (WindowsLiveProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("416c383b220392d8", profile.getId());
        assertEquals(WindowsLiveProfile.class.getSimpleName() + UserProfile.SEPARATOR + "416c383b220392d8",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WindowsLiveProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "Test", "ScribeUP", "Test ScribeUP", null, Gender.UNSPECIFIED,
                            Locale.FRANCE, null, "https://profile.live.com/", null);
        assertNotNull(profile.getUpdatedTime());
        assertEquals(7, profile.getAttributes().size());
    }
}
