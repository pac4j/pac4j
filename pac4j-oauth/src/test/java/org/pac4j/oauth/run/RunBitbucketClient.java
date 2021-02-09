package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.BitbucketClient;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;

import static org.junit.Assert.*;

/**
 * Run a manual test for the {@link BitbucketClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunBitbucketClient extends RunClient {

    public static void main(String[] args) {
        new RunBitbucketClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup78";
    }

    @Override
    protected IndirectClient getClient() {
        var client = new BitbucketClient();
        client.setKey("bjEt8BMpLwFDqZUvp6");
        client.setSecret("NN6fVXRTcV2qYVejVLZqxBRqHgn3ygD4");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        var profile = (BitbucketProfile) userProfile;
        assertEquals("testscribeup", profile.getId());
        assertEquals(BitbucketProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "testscribeup", profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), BitbucketProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "Test", "Scribeup", "Test Scribeup", "testscribeup", Gender.UNSPECIFIED, null,
                "https://bitbucket.org/account/testscribeup/avatar/32/?ts=", "/1.0/users/testscribeup", null);
        assertFalse(profile.isTeam());
    }
}
