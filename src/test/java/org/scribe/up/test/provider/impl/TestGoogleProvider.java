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

import java.util.List;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.profile.google.GoogleProfile;
import org.scribe.up.provider.impl.GoogleProvider;
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
 * This class tests the {@link org.scribe.up.provider.impl.GoogleProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestGoogleProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestGoogleProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        GoogleProvider googleProvider = new GoogleProvider();
        googleProvider.setKey("anonymous");
        googleProvider.setSecret("anonymous");
        googleProvider.setCallbackUrl("http://www.google.com/");
        googleProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = googleProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput email = form.getInputByName("Email");
        email.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput passwd = form.getInputByName("Passwd");
        passwd.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getInputByName("signIn");
        HtmlPage confirmPage = submit.click();
        form = confirmPage.getForms().get(0);
        submit = form.getInputByName("allow");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = googleProvider.getCredential(testSession,
                                                                  WebHelper.getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = googleProvider.getAccessToken(credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        GoogleProfile profile = (GoogleProfile) googleProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", profile);
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(9, profile.getAttributes().size());
        assertEquals("test ScribeUP", profile.getDisplayName());
        assertEquals("", profile.getProfileUrl());
        assertEquals(true, profile.isViewer());
        assertEquals("http://www.google.com/ig/c/photos/public/AIbEiAIAAABECMziv-rwr7flvQEiC3ZjYXJkX3Bob3RvKig5M2ViZDA5M2FhNmRmMmQ5ODVlZmQzM2Y5ZjYzZmQ1Y2YwMWFjYTM4MAEvKPh0rtxIK4u-apq8WQapWoSgNg",
                     profile.getThumbnailUrl());
        assertEquals("test ScribeUP", profile.getFormatted());
        assertEquals("ScribeUP", profile.getFamilyName());
        assertEquals("test", profile.getGivenName());
        List<GoogleObject> urls = profile.getUrls();
        GoogleObject url = urls.get(0);
        assertEquals("", url.getValue());
        assertEquals("profile", url.getType());
        List<GoogleObject> photos = profile.getPhotos();
        GoogleObject photo = photos.get(0);
        assertEquals("http://www.google.com/ig/c/photos/public/AIbEiAIAAABECMziv-rwr7flvQEiC3ZjYXJkX3Bob3RvKig5M2ViZDA5M2FhNmRmMmQ5ODVlZmQzM2Y5ZjYzZmQ1Y2YwMWFjYTM4MAEvKPh0rtxIK4u-apq8WQapWoSgNg",
                     photo.getValue());
        assertEquals("thumbnail", photo.getType());
    }
}
