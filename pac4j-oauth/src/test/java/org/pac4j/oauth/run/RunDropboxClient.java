package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.DropBoxClient;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link DropBoxClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunDropboxClient extends RunClient {

    public static void main(String[] args) {
        new RunDropboxClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpac4j";
    }

    @Override
    protected IndirectClient getClient() {
        final DropBoxClient dropBoxClient = new DropBoxClient();
        dropBoxClient.setKey("0194c6m79qll0ia");
        dropBoxClient.setSecret("a0ylze9a0bhsvxv");
        dropBoxClient.setCallbackUrl("https://www.google.com");
        return dropBoxClient;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final DropBoxProfile profile = (DropBoxProfile) userProfile;
        assertEquals("dbid:AAAQmbTqWmcB1jsyM9xwe9m1rEPS5SH9UEU", profile.getId());
        assertEquals(DropBoxProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "dbid:AAAQmbTqWmcB1jsyM9xwe9m1rEPS5SH9UEU",
            profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), DropBoxProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, getLogin(), "Test", "ScribeUP", "Test ScribeUP", null, Gender.UNSPECIFIED, Locale.FRENCH,
                null, "https://db.tt/T0YkdWpF", null);
        assertEquals(true, profile.getEmailVerified());
        assertEquals(9, profile.getAttributes().size());
    }
}
