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

import org.scribe.up.profile.Color;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.impl.TwitterProvider;
import org.scribe.up.test.util.CommonHelper;

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
public class TestTwitterProvider extends TestProvider {
    
    @Override
    protected OAuthProvider getProvider() {
        final TwitterProvider twitterProvider = new TwitterProvider();
        twitterProvider.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterProvider.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterProvider.setCallbackUrl("http://www.google.com/");
        return twitterProvider;
    }
    
    @Override
    protected String getCallbackUrl(final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput sessionUsernameOrEmail = form.getInputByName("session[username_or_email]");
        sessionUsernameOrEmail.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput sessionPassword = form.getInputByName("session[password]");
        sessionPassword.setValueAttribute("testpwdscribeup");
        final HtmlSubmitInput submit = form.getElementById("allow");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
    
    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final TwitterProfile profile = (TwitterProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("488358057", profile.getId());
        assertEquals(TwitterProfile.class.getSimpleName() + UserProfile.SEPARATOR + "488358057", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), TwitterProfile.class));
        assertCommonProfile(userProfile, null, null, null, "test scribeUP", "testscribeUP", Gender.UNSPECIFIED,
                            Locale.FRENCH,
                            "http://a0.twimg.com/sticky/default_profile_images/default_profile_5_normal.png",
                            "http://github.com/leleuj", "New York");
        assertFalse(profile.getContributorsEnabled());
        assertEquals(CommonHelper.getFormattedDate(1328872224000L, "EEE MMM dd HH:mm:ss Z yyyy", Locale.US), profile
            .getCreatedAt().toString());
        assertTrue(profile.getDefaultProfile());
        assertTrue(profile.getDefaultProfileImage());
        assertEquals("biographie", profile.getDescription());
        assertEquals(0, profile.getFavouritesCount().intValue());
        assertFalse(profile.getFollowRequestSent());
        assertEquals(0, profile.getFollowersCount().intValue());
        assertFalse(profile.getFollowing());
        assertEquals(0, profile.getFriendsCount().intValue());
        assertFalse(profile.getGeoEnabled());
        assertFalse(profile.getIsTranslator());
        assertEquals(0, profile.getListedCount().intValue());
        assertFalse(profile.getNotifications());
        assertTrue(profile.getProfileBackgroundColor() instanceof Color);
        assertEquals("http://a0.twimg.com/images/themes/theme1/bg.png", profile.getProfileBackgroundImageUrl());
        assertEquals("https://si0.twimg.com/images/themes/theme1/bg.png", profile.getProfileBackgroundImageUrlHttps());
        assertFalse(profile.getProfileBackgroundTile());
        assertEquals("https://si0.twimg.com/sticky/default_profile_images/default_profile_5_normal.png",
                     profile.getProfileImageUrlHttps());
        assertTrue(profile.getProfileLinkColor() instanceof Color);
        assertTrue(profile.getProfileSidebarBorderColor() instanceof Color);
        assertTrue(profile.getProfileSidebarFillColor() instanceof Color);
        assertTrue(profile.getProfileTextColor() instanceof Color);
        assertTrue(profile.getProfileUseBackgroundImage());
        assertTrue(profile.getProtected());
        assertNull(profile.getShowAllInlineMedia());
        assertEquals(0, profile.getStatusesCount().intValue());
        assertEquals("Amsterdam", profile.getTimeZone());
        assertEquals(3600, profile.getUtcOffset().intValue());
        assertFalse(profile.getVerified());
        assertEquals(36, profile.getAttributes().size());
    }
}
