package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.LinkedIn2Client;
import org.pac4j.oauth.profile.linkedin2.*;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link LinkedIn2Client}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunLinkedIn2Client extends RunClient {

    public static void main(String[] args) throws Exception {
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
        final LinkedIn2Client client = new LinkedIn2Client();
        client.setKey("gsqj8dn56ayn");
        client.setSecret("kUFAZ2oYvwMQ6HFl");
        client.setScope("r_basicprofile r_emailaddress rw_company_admin w_share");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(LinkedIn2Profile.class);
        kryo.register(LinkedIn2Location.class);
        kryo.register(LinkedIn2Position.class);
        kryo.register(LinkedIn2Date.class);
        kryo.register(LinkedIn2Company.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(UserProfile userProfile) {
        final LinkedIn2Profile profile = (LinkedIn2Profile) userProfile;
        assertEquals("JJjS_5BOzW", profile.getId());
        assertEquals(LinkedIn2Profile.class.getName() + UserProfile.SEPARATOR + "JJjS_5BOzW",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), LinkedIn2Profile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile,
                null,
                "test",
                "scribeUp",
                null,
                null,
                Gender.UNSPECIFIED,
                null,
                null,
                null,
                null);
        assertEquals("https://www.linkedin.com/profile/view?id=AAoAAAn67mMBxVIxeJXn2T6XBvOFEAMLv7RiJQQ&authType=name&authToken=_IWF&trk=api*a167383*s175634*", profile.getSiteStandardProfileRequest());
        assertEquals("AAoAAAn67mMBxVIxeJXn2T6XBvOFEAMLv7RiJQQ", profile.getOAuth10Id());
        assertEquals(5, profile.getAttributes().size());
    }
}
