package org.pac4j.oauth.run;

import org.pac4j.oauth.profile.google2.Google2AttributesDefinition;
import org.pac4j.oauth.profile.google2.Google2Email;
import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.GenericOAuth20StateClient;
import org.pac4j.oauth.profile.generic.GenericOAuth20Profile;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Test class for {@link GenericOAuth20StateClient}
 * Connects with Google for testing
 * @see RunGoogle2Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class RunGeneric20StateClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunGoogle2Client().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup91";
    }

    @Override
    protected IndirectClient getClient() {
        final GenericOAuth20StateClient client = new GenericOAuth20StateClient();
        client.setAttributesDefinition(new Google2AttributesDefinition());
        client.setAuthUrl("https://accounts.google.com/o/oauth2/auth");
        client.setTokenUrl("https://accounts.google.com/o/oauth2/token");
        client.setProfileUrl("https://www.googleapis.com/plus/v1/people/me");
        client.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setScope("profile email");
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GenericOAuth20Profile.class);
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final GenericOAuth20Profile profile = (GenericOAuth20Profile) userProfile;
        profile.setAttributesDefinition(new Google2AttributesDefinition());
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(GenericOAuth20StateClient.class.getName() + CommonProfile.SEPARATOR + "113675986756217860428",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GenericOAuth20Profile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertCommonProfile(userProfile, "testscribeup@gmail.com", "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.MALE, Locale.ENGLISH,
                "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                "https://plus.google.com/113675986756217860428", null);
        assertNull(profile.getAttribute("birthday"));
        List<Google2Email> emails = (List<Google2Email>) profile.getAttribute("emails");
        assertTrue(emails != null && emails.size() == 1);
        assertEquals(9, profile.getAttributes().size());
    }


}
