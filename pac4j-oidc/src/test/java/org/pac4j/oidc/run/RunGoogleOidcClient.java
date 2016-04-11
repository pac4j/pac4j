package org.pac4j.oidc.run;

import com.esotericsoftware.kryo.Kryo;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.kryo.AccessTokenTypeSerializer;
import org.pac4j.oidc.profile.GoogleOidcProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link GoogleOidcClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunGoogleOidcClient extends RunClient {

    public static void main(final String[] args) throws Exception {
        new RunGoogleOidcClient().run();
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
        final GoogleOidcClient client = new GoogleOidcClient();
        client.setClientID("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GoogleOidcProfile.class);
        kryo.register(AccessTokenType.class, new AccessTokenTypeSerializer());
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final GoogleOidcProfile profile = (GoogleOidcProfile) userProfile;
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(GoogleOidcProfile.class.getName() + CommonProfile.SEPARATOR + "113675986756217860428",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GoogleOidcProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(userProfile, getLogin(), "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.MALE, Locale.ENGLISH,
                "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                "https://plus.google.com/113675986756217860428", null);
        assertEquals(12, profile.getAttributes().size());
        assertTrue(profile.getEmailVerified());
    }
}
