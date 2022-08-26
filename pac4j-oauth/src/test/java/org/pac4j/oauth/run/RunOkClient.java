package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.OkClient;
import org.pac4j.oauth.profile.ok.OkProfile;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Run manually a test for the {@link OkClient}.
 * https://www.ok.ru/
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunOkClient extends RunClient {

    /**
     * Real profile id.
     */
    private static final String TEST_PROFILE_ID = "570212941891";
    /**
     * Real profile location.
     */
    private static final String TEST_LOCATION = ", CHINA";
    /**
     * Real profile locale.
     */
    private static final String TEST_LOCALE = "en";
    /**
     * Real profile first name.
     */
    private static final String TEST_FIRST_NAME = Pac4jConstants.EMPTY_STRING;
    /**
     * Real profile last name.
     */
    private static final String TEST_LAST_NAME = Pac4jConstants.EMPTY_STRING;
    /**
     * Real profile picture url.
     */
    private static final String TEST_PROFILE_PICTURE_URL = Pac4jConstants.EMPTY_STRING;

    public static void main(String[] args) {
        new RunOkClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup56";
    }

    @Override
    protected IndirectClient getClient() {
        final var okClient = new OkClient();
        okClient.setKey("1139019264");
        okClient.setPublicKey("CBAPAFOEEBABABABA");
        okClient.setSecret("479452FD7CA726DF558B4303");
        okClient.setCallbackUrl(PAC4J_URL);
        return okClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final var profile = (OkProfile) userProfile;
        assertEquals(TEST_PROFILE_ID, profile.getId());
        assertEquals(OkProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + TEST_PROFILE_ID,
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OkProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(
                userProfile,
                null,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_FIRST_NAME + " " + TEST_LAST_NAME,
                TEST_PROFILE_ID,
                Gender.MALE,
                new Locale(TEST_LOCALE),
                TEST_PROFILE_PICTURE_URL,
                OkProfile.BASE_PROFILE_URL + TEST_PROFILE_ID,
                TEST_LOCATION);
    }
}
