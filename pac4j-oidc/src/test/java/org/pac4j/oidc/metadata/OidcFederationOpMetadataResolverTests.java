package org.pac4j.oidc.metadata;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.creator.TokenValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link OidcFederationOpMetadataResolver}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class OidcFederationOpMetadataResolverTests {

    private static final String METADATA_CLIENT_SECRET_BASIC = """
        {
          "issuer": "https://op.example.org",
          "authorization_endpoint": "https://op.example.org/authorize",
          "token_endpoint": "https://op.example.org/token",
          "jwks_uri": "https://op.example.org/jwks",
          "response_types_supported": ["code"],
          "subject_types_supported": ["public"],
          "id_token_signing_alg_values_supported": ["RS256"],
          "token_endpoint_auth_methods_supported": ["client_secret_basic"]
        }
        """;

    @Test
    public void testInitWiresTokenValidatorAndClientAuthentication() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        val expectedMetadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val resolver = new OidcFederationOpMetadataResolver(configuration) {
            @Override
            protected OIDCProviderMetadata retrieveMetadata() {
                return expectedMetadata;
            }

            @Override
            protected TokenValidator createTokenValidator() {
                // Avoid triggering OidcConfiguration#init() through TokenValidator RSA validators.
                return Mockito.mock(TokenValidator.class);
            }
        };

        val loaded = resolver.load();
        assertSame(expectedMetadata, loaded);

        assertNotNull(resolver.getTokenValidator());

        val auth = resolver.getClientAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof ClientSecretBasic);
    }
}
