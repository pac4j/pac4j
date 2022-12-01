package org.pac4j.oidc.run;

import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oidc.client.KeycloakOidcClient;
import org.pac4j.oidc.config.KeycloakOidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Run a manual test for keycloak (http://www.keycloak.org/)
 *
 * @author Julio Arrebola
 * @since 2.0.0
 */
public class RunKeycloakOidcClient extends RunClient {

    // Test values
    private static final String CLIENT_ID="test";
    private static final String SECRET_VALUE="secret";
    private static final String AUTH_URL="http://localhost:8080/auth";
    private static final String REALM_VALUE="test";
    private static final String CALLBACK_VALUE=PAC4J_BASE_URL;
    private static final String LOGIN="user";
    private static final String PASSWORD_VALUE="password";

    // Profile values
    private static final String IDENTIFIER="IDENTIFIER";
    private static final String NAME_VALUE="Name";
    private static final String PREFERRED_USERNAME="preferred_username";
    private static final String GIVEN_NAME="GivenName";
    private static final String FAMILY_NAME="FamilyName";
    private static final String EMAIL_VALUE="test@test.com";
    private static final String ISSUER="http://localhost:8080/auth/realms/test";

    public static void main(final String[] args) {
        new RunKeycloakOidcClient().run();
    }

    @Override
    protected String getLogin() {
        return LOGIN;
    }

    @Override
    protected String getPassword() {
        return PASSWORD_VALUE;
    }

    @Override
    protected IndirectClient getClient() {
        val configuration = new KeycloakOidcConfiguration();

        configuration.setClientId(CLIENT_ID);
        configuration.setSecret(SECRET_VALUE);
        configuration.setBaseUri(AUTH_URL);
        configuration.setRealm(REALM_VALUE);
        var client = new KeycloakOidcClient(configuration);
        client.setCallbackUrl(CALLBACK_VALUE);

        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        val profile = (OidcProfile) userProfile;
        assertEquals(IDENTIFIER, profile.getId());
        assertNotNull(profile.getIdToken());
        assertNotNull(profile.getNotBefore());
        assertEquals(ISSUER, profile.getIssuer());
        assertEquals(NAME_VALUE, profile.getDisplayName());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAccessToken());
        assertEquals(GIVEN_NAME, profile.getAttribute("given_name"));
        assertEquals(FAMILY_NAME, profile.getAttribute("family_name"));
        assertEquals(PREFERRED_USERNAME, profile.getAttribute("preferred_username"));
        assertEquals(EMAIL_VALUE, profile.getAttribute("email"));
    }
}
