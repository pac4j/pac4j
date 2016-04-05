package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oauth.client.BitbucketClient;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link BitbucketClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunBitbucketClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunBitbucketClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup78";
    }

    @Override
    protected IndirectClient getClient() {
        BitbucketClient client = new BitbucketClient();
        client.setKey("bjEt8BMpLwFDqZUvp6");
        client.setSecret("NN6fVXRTcV2qYVejVLZqxBRqHgn3ygD4");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void registerForKryo(Kryo kryo) {
        kryo.register(BitbucketProfile.class);
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        BitbucketProfile profile = (BitbucketProfile) userProfile;
        assertEquals("testscribeup", profile.getUsername());
        assertEquals("Test", profile.getFirstName());
        assertEquals("Scribeup", profile.getFamilyName());
        assertEquals("Test Scribeup", profile.getDisplayName());
        assertFalse(profile.isTeam());
        assertTrue(profile.getPictureUrl().startsWith("https://bitbucket.org/account/testscribeup/avatar"));
        assertEquals("/1.0/users/testscribeup", profile.getProfileUrl());
    }
}
