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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.linkedin.LinkedInProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link LinkedInClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class LinkedInClientIT extends OAuthClientIT {
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final LinkedInClient linkedInClient = new LinkedInClient();
        linkedInClient.setKey("gsqj8dn56ayn");
        linkedInClient.setSecret("kUFAZ2oYvwMQ6HFl");
        linkedInClient.setCallbackUrl(PAC4J_URL);
        return linkedInClient;
    }
    
    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getFormByName("oauthAuthorizeForm");
        final HtmlTextInput sessionKey = form.getInputByName("session_key");
        sessionKey.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput sessionPassword = form.getInputByName("session_password");
        sessionPassword.setValueAttribute("testpwdscribeup56");
        final HtmlSubmitInput submit = form.getInputByName("authorize");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(LinkedInProfile.class);
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final LinkedInProfile profile = (LinkedInProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("167439971", profile.getId());
        assertEquals(LinkedInProfile.class.getSimpleName() + UserProfile.SEPARATOR + "167439971", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedInProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                            null,
                            "test",
                            "scribeUp",
                            "test scribeUp",
                            null,
                            Gender.UNSPECIFIED,
                            null,
                            null,
                            "https://www.linkedin.com/profile/view?id=167439971&amp;authType=name&amp;authToken=_IWF&amp;trk=api*",
                            null);
        assertEquals("ScribeUP développeur chez OpenSource", profile.getHeadline());
        assertNotNull(profile.getAccessSecret());
        assertEquals(6, profile.getAttributes().size());
    }
}
