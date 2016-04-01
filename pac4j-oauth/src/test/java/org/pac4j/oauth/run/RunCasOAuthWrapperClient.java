package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link CasOAuthWrapperClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunCasOAuthWrapperClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunCasOAuthWrapperClient().run();
    }

    @Override
    protected String getLogin() {
        return "jleleu";
    }

    @Override
    protected String getPassword() {
        return "jleleu";
    }

    @Override
    protected IndirectClient getClient() {
        final CasOAuthWrapperClient client = new CasOAuthWrapperClient();
        client.setKey("key");
        client.setSecret("secret");
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setCasOAuthUrl("http://casserverpac4j.herokuapp.com/oauth2.0");
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(CasOAuthWrapperProfile.class);
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final CasOAuthWrapperProfile profile = (CasOAuthWrapperProfile) userProfile;
        assertEquals(USERNAME, profile.getId());
        assertEquals(CasOAuthWrapperProfile.class.getName() + CommonProfile.SEPARATOR + USERNAME,
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CasOAuthWrapperProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertEquals("uid", profile.getAttribute("uid"));
        assertEquals("eduPersonAffiliation", profile.getAttribute("eduPersonAffiliation"));
        assertEquals("groupMembership", profile.getAttribute("groupMembership"));
        assertEquals(4, profile.getAttributes().size());
    }
}
