package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.WindowsLiveClient;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link WindowsLiveClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunWindowsLiveClient extends RunClient {

    public static void main(String[] args) {
        new RunWindowsLiveClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribe12";
    }

    @Override
    protected IndirectClient getClient() {
        final WindowsLiveClient liveClient = new WindowsLiveClient();
        liveClient.setKey("00000000400BFE75");
        liveClient.setSecret("9yz0WtTIUQVV7HhBV2tccTziETOt4pRG");
        liveClient.setCallbackUrl(PAC4J_URL);
        return liveClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final WindowsLiveProfile profile = (WindowsLiveProfile) userProfile;
        assertEquals("416c383b220392d8", profile.getId());
        assertEquals(WindowsLiveProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "416c383b220392d8",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), WindowsLiveProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, null, "Test", "ScribeUP", "Test ScribeUP", null, Gender.UNSPECIFIED,
                Locale.US, null, "https://profile.live.com/", null);
        assertEquals(6, profile.getAttributes().size());
    }
}
