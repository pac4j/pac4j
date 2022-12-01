package org.pac4j.oidc.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run a manual test for the Okta cloud provider (using the OpenID Connect protocol).
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RunOkta extends RunClient {

    public static void main(final String[] args) {
        new RunOkta().run();
    }

    @Override
    protected String getLogin() {
        return "testpac4j@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "Pac4jtest";
    }

    @Override
    protected IndirectClient getClient() {
        val configuration = new OidcConfiguration();
        configuration.setClientId("ZuxDX1Gw2Kvx4gFyDNWC");
        configuration.setSecret("77kjmDs94pA4UOVkeuYY7XyHnsDmSWoezrc3XZFU");
        configuration.setDiscoveryURI("https://dev-425954.oktapreview.com/.well-known/openid-configuration");
        val client = new OidcClient(configuration);
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        val profile = (OidcProfile) userProfile;
        assertEquals("00u5h0czw1aIjTQtM0h7", profile.getId());
        assertEquals(OidcProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "00u5h0czw1aIjTQtM0h7",
                profile.getTypedId());
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getIdToken());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OidcProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(profile, getLogin(), "Test", "pac4j", "Test pac4j", "testpac4j@gmail.com",
                Gender.UNSPECIFIED, new Locale("en", "US"), null, null, "America/Los_Angeles");
        assertTrue((Boolean) profile.getAttribute("email_verified"));
        assertNotNull(profile.getAttribute("at_hash"));
        assertEquals("1", profile.getAttribute("ver").toString());
        assertNotNull(profile.getAmr());
        assertEquals("https://dev-425954.oktapreview.com", profile.getIssuer());
        assertEquals("ZuxDX1Gw2Kvx4gFyDNWC", profile.getAudience().get(0));
        assertEquals("00o5gxpohzF1JWEXZ0h7", profile.getAttribute("idp"));
        assertNotNull(profile.getAuthTime());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("jti"));
        assertEquals(22, profile.getAttributes().size());
    }
}
