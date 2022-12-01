package org.pac4j.oauth.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.LinkedIn2Client;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Run manually a test for the {@link LinkedIn2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunLinkedIn2Client extends RunClient {

    public static void main(String[] args) {
        new RunLinkedIn2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup56";
    }

    @Override
    protected IndirectClient getClient() {
        val client = new LinkedIn2Client();
        client.setKey("86xtdkj897xk7d");
        client.setSecret("lU5w96OOs4hJ7HqU");
        client.setScope("r_liteprofile r_emailaddress");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        val profile = (LinkedIn2Profile) userProfile;
        assertEquals("X_0RQBLXtJ", profile.getId());
        assertEquals(LinkedIn2Profile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "X_0RQBLXtJ",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedIn2Profile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                "testscribeup@gmail.com",
                "test",
                "scribeUp",
                "test scribeUp",
                null,
                Gender.UNSPECIFIED,
                null,
                "https://media.licdn.com/dms/image/C5603AQHfitHQ5I8fcg/profile-displayphoto-shrink_100_100/" +
                    "0?e=15",
                null, null);
    }
}
