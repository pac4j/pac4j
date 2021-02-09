package org.pac4j.oidc.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import static org.junit.Assert.*;

/**
 * Run a manual test for the IdentityServer4 (https://github.com/IdentityServer/IdentityServer4/src/Host)
 * with the following configuration:
 *
 * new Client
 * {
 *     ClientId = "test",
 *     ClientSecrets = new List<Secret>
 *     {
 *         new Secret("secret".Sha256())
 *     },
 *     RedirectUris = new List<string>
 *     {
 *         "http://www.pac4j.org/"
 *     },
 *     AllowedGrantTypes = GrantTypes.ImplicitAndClientCredentials,
 *     AllowedScopes = new List<string>
 *     {
 *         "openid", "profile", "email"
 *     }
 * },
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RunIdentityServer4 extends RunClient {

    private enum Flow { IMPLICIT_FLOW, IMPLICIT_FLOW_CLIENT_SIDE, AUTHORIZATION_CODE, HYBRID_FLOW };

    private final static Flow flow = Flow.HYBRID_FLOW;

    public static void main(final String[] args) {
        new RunIdentityServer4().run();
    }

    @Override
    protected String getLogin() {
        return "alice";
    }

    @Override
    protected String getPassword() {
        return "alice";
    }

    @Override
    protected IndirectClient getClient() {
        final var configuration = new OidcConfiguration();
        configuration.setClientId("test");
        configuration.setSecret("secret");
        configuration.setDiscoveryURI("http://localhost:1941/.well-known/openid-configuration");
        if (flow == Flow.IMPLICIT_FLOW) {
            // AllowedGrantTypes = GrantTypes.ImplicitAndClientCredentials,
            configuration.setResponseType("id_token");
            configuration.setResponseMode("form_post");
            configuration.setUseNonce(true);
            logger.warn("For the implicit flow, copy / paste the form body parameters after a ? as the returned url");
        } else if (flow == Flow.IMPLICIT_FLOW_CLIENT_SIDE) {
            // this flow can not be used in fact (as data ae passed as anchor parameters, only on client side)
            // AllowedGrantTypes = GrantTypes.ImplicitAndClientCredentials,
            configuration.setResponseType("id_token");
            configuration.setUseNonce(true);
        /*} else if (flow == Flow.AUTHORIZATION_CODE) {
            AllowedGrantTypes = GrantTypes.CodeAndClientCredentials,*/
        } else if (flow == Flow.HYBRID_FLOW) {
            // AllowAccessTokensViaBrowser = true, AllowedGrantTypes = GrantTypes.HybridAndClientCredentials,
            configuration.setResponseType("code id_token token");
            configuration.setUseNonce(true);
        } else if (flow != Flow.AUTHORIZATION_CODE) {
            throw new TechnicalException("Unsupported flow for tests");
        }
        final var client = new OidcClient(configuration);
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final var profile = (OidcProfile) userProfile;
        assertEquals("818727", profile.getId());
        assertNotNull(profile.getIdToken());
        assertEquals("test", profile.getAudience().get(0));
        assertNotNull(profile.getNotBefore());
        assertEquals("idsvr", profile.getAttribute("idp"));
        assertNotNull(profile.getAuthTime());
        assertEquals("http://localhost:1941", profile.getIssuer());
        assertEquals("Alice Smith", profile.getDisplayName());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("sid"));
        if (flow == Flow.IMPLICIT_FLOW || flow == Flow.IMPLICIT_FLOW_CLIENT_SIDE) {
            assertNull(profile.getAccessToken());
            assertEquals(12, profile.getAttributes().size());
        } else if (flow == Flow.AUTHORIZATION_CODE) {
            assertNotNull(profile.getAccessToken());
            assertEquals(12, profile.getAttributes().size());
        } else if (flow == Flow.HYBRID_FLOW) {
            assertNotNull(profile.getAccessToken());
            assertEquals(13, profile.getAttributes().size());
        }
    }
}
