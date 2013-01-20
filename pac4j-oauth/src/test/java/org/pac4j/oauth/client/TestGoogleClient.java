/*
  Copyright 2012 - 2013 Jerome Leleu

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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.google.GoogleObject;
import org.pac4j.oauth.profile.google.GoogleProfile;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link GoogleClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestGoogleClient extends TestOAuthClient {
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final GoogleClient googleClient = new GoogleClient();
        googleClient.setKey("anonymous");
        googleClient.setSecret("anonymous");
        googleClient.setCallbackUrl(GOOGLE_URL);
        return googleClient;
    }
    
    @Override
    public void testAuthenticationAndUserProfileRetrieval() throws Exception {
        // TODO : remove Google OAuth 1.0 or reactivate ?
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute("testpwdscribeup34");
        HtmlSubmitInput submit = form.getInputByName("signIn");
        final HtmlPage confirmPage = submit.click();
        form = confirmPage.getForms().get(0);
        submit = form.getInputByName("allow");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final GoogleProfile profile = (GoogleProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(GoogleProfile.class.getSimpleName() + UserProfile.SEPARATOR + "113675986756217860428",
                     profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GoogleProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                            null,
                            "Jérôme",
                            "ScribeUP",
                            "Jérôme ScribeUP",
                            null,
                            Gender.UNSPECIFIED,
                            null,
                            "http://www.google.com/ig/c/photos/public/AIbEiAIAAABECMziv-rwr7flvQEiC3ZjYXJkX3Bob3RvKig5M2ViZDA5M2FhNmRmMmQ5ODVlZmQzM2Y5ZjYzZmQ1Y2YwMWFjYTM4MAEvKPh0rtxIK4u-apq8WQapWoSgNg",
                            "https://plus.google.com/113675986756217860428", null);
        assertTrue(profile.getIsViewer());
        assertEquals("Jérôme ScribeUP", profile.getFormatted());
        final List<GoogleObject> urls = profile.getUrls();
        final GoogleObject url = urls.get(0);
        assertEquals("https://plus.google.com/113675986756217860428", url.getValue());
        assertEquals("profile", url.getType());
        final List<GoogleObject> photos = profile.getPhotos();
        final GoogleObject photo = photos.get(0);
        assertEquals("http://www.google.com/ig/c/photos/public/AIbEiAIAAABECMziv-rwr7flvQEiC3ZjYXJkX3Bob3RvKig5M2ViZDA5M2FhNmRmMmQ5ODVlZmQzM2Y5ZjYzZmQ1Y2YwMWFjYTM4MAEvKPh0rtxIK4u-apq8WQapWoSgNg",
                     photo.getValue());
        assertEquals("thumbnail", photo.getType());
        assertEquals(10, profile.getAttributes().size());
    }
}
