package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;

import static org.junit.Assert.*;

/**
 * Run a manual test for the {@link CasOAuthWrapperClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunCasOAuthWrapperClient extends RunClient {

    public static void main(String[] args) {
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
        //client.setImplicitFlow(true);
        //client.setCasOAuthUrl("http://casserverpac4j.herokuapp.com/oauth2.0");
        client.setCasOAuthUrl("http://localhost:8888/cas/oauth2.0");
        return client;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final CasOAuthWrapperProfile profile = (CasOAuthWrapperProfile) userProfile;
        assertEquals(getLogin(), profile.getId());
        assertEquals(CasOAuthWrapperProfile.class.getName() + CommonProfile.SEPARATOR + getLogin(),
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CasOAuthWrapperProfile.class));
        assertTrue(CommonHelper.isNotBlank(profile.getAccessToken()));
        assertTrue(profile.isFromNewLogin());
        assertEquals("AcceptUsersAuthenticationHandler", profile.getAuthenticationMethod());
        assertFalse(profile.isLongTermAuthenticationRequestTokenUsed());
        assertEquals("AcceptUsersAuthenticationHandler", profile.getSuccessfulAuthenticationHandlers());
        assertNotNull(profile.getAuthenticationDate());
        assertEquals(6, profile.getAttributes().size());
    }
}
