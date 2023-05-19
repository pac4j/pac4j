package org.pac4j.oidc.authorization.generator;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link KeycloakRolesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class KeycloakRolesAuthorizationGeneratorTests {

    private static final String ACCESS_TOKEN =
        "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ6NTRlSnJINEZyY0dYMjNzcDFGdU95UV9HOHJyeWY0MWd1RXBHZkJpTmhzIn0.eyJqdGki"
        + "OiIzOWU0YjI3Ni1mNDI5LTRiZTItOGRhNC05MzM3N2IyM2E3MmEiLCJleHAiOjE1NDI2OTgwNjEsIm5iZiI6MCwiaWF0IjoxNTQyNjk3NzYxLCJpc3MiOi"
        + "JodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvZGVtbyIsImF1ZCI6WyJrZXljbG9ha2lkIiwiYWNjb3VudCJdLCJzdWIiOiIzYjQ2MzAxOC05"
        + "NWExLTQzMjgtOTkyMC1iNDM3N2IyOTE2NGUiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJrZXljbG9ha2lkIiwiYXV0aF90aW1lIjoxNTQyNjk3NTI3LCJzZX"
        + "NzaW9uX3N0YXRlIjoiYWEwZTg5MzYtZjNhOS00NjA5LWIzNDgtZmQ1NWQ1ZTZhYWE0IiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8v"
        + "bG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiUk9MRV9CSU5HTyIsInVtYV9hdXRob3JpemF0aW"
        + "9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsia2V5Y2xvYWtpZCI6eyJyb2xlcyI6WyJST0xFX0NMSUVOVElTU0lNRSJdfSwiYWNjb3VudCI6eyJyb2xlcyI6"
        + "WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgZW1haWwgcHJvZmlsZS"
        + "IsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkrDqXLDtG1lIExFTEVVIiwicHJlZmVycmVkX3VzZXJuYW1lIjoibGVsZXVqIiwiZ2l2ZW5fbmFt"
        + "ZSI6IkrDqXLDtG1lIiwiZmFtaWx5X25hbWUiOiJMRUxFVSIsImVtYWlsIjoibGVsZXVqQGdtYWlsLmNvbSJ9.dTCnU7qV8epo3RZPCcWuI2xEz_ejeQ0yg"
        + "OhDycnm11mTPd-BHc3mo6LOxAHs4EiUXcMIVpgvAmVzTB8jY7wyUUwaaOIyPES03pU0pH93QlBFCCV9D4e_Qqt7Dg0iUvCq57PQHmI0X80GSg8S0Nm-96J"
        + "ljNtm1d9dKETvFAKRXWzubRy_PXu6pz32GMDHmvIb7JvdcDe3tYuREZVwNTibfu6rG0KalMAYO1Q0u44fGDxcJTSxfZ7crtno4ujifw_azAw1Kpqqf2mbY"
        + "elw_pr5HALNHsRbpYp2DtxUWQ-tGYLP6H3fYS-cUCgkWyeS7Pl1KHPJBmWvEWEaJjeBfhfN3g";

    private AuthorizationGenerator generator = new KeycloakRolesAuthorizationGenerator("keycloakid");

    @Test
    public void test() {
        val profile = new KeycloakOidcProfile();
        final AccessToken accessToken = new BearerAccessToken(ACCESS_TOKEN);
        profile.setAccessToken(accessToken);
        generator.generate(new CallContext(MockWebContext.create(), new MockSessionStore()), profile);
        assertEquals(4, profile.getRoles().size());
    }
}
