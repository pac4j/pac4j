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

import java.awt.Color;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.scribe.up.provider.impl.TwitterProvider;
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
 * This class tests the {@link org.scribe.up.provider.impl.TwitterProvider} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestTwitterProvider extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TestTwitterProvider.class);
    
    public void testProvider() throws Exception {
        // init provider
        TwitterProvider twitterProvider = new TwitterProvider();
        twitterProvider.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterProvider.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterProvider.setCallbackUrl("http://www.google.com/");
        twitterProvider.init();
        
        // authorization url
        SingleUserSession testSession = new SingleUserSession();
        String authorizationUrl = twitterProvider.getAuthorizationUrl(testSession);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        WebClient webClient = WebHelper.newClient();
        HtmlPage loginPage = webClient.getPage(authorizationUrl);
        HtmlForm form = loginPage.getForms().get(0);
        HtmlTextInput sessionUsernameOrEmail = form.getInputByName("session[username_or_email]");
        sessionUsernameOrEmail.setValueAttribute("testscribeup@gmail.com");
        HtmlPasswordInput sessionPassword = form.getInputByName("session[password]");
        sessionPassword.setValueAttribute("testpwdscribeup");
        HtmlSubmitInput submit = form.getElementById("allow");
        HtmlPage callbackPage = submit.click();
        String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        
        OAuthCredential credential = twitterProvider.getCredential(WebHelper.getParametersFromUrl(callbackUrl));
        // access token
        Token accessToken = twitterProvider.getAccessToken(testSession, credential);
        logger.debug("accessToken : {}", accessToken);
        // user profile
        TwitterProfile profile = (TwitterProfile) twitterProvider.getUserProfile(accessToken);
        logger.debug("userProfile : {}", profile);
        assertEquals("488358057", profile.getId());
        assertEquals("test scribeUP", profile.getAttributes().get("name"));
        assertEquals(36, profile.getAttributes().size());
        assertFalse(profile.isContributorsEnabled());
        assertTrue(profile.getCreatedAt() instanceof Date);
        assertTrue(profile.isDefaultProfile());
        assertTrue(profile.isDefaultProfileImage());
        assertEquals("biographie", profile.getDescription());
        assertEquals(0, profile.getFavouritesCount());
        assertFalse(profile.isFollowRequestSent());
        assertEquals(0, profile.getFollowersCount());
        assertFalse(profile.isFollowing());
        assertEquals(0, profile.getFriendsCount());
        assertFalse(profile.isGeoEnabled());
        assertFalse(profile.isTranslator());
        assertEquals(Locale.FRENCH, profile.getLang());
        assertEquals(0, profile.getListedCount());
        assertEquals("New York", profile.getLocation());
        assertEquals("test scribeUP", profile.getName());
        assertFalse(profile.isNotifications());
        assertTrue(profile.getProfileBackgroundColor() instanceof Color);
        assertEquals("http://a0.twimg.com/images/themes/theme1/bg.png", profile.getProfileBackgroundImageUrl());
        assertEquals("https://si0.twimg.com/images/themes/theme1/bg.png", profile.getProfileBackgroundImageUrlHttps());
        assertFalse(profile.isProfileBackgroundTile());
        assertEquals("http://a0.twimg.com/sticky/default_profile_images/default_profile_5_normal.png",
                     profile.getProfileImageUrl());
        assertEquals("https://si0.twimg.com/sticky/default_profile_images/default_profile_5_normal.png",
                     profile.getProfileImageUrlHttps());
        assertTrue(profile.getProfileLinkColor() instanceof Color);
        assertTrue(profile.getProfileSidebarBorderColor() instanceof Color);
        assertTrue(profile.getProfileSidebarFillColor() instanceof Color);
        assertTrue(profile.getProfileTextColor() instanceof Color);
        assertTrue(profile.isProfileUseBackgroundImage());
        assertTrue(profile.isProtected());
        assertEquals("testscribeUP", profile.getScreenName());
        assertFalse(profile.isShowAllInlineMedia());
        assertEquals(0, profile.getStatusesCount());
        assertEquals("Amsterdam", profile.getTimeZone());
        assertNull(profile.getUrl());
        assertEquals(3600, profile.getUtcOffset());
        assertFalse(profile.isVerified());
    }
}
