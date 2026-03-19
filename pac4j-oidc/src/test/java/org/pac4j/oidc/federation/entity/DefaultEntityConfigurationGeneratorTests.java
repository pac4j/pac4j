package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.federation.config.OidcFederationProperties;
import org.pac4j.oidc.metadata.IOidcOpMetadataResolver;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link DefaultEntityConfigurationGenerator}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class DefaultEntityConfigurationGeneratorTests {
    @TempDir
    public Path tmp;

    private static DefaultEntityConfigurationGenerator newGenerator(OidcFederationProperties federation, String callbackUrl) {
        val config = new OidcConfiguration();
        config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
        config.setFederation(federation);
        val opResolver = mock(IOidcOpMetadataResolver.class);
        config.setOpMetadataResolver(opResolver);
        val clientAuth = mock(PrivateKeyJWT.class);
        when(clientAuth.getMethod()).thenReturn(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
        when(opResolver.getClientAuthentication()).thenReturn(clientAuth);

        val client = mock(OidcClient.class);
        when(client.getConfiguration()).thenReturn(config);
        when(client.getCallbackUrl()).thenReturn(callbackUrl);

        return new DefaultEntityConfigurationGenerator(client);
    }

    @Test
    public void testGetContentType() {
        val federation = new OidcFederationProperties();
        val generator = newGenerator(federation, "https://client.example.org/callback");
        assertEquals("application/entity-statement+jwt", generator.getContentType());
    }
    @Test
    public void testGenerateWithoutJwksAndWithoutKeystore() {
        val federation = new OidcFederationProperties();
        federation.setJwks(null);
        federation.setKeystore(null);

        val generator = newGenerator(federation, "https://client.example.org/callback");
        assertThrows(TechnicalException.class, generator::generate);
    }

    @Test
    public void testGenerateWithJwksNotExistingCreatesFileAndDefaultsEntityIdToCallbackUrl() throws Exception {
        val jwksFile = tmp.resolve("entity.jwks");
        assertFalse(Files.exists(jwksFile));

        val callbackUrl = "https://client.example.org/callback";

        val federation = new OidcFederationProperties();
        federation.getJwks().setJwksResource(new FileSystemResource(jwksFile.toFile()));
        federation.setEntityId(callbackUrl);

        val generator = newGenerator(federation, callbackUrl);

        val serializedJwt = generator.generate();
        assertTrue(Files.exists(jwksFile));

        val jwkSetFromFile = JWKSet.parse(Files.readString(jwksFile));
        assertEquals(1, jwkSetFromFile.getKeys().size());
        assertTrue(jwkSetFromFile.getKeys().get(0).isPrivate());

        val signed = SignedJWT.parse(serializedJwt);
        val claims = signed.getJWTClaimsSet();
        assertEquals(callbackUrl, claims.getIssuer());
        assertEquals(callbackUrl, claims.getSubject());
        assertTrue(claims.getAudience().isEmpty());

        assertEquals("entity-statement+jwt", signed.getHeader().getType().toString());
        assertJwksClaimContainsSinglePublicKey(claims.getClaim("jwks"));
        assertRedirectUrisClaim(claims.getClaim("metadata"), callbackUrl);
    }

    @Test
    public void testGenerateWithJwksExistingUsesKidAndUsesConfiguredEntityId() throws Exception {
        val jwksFile = tmp.resolve("existing.jwks");

        val federation = new OidcFederationProperties();
        federation.getJwks().setJwksResource(new FileSystemResource(jwksFile.toFile()));
        federation.getJwks().setKid("my-kid");
        federation.setEntityId("https://entity.example.org");

        val signingKey = new OctetKeyPairGenerator(Curve.Ed25519)
            .keyID("my-kid")
            .keyUse(KeyUse.SIGNATURE)
            .generate();
        Files.writeString(jwksFile, new JWKSet(signingKey).toString(false));

        val jwksOriginal = Files.readString(jwksFile);

        val callbackUrl = "https://client.example.org/callback";
        val generator = newGenerator(federation, callbackUrl);

        val serializedJwt = generator.generate();

        assertEquals(jwksOriginal, Files.readString(jwksFile));

        val signed = SignedJWT.parse(serializedJwt);
        val claims = signed.getJWTClaimsSet();
        assertEquals("https://entity.example.org", claims.getIssuer());
        assertEquals("https://entity.example.org", claims.getSubject());
        assertTrue(claims.getAudience().isEmpty());

        assertEquals("my-kid", signed.getHeader().getKeyID());
        assertJwksClaimContainsSinglePublicKey(claims.getClaim("jwks"));
        assertRedirectUrisClaim(claims.getClaim("metadata"), callbackUrl);
    }

    @Test
    public void testGenerateIncludesFederationPropertiesInJwtClaims() throws Exception {
        val jwksFile = tmp.resolve("props.jwks");

        val federation = new OidcFederationProperties();
        federation.getJwks().setJwksResource(new FileSystemResource(jwksFile.toFile()));

        federation.setEntityId("https://entity.example.org");
        federation.setValidityInDays(7);
        federation.setApplicationType("native");
        federation.setResponseTypes(List.of("code", "id_token"));
        federation.setGrantTypes(List.of("authorization_code", "refresh_token"));
        federation.setScopes(List.of("openid", "email"));
        federation.setContactName("pac4j test client");
        federation.setContactEmails(List.of("ops@example.org", "security@example.org"));

        val callbackUrl = "https://client.example.org/callback";
        val generator = newGenerator(federation, callbackUrl);

        val serializedJwt = generator.generate();
        val signed = SignedJWT.parse(serializedJwt);
        val claims = signed.getJWTClaimsSet();

        assertEquals("https://entity.example.org", claims.getIssuer());

        val validityMs = 7L * 24 * 60 * 60 * 1000L;
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertEquals(validityMs, claims.getExpirationTime().getTime() - claims.getIssueTime().getTime());

        assertRelyingPartyMetadata(
            claims.getClaim("metadata"),
            callbackUrl,
            "native",
            List.of("code", "id_token"),
            List.of("authorization_code", "refresh_token"),
            "openid email",
            ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue());

        val metadataClaim = (Map<String, Object>) claims.getClaim("metadata");
        assertNotNull(metadataClaim);
        val openIdRelyingParty = (Map<String, Object>) metadataClaim.get("openid_relying_party");
        assertNotNull(openIdRelyingParty);
        assertEquals("pac4j test client", openIdRelyingParty.get("client_name"));
        assertEquals(List.of("ops@example.org", "security@example.org"), openIdRelyingParty.get("contacts"));
    }

    @Test
    public void testGenerateWithKeystoreNotExistingGeneratesKeystore() throws Exception {
        val keystoreFile = tmp.resolve("signing.jks");
        assertFalse(Files.exists(keystoreFile));

        val federation = new OidcFederationProperties();
        federation.getKeystore().setKeystoreResource(new FileSystemResource(keystoreFile.toFile()));
        federation.getKeystore().setKeystorePassword("storepass");
        federation.getKeystore().setPrivateKeyPassword("keypass");

        val generatorSpy = spy((FileSystemKeystoreGenerator) federation.getKeystore().getKeystoreGenerator());
        federation.getKeystore().setKeystoreGenerator(generatorSpy);

        val callbackUrl = "https://client.example.org/callback";
        val generator = newGenerator(federation, callbackUrl);

        val serializedJwt = generator.generate();
        assertNotNull(serializedJwt);

        verify(generatorSpy).generate();

        assertTrue(Files.exists(keystoreFile));
        assertTrue(Files.exists(tmp.resolve("oidcfede-signing-cert.pem")));
        assertTrue(Files.exists(tmp.resolve("oidcfede-signing-cert.crt")));
        assertTrue(Files.exists(tmp.resolve("oidcfede-signing-cert.key")));

        val signed = SignedJWT.parse(serializedJwt);
        assertEquals("entity-statement+jwt", signed.getHeader().getType().toString());
        assertJwksClaimContainsSinglePublicKey(signed.getJWTClaimsSet().getClaim("jwks"));
    }

    @Test
    public void testGenerateWithKeystoreExistingDoesNotRegenerateKeystore() throws Exception {
        val keystoreFile = tmp.resolve("existing.jks");

        val federation = new OidcFederationProperties();
        federation.getKeystore().setKeystoreResource(new FileSystemResource(keystoreFile.toFile()));
        federation.getKeystore().setKeystorePassword("storepass");
        federation.getKeystore().setPrivateKeyPassword("keypass");

        val generatorSpy = spy(new FileSystemKeystoreGenerator(federation.getKeystore()));
        federation.getKeystore().setKeystoreGenerator(generatorSpy);

        generatorSpy.generate();
        reset(generatorSpy);

        assertTrue(Files.exists(keystoreFile));

        val callbackUrl = "https://client.example.org/callback";
        val generator = newGenerator(federation, callbackUrl);

        val serializedJwt = generator.generate();
        assertNotNull(serializedJwt);

        verify(generatorSpy, never()).generate();

        val signed = SignedJWT.parse(serializedJwt);
        assertEquals("entity-statement+jwt", signed.getHeader().getType().toString());
        assertJwksClaimContainsSinglePublicKey(signed.getJWTClaimsSet().getClaim("jwks"));
    }

    @Test
    public void testBuildConfigRequiresPrivateKeyPart() throws Exception {
        val federation = new OidcFederationProperties();
        val generator = newGenerator(federation, "https://client.example.org/callback");

        val signingKey = new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("kid")
            .generate();

        val publicOnly = signingKey.toPublicJWK();
        assertThrows(TechnicalException.class, () -> generator.buildConfig(publicOnly));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildConfigGeneratesSignedJwtWithExpectedClaimsAndPublicJwks() throws Exception {
        val federation = new OidcFederationProperties();
        federation.setEntityId("https://entity.example.org");
        federation.setValidityInDays(7);
        federation.setApplicationType("native");
        federation.setResponseTypes(List.of("code", "id_token"));
        federation.getGrantTypes().add("refresh_token");
        federation.setScopes(List.of("openid", "email"));
        federation.setContactName("pac4j buildConfig client");
        federation.setContactEmails(List.of("build@example.org"));

        val callbackUrl = "https://client.example.org/callback";
        val generator = newGenerator(federation, callbackUrl);

        val signingKey = new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("kid")
            .generate();

        val serializedJwt = generator.buildConfig(signingKey);
        val signed = SignedJWT.parse(serializedJwt);
        val claims = signed.getJWTClaimsSet();

        assertEquals(DefaultEntityConfigurationGenerator.ENTITY_STATEMENT_TYPE, signed.getHeader().getType().toString());
        assertEquals("kid", signed.getHeader().getKeyID());

        assertEquals("https://entity.example.org", claims.getIssuer());
        assertEquals("https://entity.example.org", claims.getSubject());
        assertNotNull(claims.getJWTID());
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getNotBeforeTime());
        assertNotNull(claims.getExpirationTime());
        assertEquals(claims.getIssueTime(), claims.getNotBeforeTime());
        assertEquals(7L * 24 * 60 * 60 * 1000L,
            claims.getExpirationTime().getTime() - claims.getIssueTime().getTime());
        assertNull(claims.getClaim("authority_hints"));

        val metadataClaim = (Map<String, Object>) claims.getClaim("metadata");
        assertNotNull(metadataClaim);
        val openIdRelyingParty = (Map<String, Object>) metadataClaim.get("openid_relying_party");
        assertNotNull(openIdRelyingParty);
        assertEquals(List.of(callbackUrl), openIdRelyingParty.get("redirect_uris"));
        assertEquals("native", openIdRelyingParty.get("application_type"));
        assertEquals(List.of("code", "id_token"), openIdRelyingParty.get("response_types"));
        assertEquals(List.of("authorization_code", "refresh_token"), openIdRelyingParty.get("grant_types"));
        assertEquals("openid email", openIdRelyingParty.get("scope"));
        assertEquals(ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue(),
            openIdRelyingParty.get("token_endpoint_auth_method"));
        assertEquals(List.of("explicit", "automatic"), openIdRelyingParty.get("client_registration_types"));
        assertEquals("pac4j buildConfig client", openIdRelyingParty.get("client_name"));
        assertEquals(List.of("build@example.org"), openIdRelyingParty.get("contacts"));

        val jwksClaim = (Map<String, Object>) claims.getClaim("jwks");
        assertNotNull(jwksClaim);

        val keys = (List<Map<String, Object>>) jwksClaim.get("keys");
        assertNotNull(keys);
        assertEquals(1, keys.size());

        // The published JWKS must only expose the public key.
        assertEquals("kid", keys.get(0).get("kid"));
        assertFalse(keys.get(0).containsKey("d"));
    }

    @SuppressWarnings("unchecked")
    private static void assertJwksClaimContainsSinglePublicKey(final Object jwksClaim) {
        assertNotNull(jwksClaim);
        val jwksMap = (Map<String, Object>) jwksClaim;
        val keys = (List<Map<String, Object>>) jwksMap.get("keys");
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertFalse(keys.get(0).containsKey("d"));
    }

    @SuppressWarnings("unchecked")
    private static void assertRedirectUrisClaim(final Object metadataClaim, final String expectedCallbackUrl) {
        assertNotNull(metadataClaim);
        val metadata = (Map<String, Object>) metadataClaim;

        val openIdRelyingParty = (Map<String, Object>) metadata.get("openid_relying_party");
        assertNotNull(openIdRelyingParty);

        val redirectUris = (List<String>) openIdRelyingParty.get("redirect_uris");
        assertEquals(List.of(expectedCallbackUrl), redirectUris);
    }

    @SuppressWarnings("unchecked")
    private static void assertRelyingPartyMetadata(final Object metadataClaim,
                                                   final String expectedCallbackUrl,
                                                   final String expectedApplicationType,
                                                   final List<String> expectedResponseTypes,
                                                   final List<String> expectedGrantTypes,
                                                   final String expectedScope,
                                                   final String expectedTokenEndpointAuthMethod) {
        assertNotNull(metadataClaim);
        val metadata = (Map<String, Object>) metadataClaim;

        val openIdRelyingParty = (Map<String, Object>) metadata.get("openid_relying_party");
        assertNotNull(openIdRelyingParty);

        assertEquals(List.of(expectedCallbackUrl), openIdRelyingParty.get("redirect_uris"));
        assertEquals(expectedApplicationType, openIdRelyingParty.get("application_type"));
        assertEquals(expectedResponseTypes, openIdRelyingParty.get("response_types"));
        assertEquals(expectedGrantTypes, openIdRelyingParty.get("grant_types"));
        assertEquals(expectedScope, openIdRelyingParty.get("scope"));
        assertEquals(expectedTokenEndpointAuthMethod, openIdRelyingParty.get("token_endpoint_auth_method"));
    }
}
