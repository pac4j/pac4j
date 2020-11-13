package org.pac4j.oidc.run;

import com.nimbusds.jose.JWSAlgorithm;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import static org.junit.Assert.*;

/**
 * Run a manual test for the MitreID.org provider (using the OpenID Connect protocol).
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RunMitreIdOrg extends RunClient {

    public static void main(final String[] args) {
        new RunMitreIdOrg().run();
    }

    @Override
    protected String getLogin() {
        return "admin";
    }

    @Override
    protected String getPassword() {
        return "password";
    }

    @Override
    protected IndirectClient getClient() {
        final OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("acdf79d7-0129-4ba3-bc61-a52486cf82ff");
        configuration.setSecret("ALhlPK5ONNGojjZvEiIgyNEUfX1MbAlDXT1dM0-pVQSa-IID5QMq-lEhlawRqejPZ8c70LBqfKyFL79tefmPb7k");
        configuration.setDiscoveryURI("https://mitreid.org/.well-known/openid-configuration");
        configuration.setPreferredJwsAlgorithm(JWSAlgorithm.parse("none"));
        final OidcClient client = new OidcClient(configuration);
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final OidcProfile profile = (OidcProfile) userProfile;
        assertEquals("90342.ASDFJWFA", profile.getId());
        assertEquals(OidcProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + "90342.ASDFJWFA",
                profile.getTypedId());
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getIdToken());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), OidcProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(profile, "admin@example.com", null, null, "Demo Admin", "admin",
                Gender.UNSPECIFIED, null, null, null, null);
        assertTrue((Boolean) profile.getAttribute("email_verified"));
        assertEquals("https://mitreid.org/", profile.getIssuer());
        assertEquals("acdf79d7-0129-4ba3-bc61-a52486cf82ff", profile.getAudience().get(0));
        assertNotNull(profile.getAuthTime());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("jti"));
        assertEquals(13, profile.getAttributes().size());
    }
}
