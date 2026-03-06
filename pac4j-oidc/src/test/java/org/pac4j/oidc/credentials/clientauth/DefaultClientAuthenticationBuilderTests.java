package org.pac4j.oidc.credentials.clientauth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.config.PrivateKeyJWTClientAuthnMethodConfig;
import org.pac4j.oidc.config.method.ClientSecretJwtClientAuthnMethodConfig;
import org.pac4j.oidc.exceptions.OidcUnsupportedClientAuthMethodException;

import java.net.URI;
import java.security.KeyPairGenerator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DefaultClientAuthenticationBuilder}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class DefaultClientAuthenticationBuilderTests {

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

    private static final String METADATA_PRIVATE_KEY_JWT = """
        {
          "issuer": "https://op.example.org",
          "authorization_endpoint": "https://op.example.org/authorize",
          "token_endpoint": "https://op.example.org/token",
          "jwks_uri": "https://op.example.org/jwks",
          "response_types_supported": ["code"],
          "subject_types_supported": ["public"],
          "id_token_signing_alg_values_supported": ["RS256"],
          "token_endpoint_auth_methods_supported": ["private_key_jwt"]
        }
        """;

    private static final String METADATA_CLIENT_SECRET_JWT = """
        {
          "issuer": "https://op.example.org",
          "authorization_endpoint": "https://op.example.org/authorize",
          "token_endpoint": "https://op.example.org/token",
          "jwks_uri": "https://op.example.org/jwks",
          "response_types_supported": ["code"],
          "subject_types_supported": ["public"],
          "id_token_signing_alg_values_supported": ["RS256"],
          "token_endpoint_auth_methods_supported": ["client_secret_jwt"]
        }
        """;

    @Test
    public void testBuildClientAuthnClientSecretBasic() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        builder.buildClientAuthentication();

        val auth = builder.getClientAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof ClientSecretBasic);
    }

    @Test
    public void testBuildClientAuthnThrowsWhenClientAuthNotSupported() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        assertThrows(OidcUnsupportedClientAuthMethodException.class, builder::buildClientAuthentication);
    }

    @Test
    public void testBuildClientAuthnNotDefined() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        builder.buildClientAuthentication();

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, builder.getClientAuthentication().getMethod());
    }

    @Test
    public void testBuildClientAuthnNotDefinedAndNotInClientSupported() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setSupportedClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        val e = assertThrows(OidcUnsupportedClientAuthMethodException.class, () -> {
            builder.buildClientAuthentication();
        });
        assertEquals("None of the Token endpoint provider metadata authentication methods " +
            "are supported: [client_secret_basic]", e.getMessage());
    }

    @Test
    public void testBuildClientAuthnClientSecretJwtWithoutConfig() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_JWT);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        val e = assertThrows(TechnicalException.class, () -> {
            builder.buildClientAuthentication();
        });
        assertEquals("clientSecretJwtConfig cannot be null", e.getMessage());
    }

    @Test
    public void testBuildClientAuthnClientSecretJwtWithConfigNoAudience() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        configuration.setClientSecretJwtClientAuthnMethodConfig(new ClientSecretJwtClientAuthnMethodConfig(null, JWSAlgorithm.HS256));

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_JWT);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        val e = assertThrows(TechnicalException.class, () -> {
            builder.buildClientAuthentication();
        });
        assertEquals("clientSecretJwtConfig.getAudience() cannot be null", e.getMessage());
    }

    @Test
    public void testBuildClientAuthnClientSecretJwtWithConfigNoAlg() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("mySecret");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        configuration.setClientSecretJwtClientAuthnMethodConfig(
            new ClientSecretJwtClientAuthnMethodConfig(new URI("http://audience"), null));

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_JWT);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        val e = assertThrows(TechnicalException.class, () -> {
            builder.buildClientAuthentication();
        });
        assertEquals("clientSecretJwtConfig.getJwsAlgorithm() cannot be null", e.getMessage());
    }

    @Test
    public void testBuildClientAuthnClientSecretJwt() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");
        configuration.setSecret("12345678901234567890123456789012");
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        configuration.setClientSecretJwtClientAuthnMethodConfig(
            new ClientSecretJwtClientAuthnMethodConfig(new URI("http://audience"), JWSAlgorithm.HS256));

        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_JWT);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        builder.buildClientAuthentication();

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_JWT, builder.getClientAuthentication().getMethod());
    }

    @Test
    public void testGetClientAuthnRenewsExpiredPrivateKeyJwtWhenEnabled() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientId("myClient");

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        val privateKey = keyPairGenerator.generateKeyPair().getPrivate();

        val privateKeyJwtConfig = new PrivateKeyJWTClientAuthnMethodConfig();
        privateKeyJwtConfig.setPrivateKey(privateKey);
        privateKeyJwtConfig.setJwsAlgorithm(JWSAlgorithm.RS256);
        privateKeyJwtConfig.setUseExpiration(true);
        privateKeyJwtConfig.setKeyClockSkew(0);
        // Force immediate expiration (in the past) to trigger renewal logic
        privateKeyJwtConfig.setValidity(-10);
        configuration.setPrivateKeyJWTClientAuthnMethodConfig(privateKeyJwtConfig);

        val metadata = OIDCProviderMetadata.parse(METADATA_PRIVATE_KEY_JWT);

        val builder = new DefaultClientAuthenticationBuilder(configuration, metadata);
        builder.buildClientAuthentication();

        val auth1 = builder.getClientAuthentication();
        assertNotNull(auth1);
        assertTrue(auth1 instanceof PrivateKeyJWT);

        val auth2 = builder.getClientAuthentication();
        assertNotNull(auth2);
        assertTrue(auth2 instanceof PrivateKeyJWT);

        // With an always-expired token, each call should renew the PrivateKeyJWT.
        assertNotSame(auth1, auth2);

        val jwtId1 = ((PrivateKeyJWT) auth1).getJWTAuthenticationClaimsSet().getJWTID().toString();
        val jwtId2 = ((PrivateKeyJWT) auth2).getJWTAuthenticationClaimsSet().getJWTID().toString();
        assertNotEquals(jwtId1, jwtId2);
    }
}
