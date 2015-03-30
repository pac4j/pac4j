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

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.profile.Color;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.profile.twitter.TwitterProfile;

import com.esotericsoftware.kryo.Kryo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * This class tests the {@link TwitterClient} class by simulating a complete authentication.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterClientIT extends OAuthClientIT {

    @SuppressWarnings("rawtypes")
    @Override
    protected Client getClient() {
        final TwitterClient twitterClient = new TwitterClient();
        twitterClient.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterClient.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterClient.setCallbackUrl(PAC4J_URL);
        return twitterClient;
    }

    @Override
    protected String getCallbackUrl(final WebClient webClient, final HtmlPage authorizationPage) throws Exception {
        final HtmlForm form = authorizationPage.getForms().get(0);
        final HtmlTextInput sessionUsernameOrEmail = form.getInputByName("session[username_or_email]");
        sessionUsernameOrEmail.setValueAttribute("testscribeup@gmail.com");
        final HtmlPasswordInput sessionPassword = form.getInputByName("session[password]");
        sessionPassword.setValueAttribute("testpwdscribeup");
        final HtmlSubmitInput submit = authorizationPage.getHtmlElementById("allow");
        final HtmlPage callbackPage = submit.click();
        final String callbackUrl = callbackPage.getUrl().toString();
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(TwitterProfile.class);
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final TwitterProfile profile = (TwitterProfile) userProfile;
        logger.debug("userProfile : {}", profile);
        assertEquals("488358057", profile.getId());
        assertEquals(TwitterProfile.class.getSimpleName() + UserProfile.SEPARATOR + "488358057", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), TwitterProfile.class));
        assertTrue(StringUtils.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, null, null, "test scribeUP", "testscribeUP", Gender.UNSPECIFIED,
                Locale.UK, ".twimg.com/sticky/default_profile_images/default_profile_5_normal.png",
                "http://t.co/fNjYqp7wZ8", "New York");
        assertFalse(profile.getContributorsEnabled());
        assertEquals(TestsHelper.getFormattedDate(1328872224000L, "EEE MMM dd HH:mm:ss Z yyyy", Locale.US), profile
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
        assertTrue(profile.getProfileBackgroundImageUrl().contains(".twimg.com/images/themes/theme1/bg.png"));
        assertTrue(profile.getProfileBackgroundImageUrlHttps().endsWith("/images/themes/theme1/bg.png"));
        assertFalse(profile.getProfileBackgroundTile());
        assertTrue(profile.getProfileImageUrlHttps().endsWith(
                "/sticky/default_profile_images/default_profile_5_normal.png"));
        assertTrue(profile.getProfileLinkColor() instanceof Color);
        assertTrue(profile.getProfileSidebarBorderColor() instanceof Color);
        assertTrue(profile.getProfileSidebarFillColor() instanceof Color);
        assertTrue(profile.getProfileTextColor() instanceof Color);
        assertTrue(profile.getProfileUseBackgroundImage());
        assertTrue(profile.getProtected());
        assertNull(profile.getShowAllInlineMedia());
        assertEquals(0, profile.getStatusesCount().intValue());
        assertEquals("Amsterdam", profile.getTimeZone());
        assertEquals(7200, profile.getUtcOffset().intValue());
        assertFalse(profile.getVerified());
        assertNotNull(profile.getAccessSecret());
        assertEquals(37, profile.getAttributes().size());
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    @Override
    protected String getCallbackUrlForCancel(final HtmlPage authorizationPage) throws Exception {
        final HtmlSubmitInput submit = authorizationPage.getHtmlElementById("cancel");
        final HtmlPage callbackPage = submit.click();
        final List<HtmlAnchor> anchors = callbackPage.getAnchors();
        String callbackUrl = null;
        for (final HtmlAnchor anchor : anchors) {
            final String url = anchor.getHrefAttribute();
            if (url.startsWith(PAC4J_URL)) {
                callbackUrl = url;
                break;
            }
        }
        logger.debug("callbackUrl : {}", callbackUrl);
        return callbackUrl;
    }
}
