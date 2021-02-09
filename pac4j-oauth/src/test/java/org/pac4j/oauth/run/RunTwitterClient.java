package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oauth.profile.twitter.TwitterProfile;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link TwitterClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunTwitterClient extends RunClient {

    public static void main(String[] args) {
        new RunTwitterClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup12";
    }

    @Override
    protected IndirectClient getClient() {
        final var twitterClient = new TwitterClient();
        twitterClient.setKey("3nJPbVTVRZWAyUgoUKQ8UA");
        twitterClient.setSecret("h6LZyZJmcW46Vu8R47MYfeXTSYGI30EqnWaSwVhFkbA");
        twitterClient.setCallbackUrl(PAC4J_URL);
        return twitterClient;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final var profile = (TwitterProfile) userProfile;
        assertEquals("488358057", profile.getId());
        assertEquals(TwitterProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "488358057", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), TwitterProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, null, null, "test scribeUP", "testscribeUP", Gender.UNSPECIFIED,
                Locale.UK, ".twimg.com/sticky/default_profile_images/default_profile_normal.png",
                "http://t.co/fNjYqp7wZ8", "New York");
        assertFalse(profile.getContributorsEnabled());
        assertEquals("Fri Feb 10 12:10:24 +0100 2012",
            new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US).format(profile.getCreatedAt()));
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
        assertTrue(profile.getProfileBackgroundImageUrl().toString().contains(".twimg.com/images/themes/theme1/bg.png"));
        assertTrue(profile.getProfileBackgroundImageUrlHttps().toString().endsWith("/images/themes/theme1/bg.png"));
        assertFalse(profile.getProfileBackgroundTile());
        assertTrue(profile.getProfileImageUrlHttps().toString().endsWith(
                "/sticky/default_profile_images/default_profile_normal.png"));
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
}
