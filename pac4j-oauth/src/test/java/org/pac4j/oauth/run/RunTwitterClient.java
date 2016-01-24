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
package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Color;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oauth.profile.twitter.TwitterProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link TwitterClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunTwitterClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunTwitterClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected IndirectClient getClient() {
        final TwitterClient twitterClient = new TwitterClient();
        twitterClient.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterClient.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterClient.setCallbackUrl(PAC4J_URL);
        return twitterClient;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(TwitterProfile.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final TwitterProfile profile = (TwitterProfile) userProfile;
        assertEquals("488358057", profile.getId());
        assertEquals(TwitterProfile.class.getName() + UserProfile.SEPARATOR + "488358057", profile.getTypedId());
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
        assertEquals(3600, profile.getUtcOffset().intValue());
        assertFalse(profile.getVerified());
        assertNotNull(profile.getAccessSecret());
        assertEquals(37, profile.getAttributes().size());
    }
}
