package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.generation.FileSystemKeystoreGenerator;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.federation.config.OidcFederationProperties;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the generation of the entity configuration (entity statement JWT)
 * and its related signing material (JWKS / keystore).
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class DefaultEntityConfigurationTests {

    @TempDir
    public Path tmp;

    private static DefaultEntityConfigurationGenerator newGenerator(final OidcFederationProperties federation, final String callbackUrl) {
        val config = new OidcConfiguration();
        config.setFederation(federation);

        val client = mock(OidcClient.class);
        when(client.getConfiguration()).thenReturn(config);
        when(client.getCallbackUrl()).thenReturn(callbackUrl);

        return new DefaultEntityConfigurationGenerator(client);
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

        val federation = new OidcFederationProperties();
        federation.getJwks().setJwksResource(new FileSystemResource(jwksFile.toFile()));
        federation.setEntityId(null);

        val callbackUrl = "https://client.example.org/callback";
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
        assertEquals(List.of(callbackUrl), claims.getAudience());

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
        assertEquals(List.of("https://entity.example.org"), claims.getAudience());

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
        federation.setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

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
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue());
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
