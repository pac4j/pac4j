package org.pac4j.oidc.run;

import com.esotericsoftware.kryo.Kryo;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.kryo.AccessTokenTypeSerializer;
import org.pac4j.oidc.profile.AzureAdProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link AzureAdClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunAzureAdClient extends RunClient {

    public static void main(final String[] args) throws Exception {
        new RunAzureAdClient().run();
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
        final AzureAdClient client = new AzureAdClient();
        client.setClientID("788339d7-1c44-4732-97c9-134cb201f01f");
        client.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
        client.setDiscoveryURI("https://login.microsoftonline.com/38c46e5a-21f0-46e5-940d-3ca06fd1a330/.well-known/openid-configuration");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(AzureAdProfile.class);
        kryo.register(AccessTokenType.class, new AccessTokenTypeSerializer());
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final AzureAdProfile profile = (AzureAdProfile) userProfile;
        assertEquals("alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE", profile.getId());
        assertEquals(AzureAdProfile.class.getName() + UserProfile.SEPARATOR + "alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), AzureAdProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(userProfile, getLogin(), "Jérôme", "TESTPAC4J", "MyDisplayName", null,
                Gender.UNSPECIFIED, null, null, null, null);
        assertEquals("live.com", profile.getIdp());
        assertEquals(10, profile.getAttributes().size());
    }
}
