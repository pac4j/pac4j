package org.pac4j.core.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyStore;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests {@link JwkHelper}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public final class JwkHelperTests {

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
    public void testSaveJwkPrivatePersistsPrivateMaterial() throws Exception {
        val jwksPath = Files.createTempDirectory("jwks-helper-tests").resolve("saved-private.jwks");
        Files.deleteIfExists(jwksPath);

        val key = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("private-kid").generate();
        JwkHelper.saveJwkPrivate(key, jwksPath.toString());
        val content = Files.readString(jwksPath);
        assertTrue(content.contains("\n"));

        val jwkSet = JWKSet.load(jwksPath.toFile());
        assertEquals(1, jwkSet.getKeys().size());
        val savedKey = (RSAKey) jwkSet.getKeys().get(0);
        assertEquals("private-kid", savedKey.getKeyID());
        assertNotNull(savedKey.getPrivateExponent());
        assertTrue(savedKey.isPrivate());
        assumeTrue(Files.getFileStore(jwksPath).supportsFileAttributeView("posix"));
        assertEquals(PosixFilePermissions.fromString("rw-------"), Files.getPosixFilePermissions(jwksPath));
    }

    @Test
    public void testSaveJwkPublicPersistsOnlyPublicMaterial() throws Exception {
        val jwksPath = Files.createTempDirectory("jwks-helper-tests").resolve("saved-public.jwks");
        Files.deleteIfExists(jwksPath);

        val key = new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID("public-kid").generate();
        JwkHelper.saveJwkPublic(key, jwksPath.toString());
        val content = Files.readString(jwksPath);
        assertTrue(content.contains("\n"));

        val jwkSet = JWKSet.load(jwksPath.toFile());
        assertEquals(1, jwkSet.getKeys().size());
        val savedKey = (RSAKey) jwkSet.getKeys().get(0);
        assertEquals("public-kid", savedKey.getKeyID());
        assertNull(savedKey.getPrivateExponent());
        assertFalse(savedKey.isPrivate());
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

    /** A throwaway PKCS#12 holding an EC key and its certificate, as a certification authority hands over. */
    private static final String KEYSTORE =
            "MIIDowIBAzCCA2kGCSqGSIb3DQEHAaCCA1oEggNWMIIDUjCCAi8GCSqGSIb3DQEHBqCCAiAwggIcAgEAMIICFQYJKoZIhvcNAQcB"
            + "MBwGCiqGSIb3DQEMAQYwDgQIVA8mkRqhhTECAggAgIIB6PHnj1qkGIl1REc2F2oqhUDZ+PWGvixNUvDpYB9GXVtscZ0t/UgHYIeU"
            + "LF/DxugGJt6sKWTJ8ztwBurZNEJiNkYvsCHHF7tsYlbWgOfiWfWlxWbuwSLSvTFqzbSqxUO9zLhA6aCN3soKB9rzD8r6Fy0UbBek"
            + "p1m4BMsxfxYezfBSDeZzE7pmi9dQELnxhQnqsAl4igbdcE7wddnrsEDMfBXQwm6uhETuiRo1lk//xy9OAyuaA/ITZc2WUT3CGn9A"
            + "RtulSkKBlocOIAjkj0/Z1a1rVG8xzKd94UuG4GMHtvg60mP6rUI/zyxcsC4qwC4Xn/CV6TwT1OIAL4zSixOWK63UZC9B900wKlXl"
            + "FtWOi15rX7lMHJfrS3xpjvFY2CHSvEONfhL1sgSXB7mNeXx3HXq/Sn9lV9AV2OtR0PBi3zhRm2JfHRwsxvm6Wmd/k/7fqrNxqTA9"
            + "Xb5G9F9clzpklGrG1QDu5kBEir4P5bn18hrZddyvaRo0Y7YYEp0Pt7y529NlF2dr3S1LNaC/bBzFPi2+qLCzoLtq4KjK/IdgXfwZ"
            + "d1NjJpMQPKgkjytWgdqqSyFosodW95Rsnu3/qPXEl+DeKQU62oSi7yCC1NhxAtan6GdoSzUQLhPcBjp7DxjkwTDahlw4+skkMIIB"
            + "GwYJKoZIhvcNAQcBoIIBDASCAQgwggEEMIIBAAYLKoZIhvcNAQwKAQKggbQwgbEwHAYKKoZIhvcNAQwBAzAOBAig6ufP1iTzvgIC"
            + "CAAEgZB24kmq8faO0vZobwtL/auHpOndLaF8bsw41fqc3wgiEDTwAYMA6FiHbx3Dq+aPT9257BIqfeP2yWhJtfGax9kKHvhyjXGq"
            + "IWM0oUXSMFOKP02r5kkfeMy42xyRCnFC/s5ZzZfPu2QaqfToFaZiV3u0LE9CSn8gHM8Y7unpv8V1mDwjjnHU3YYmsrcPFDf5GdIx"
            + "OjATBgkqhkiG9w0BCRQxBh4EAHIAcDAjBgkqhkiG9w0BCRUxFgQUticB+ID6pw9EQ3iuJy3jeUWQ2c8wMTAhMAkGBSsOAwIaBQAE"
            + "FEG/2ypRxiMtrRoZOP5RkHm+D4tDBAgyvwPkK7zf0AICCAA=";

    private static final char[] PASSWORD = "changeit".toCharArray();

    private static KeyStore testKeyStore() throws Exception {
        val keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(java.util.Base64.getDecoder().decode(KEYSTORE)), PASSWORD);
        return keyStore;
    }

    @Test
    public void testGenerateAnEcKeyForAnEcAlgorithm() {
        val key = JwkHelper.generateKey(JWSAlgorithm.ES256, "kid-ec");

        val ecKey = assertInstanceOf(ECKey.class, key);
        assertEquals(Curve.P_256, ecKey.getCurve());
        assertEquals("kid-ec", ecKey.getKeyID());
        assertTrue(ecKey.isPrivate());
        // the algorithm is carried by the key, so that it never has to be configured twice
        assertEquals(JWSAlgorithm.ES256, JwkHelper.determineAlgorithm(ecKey, false));
    }

    @Test
    public void testGenerateWithoutAnyKeyIdentifier() {
        val key = JwkHelper.generateKey(JWSAlgorithm.ES256);

        assertNull(key.getKeyID());
        assertEquals(JWSAlgorithm.ES256, JwkHelper.determineAlgorithm(key, false));
    }

    @Test
    public void testGenerateAnRsaKeyForAnRsaAlgorithm() {
        val key = JwkHelper.generateKey(JWSAlgorithm.RS256, "kid-rsa");

        assertInstanceOf(RSAKey.class, key);
        assertEquals("kid-rsa", key.getKeyID());
    }

    @Test
    public void testGenerateRefusesAnUnsupportedAlgorithm() {
        TestsHelper.expectException(() -> JwkHelper.generateKey(JWSAlgorithm.HS256, "kid"),
            TechnicalException.class, "Cannot generate a key for the algorithm: HS256");
    }

    /** A key carrying its certificate chain, exactly as JWK.load returns it from a keystore. */
    private static JWK keyWithCertificateChain() throws Exception {
        return JWK.load(testKeyStore(), "rp", PASSWORD);
    }

    @Test
    public void testBuildSignedJwtPublishesTheCertificateChainOfTheKey() throws Exception {
        val key = keyWithCertificateChain();
        val claims = new JWTClaimsSet.Builder().subject("sub").build();

        val jwt = SignedJWT.parse(JwkHelper.buildSignedJwt(claims, key, JWSAlgorithm.ES256,
            "oauth-authz-req+jwt", true));

        assertEquals("oauth-authz-req+jwt", jwt.getHeader().getType().toString());
        assertEquals(1, jwt.getHeader().getX509CertChain().size());
        assertArrayEquals(testKeyStore().getCertificate("rp").getEncoded(),
            jwt.getHeader().getX509CertChain().get(0).decode());
        assertTrue(jwt.verify(new ECDSAVerifier(((ECKey) key).toPublicJWK())));
    }

    @Test
    public void testBuildSignedJwtLeavesTheCertificateChainOutWhenNotAsked() throws Exception {
        val jwt = SignedJWT.parse(JwkHelper.buildSignedJwt(new JWTClaimsSet.Builder().build(),
            keyWithCertificateChain(), JWSAlgorithm.ES256, "oauth-authz-req+jwt", false));

        assertNull(jwt.getHeader().getX509CertChain());
    }

    @Test
    public void testBuildSignedJwtRefusesAChainTheKeyDoesNotCarry() {
        val key = JwkHelper.generateKey(JWSAlgorithm.ES256, "kid-ec");

        TestsHelper.expectException(() -> JwkHelper.buildSignedJwt(new JWTClaimsSet.Builder().build(), key,
            JWSAlgorithm.ES256, "oauth-authz-req+jwt", true),
            TechnicalException.class, "No certificate chain in the signing key: kid-ec");
    }

    @Test
    public void testResolveTheSigningKeyFromTheJwks(@TempDir final java.nio.file.Path directory) {
        val jwks = new JwksProperties()
            .setJwksResource(new FileSystemResource(directory.resolve("keys.jwks").toFile()));

        val key = JwkHelper.resolveSigningKey(jwks, null, JWSAlgorithm.ES256);

        // the resource did not exist: a key was created for the requested algorithm, and saved
        assertInstanceOf(ECKey.class, key);
        assertEquals(Curve.P_256, ((ECKey) key).getCurve());
        assertTrue(directory.resolve("keys.jwks").toFile().exists());
    }

    @Test
    public void testResolveTheSigningKeyWithoutAnySource() {
        TestsHelper.expectException(() -> JwkHelper.resolveSigningKey(null, null),
            TechnicalException.class, "A JWKS or a keystore is mandatory to get the signing key");
    }
}
