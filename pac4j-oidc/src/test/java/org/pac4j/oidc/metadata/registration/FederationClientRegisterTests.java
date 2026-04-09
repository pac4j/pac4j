package org.pac4j.oidc.metadata.registration;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.federation.entity.EntityConfigurationGenerator;
import org.pac4j.test.web.ServerResponse;
import org.pac4j.test.web.WebServer;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link FederationClientRegister}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class FederationClientRegisterTests {

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

    private JWKSet federationJWKS;

    private FederationClientRegister register;

    @TempDir
    Path tmp;

    @BeforeEach
    public void setUp() {
        federationJWKS = new JWKSet();
        register = new FederationClientRegister();
    }

    @Test
    public void testAutomaticRegistrationSetsClientIdFromEntityId() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.getFederation().setEntityId("https://rp.example.org");
        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.AUTOMATIC));

        register.register(configuration, metadata, federationJWKS);

        assertEquals("https://rp.example.org", configuration.getClientId());
    }

    @Test
    public void testAutomaticRegistrationDoesNotSetClientIdWhenRpDoesNotSupportAutomatic() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.getFederation().setEntityId("https://rp.example.org");
        configuration.getFederation().setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT.getValue()));
        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.AUTOMATIC));

        register.register(configuration, metadata, federationJWKS);

        assertNull(configuration.getClientId());
    }

    @Test
    public void testExplicitRegistrationIsNotTriggeredWhenRpDoesNotSupportExplicit() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.getFederation().setEntityId("https://rp.example.org");
        configuration.getFederation().setClientRegistrationTypes(List.of(ClientRegistrationType.AUTOMATIC.getValue()));
        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        configuration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);
        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT));

        register.register(configuration, metadata, federationJWKS);

        assertNull(configuration.getClientId());
        Mockito.verifyNoInteractions(entityConfigurationGenerator);
    }

    @Test
    public void testAutomaticRegistrationIsPreferredWhenBothRegistrationTypesAreSupported() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.getFederation().setEntityId("https://rp.example.org");
        configuration.getFederation().setClientRegistrationTypes(List.of(
            ClientRegistrationType.EXPLICIT.getValue(),
            ClientRegistrationType.AUTOMATIC.getValue()));
        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        configuration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);
        val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
        metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT, ClientRegistrationType.AUTOMATIC));

        register.register(configuration, metadata, federationJWKS);

        assertEquals("https://rp.example.org", configuration.getClientId());
        Mockito.verifyNoInteractions(entityConfigurationGenerator);
    }

    @Test
    public void testPerformExplicitRegistrationSetsClientId() throws Exception {
        val secretFile = tmp.resolve("federation-secret-for-client-id-test.txt");
        val configuration = new OidcConfiguration();
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        configuration.getFederation().setEntityId("https://rp.example.org");
        configuration.getFederation().setSecretExportFile(secretFile.toString());

        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        Mockito.when(entityConfigurationGenerator.getContentType()).thenReturn("application/entity-statement+jwt");
        Mockito.when(entityConfigurationGenerator.generateEntityStatement()).thenReturn("entity-configuration");
        configuration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);

        val signingKey = new RSAKeyGenerator(2048).keyID("registration-key").generate();
        val registrationResponse = buildRegistrationResponseJwt(signingKey, "registeredClient", "registeredSecret");

        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(WebServer.Response.Status.CREATED,
                "application/entity-statement+jwt", registrationResponse));
        webServer.start();
        try {
            val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
            metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT));
            metadata.setFederationRegistrationEndpointURI(
                new URI("http://localhost:" + webServer.getListeningPort() + "/register?r=ok"));

            register.register(configuration, metadata, new JWKSet(signingKey.toPublicJWK()));

            assertEquals("registeredClient", configuration.getClientId());
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testPerformExplicitRegistrationWithSecretWithoutSecretExportFileReturnsError() throws Exception {
        val configuration = new OidcConfiguration();
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        configuration.getFederation().setEntityId("https://rp.example.org");

        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        Mockito.when(entityConfigurationGenerator.getContentType()).thenReturn("application/entity-statement+jwt");
        Mockito.when(entityConfigurationGenerator.generateEntityStatement()).thenReturn("entity-configuration");
        configuration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);

        val signingKey = new RSAKeyGenerator(2048).keyID("registration-key").generate();
        val registrationResponse = buildRegistrationResponseJwt(signingKey, "registeredClient", "registeredSecret");

        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(WebServer.Response.Status.CREATED,
                "application/entity-statement+jwt", registrationResponse));
        webServer.start();
        try {
            val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
            metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT));
            metadata.setFederationRegistrationEndpointURI(
                new URI("http://localhost:" + webServer.getListeningPort() + "/register?r=ok"));

            val exception = assertThrows(OidcException.class,
                () -> register.register(configuration, metadata, new JWKSet(signingKey.toPublicJWK())));
            assertEquals("Client secret export file is required", exception.getMessage());
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testPerformExplicitRegistrationWithSecretAndSecretExportFileSavesSecret() throws Exception {
        val secretFile = tmp.resolve("federation-secret.txt");
        val configuration = new OidcConfiguration();
        configuration.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        configuration.getFederation().setEntityId("https://rp.example.org");
        configuration.getFederation().setSecretExportFile(secretFile.toString());

        val entityConfigurationGenerator = Mockito.mock(EntityConfigurationGenerator.class);
        Mockito.when(entityConfigurationGenerator.getContentType()).thenReturn("application/entity-statement+jwt");
        Mockito.when(entityConfigurationGenerator.generateEntityStatement()).thenReturn("entity-configuration");
        configuration.getFederation().setEntityConfigurationGenerator(entityConfigurationGenerator);

        val signingKey = new RSAKeyGenerator(2048).keyID("registration-key").generate();
        val registrationResponse = buildRegistrationResponseJwt(signingKey, "registeredClient", "registeredSecret");

        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(WebServer.Response.Status.CREATED,
                "application/entity-statement+jwt", registrationResponse));
        webServer.start();
        try {
            val metadata = OIDCProviderMetadata.parse(METADATA_CLIENT_SECRET_BASIC);
            metadata.setClientRegistrationTypes(List.of(ClientRegistrationType.EXPLICIT));
            metadata.setFederationRegistrationEndpointURI(
                new URI("http://localhost:" + webServer.getListeningPort() + "/register?r=ok"));

            register.register(configuration, metadata, new JWKSet(signingKey.toPublicJWK()));

            assertEquals("registeredClient", configuration.getClientId());
            assertTrue(Files.exists(secretFile));
            assertEquals("registeredSecret", Files.readString(secretFile));
        } finally {
            webServer.stop();
        }
    }

    private static String buildRegistrationResponseJwt(final JWK signingKey,
                                                       final String clientId,
                                                       final String clientSecret) {
        val now = new Date();
        val claims = new JWTClaimsSet.Builder()
            .issuer("https://op.example.org")
            .subject("https://op.example.org")
            .audience("https://rp.example.org")
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 60_000))
            .claim("jwks", new JWKSet(signingKey.toPublicJWK()).toJSONObject())
            .claim("metadata", Map.of("openid_relying_party", Map.of(
                "client_id", clientId,
                "client_secret", clientSecret)))
            .build();
        return JwkHelper.buildSignedJwt(claims, signingKey, "entity-statement+jwt");
    }
}
