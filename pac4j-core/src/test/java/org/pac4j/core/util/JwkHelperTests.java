package org.pac4j.core.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;

import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link JwkHelper}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class JwkHelperTests {

    @Test
    public void testBuildKidReturnsOriginalKidWhenProvided() {
        assertEquals("my-kid", JwkHelper.buildKid("my-kid"));
    }

    @Test
    public void testBuildKidGeneratesValueWhenMissing() {
        val generated = JwkHelper.buildKid(null);
        assertNotNull(generated);
        assertFalse(generated.isBlank());
    }

    @Test
    public void testLoadJwkFromOrCreateJwksGeneratesAndPersistsKeyWhenMissing() throws Exception {
        val jwksPath = Files.createTempDirectory("jwks-helper-tests").resolve("generated.jwks");
        Files.deleteIfExists(jwksPath);

        val jwksProperties = new JwksProperties();
        jwksProperties.setJwksPath(jwksPath.toString());
        jwksProperties.setKid("generated-kid");

        val signingJwk = JwkHelper.loadJwkFromOrCreateJwks(jwksProperties);

        assertNotNull(signingJwk);
        assertTrue(signingJwk.isPrivate());
        assertEquals("generated-kid", signingJwk.getKeyID());
        assertTrue(Files.exists(jwksPath));

        val jwkSet = JWKSet.load(jwksPath.toFile());
        assertEquals(1, jwkSet.getKeys().size());
        assertEquals("generated-kid", jwkSet.getKeys().get(0).getKeyID());
    }

    @Test
    public void testLoadJwkFromOrCreateJwksSelectsRequestedKid() throws Exception {
        val jwksPath = Files.createTempDirectory("jwks-helper-tests").resolve("existing.jwks");
        Files.deleteIfExists(jwksPath);

        val key1 = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("kid-1").generate();
        val key2 = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("kid-2").generate();
        val jwkSet = new JWKSet(List.of(key1, key2));
        Files.writeString(jwksPath, jwkSet.toString(false));

        val jwksProperties = new JwksProperties();
        jwksProperties.setJwksPath(jwksPath.toString());
        jwksProperties.setKid("kid-2");

        val signingJwk = JwkHelper.loadJwkFromOrCreateJwks(jwksProperties);
        assertEquals("kid-2", signingJwk.getKeyID());
        assertTrue(signingJwk.isPrivate());
    }

    @Test
    public void testDetermineAlgorithmUsesJwkDeclaredAlgorithmWhenPresent() throws Exception {
        val key = new RSAKeyGenerator(2048)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("rsa")
            .algorithm(JWSAlgorithm.RS512)
            .generate();

        assertEquals(JWSAlgorithm.RS512, JwkHelper.determineAlgorithm(key, false));
    }

    @Test
    public void testDetermineAlgorithmFromEcCurve() throws Exception {
        val ecKey = new ECKeyGenerator(Curve.P_384).keyID("ec").generate();
        assertEquals(JWSAlgorithm.ES384, JwkHelper.determineAlgorithm(ecKey, false));
    }

    @Test
    public void testDetermineAlgorithmRejectsSymmetricKeyWhenDisabled() throws Exception {
        val key = new OctetSequenceKeyGenerator(256).keyID("sym").generate();
        val exception = assertThrows(TechnicalException.class, () -> JwkHelper.determineAlgorithm(key, false));
        assertEquals("Symmetric keys (OctetSequenceKey / SecretKey) are not allowed", exception.getMessage());
    }

    @Test
    public void testDetermineSignerBuildsMacSignerForSymmetricKey() throws Exception {
        val key = new OctetSequenceKeyGenerator(256).keyID("sym").generate();
        val signer = JwkHelper.determineSigner(key, true);
        assertNotNull(signer);
        assertTrue(signer instanceof MACSigner);
    }

    @Test
    public void testHasPrivatePartForRsa() throws Exception {
        val key = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("rsa").generate();
        assertTrue(JwkHelper.hasPrivatePart(key));
        assertFalse(JwkHelper.hasPrivatePart(key.toPublicJWK()));
    }

    @Test
    public void testBuildSignedJwtBuildsSignedTokenWithHeadersAndClaims() throws Exception {
        val key = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("kid-rsa").generate();
        val claims = new JWTClaimsSet.Builder()
            .subject("sub")
            .issuer("https://issuer.example.org")
            .build();

        val jwt = JwkHelper.buildSignedJwt(claims, key, "entity-statement+jwt");
        val signedJwt = SignedJWT.parse(jwt);

        assertEquals("entity-statement+jwt", signedJwt.getHeader().getType().toString());
        assertEquals("kid-rsa", signedJwt.getHeader().getKeyID());
        assertEquals("sub", signedJwt.getJWTClaimsSet().getSubject());
        assertTrue(signedJwt.verify(new RSASSAVerifier(key.toPublicJWK())));
    }
}
