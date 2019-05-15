package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.profile.google2.Google2Profile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link Google2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunGoogle2Client extends RunClient {

    public static void main(String[] args) {
        new RunGoogle2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup92";
    }

    @Override
    protected IndirectClient getClient() {
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        google2Client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        google2Client.setCallbackUrl(PAC4J_BASE_URL);
        google2Client.setScope(Google2Client.Google2Scope.EMAIL_AND_PROFILE);
        return google2Client;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final Google2Profile profile = (Google2Profile) userProfile;
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(Google2Profile.class.getName() + CommonProfile.SEPARATOR + "113675986756217860428",
            profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), Google2Profile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
            Gender.MALE, Locale.ENGLISH,
            "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
            "https://plus.google.com/113675986756217860428", null);
        assertTrue(profile.getEmailVerified());
        assertEquals(10, profile.getAttributes().size());
    }
}
