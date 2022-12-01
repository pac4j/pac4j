package org.pac4j.oidc.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Run a manual test for the CAS OpenID Connect wrapper support.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RunCasOidcWrapper extends RunClient {

    private final static String CLIENT_ID = "testoidc";

    public static void main(final String[] args) {
        new RunCasOidcWrapper().run();
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
        val configuration = new OidcConfiguration();
        configuration.setClientId(CLIENT_ID);
        configuration.setSecret("secret");
        //configuration.setDiscoveryURI("https://casserverpac4j.herokuapp.com/oidc/.well-known/openid-configuration");
        configuration.setDiscoveryURI("http://localhost:8888/cas/oidc/.well-known/openid-configuration");
        val client = new OidcClient(configuration);
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        val profile = (OidcProfile) userProfile;
        assertEquals(getLogin(), profile.getId());
        assertNotNull(profile.getIdToken());
        assertEquals("http://localhost:8080/cas/oidc", profile.getIssuer());
        assertEquals(CLIENT_ID, profile.getAttribute("preferred_username"));
        assertNotNull(profile.getAccessToken());
        assertEquals(CLIENT_ID, profile.getAudience().get(0));
        assertNotNull(profile.getNotBefore());
        assertNotNull(profile.getAuthTime());
        assertNotNull(profile.getAttribute("state"));
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("jti"));
        assertEquals(13, profile.getAttributes().size());
    }
}
