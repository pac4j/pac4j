package org.pac4j.oidc.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.config.AzureAdOidcConfiguration;
import org.pac4j.oidc.profile.azuread.AzureAdProfile;

import static org.junit.Assert.*;

/**
 * Run a manual test for the {@link AzureAdClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@SuppressWarnings( "deprecation" )
public class RunAzureAdClient extends RunClient {

    public static void main(final String[] args) {
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
        final AzureAdOidcConfiguration configuration = new AzureAdOidcConfiguration();
        configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
        configuration.setSecret("7~nTx-1_-zn~04jLMfY4J~af6vRY9wXrYW");
        configuration.setTenant("38c46e5a-21f0-46e5-940d-3ca06fd1a330");
        final AzureAdClient client = new AzureAdClient(configuration);
        client.setCallbackUrl("https://www.pac4j.org/test.html");
        //client.setCallbackUrl(CommonHelper.addParameter(PAC4J_URL, Clients.DEFAULT_CLIENT_NAME_PARAMETER, client.getName()));
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final AzureAdProfile profile = (AzureAdProfile) userProfile;
        assertEquals("alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE", profile.getId());
        assertEquals(AzureAdProfile.class.getName() + CommonProfile.SEPARATOR + "alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE",
                profile.getTypedId());
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getIdToken());
        assertNotNull(profile.getRefreshToken());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), AzureAdProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(profile, getLogin(), "Jérôme", "TESTPAC4J", "MyDisplayName", null,
                Gender.UNSPECIFIED, null, null, null, null);
        assertEquals("live.com", profile.getIdp());
        assertEquals("6c59c433-11b5-4fb1-9641-40b829e7a8e4", profile.getOid());
        assertEquals("38c46e5a-21f0-46e5-940d-3ca06fd1a330", profile.getTid());
        assertEquals("1.0", profile.getVer());
        assertNotNull(profile.getAmr());
        assertNotNull(profile.getIssuer());
        assertEquals("788339d7-1c44-4732-97c9-134cb201f01f", profile.getAudience().get(0));
        assertEquals("live.com#" + getLogin(), profile.getUniqueName());
        assertNotNull(profile.getNotBefore());
        assertEquals("live.com", profile.getIdp());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIpaddr());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("sub"));
        assertNull(profile.getAttribute("pwd_exp"));
        assertEquals(25, profile.getAttributes().size());
    }
}
