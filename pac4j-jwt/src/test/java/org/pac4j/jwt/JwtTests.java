package org.pac4j.jwt;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.Color;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.util.generator.StaticValueGenerator;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.oauth.profile.facebook.FacebookProfileDefinition;
import org.pac4j.oauth.profile.twitter.TwitterProfile;
import org.pac4j.oauth.profile.twitter.TwitterProfileDefinition;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JwtGenerator} and {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class JwtTests implements TestsConstants {

    private static final String KEY2 = "02ez4f7dsq==drrdz54z---++-6ef78=";

    private static final Set<String> ROLES = new HashSet<>(Arrays.asList(new String[]{"role1", "role2"}));

    @Test
    public void testGenericJwt() {
        val token = """
            eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..NTvhJXwZ_sN4zYBK.exyLJWkOclCVcffz58CE-
            3XWWV24aYyGWR5HVrfm4HLQi1xgmwglLlEIiFlOSTOSZ_LeAwl2Z3VFh-5EidocjwGkAPGQA_4_KCLbK8Im7M25ZZvDzCJ1kKN1JrDIIrBWCcuI4Mbw0O
            _YGb8TfIECPkpeG7wEgBG30sb1kH-F_vg9yjYfB4MiJCSFmY7cRqN9-9O23tz3wYv3b-eJh5ACr2CGSVNj2KcMsOMJ6bbALgz6pzQTIWk_
            fhcE9QSfaSY7RuZ8cRTV-UTjYgZk1gbd1LskgchS.ijMQmfPlObJv7oaPG8LCEg""";
        val credentials = new TokenCredentials(token);
        val authenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        authenticator.validate(credentials, null, null);
        assertNotNull(credentials.getUserProfile());
    }

    @Test
    public void testNestedClaimsJwt() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET));
        var claimsMap = new HashMap<String, Object>();
        claimsMap.put("userData", Map.of("id", "123345", "name", "pac4j"));
        claimsMap.put("iss", "https://pac4j.org");
        claimsMap.put("jti", "JTI");
        claimsMap.put("iat", new Date().getTime());
        claimsMap.put("exp", new Date().getTime() + 10_000);
        claimsMap.put("sub", "pac4j");

        var token = generator.generate(claimsMap);
        val credentials = new TokenCredentials(token);
        val authenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET));
        authenticator.validate(credentials, null, null);
        assertNotNull(credentials.getUserProfile());
        assertTrue(credentials.getUserProfile().containsAttribute("id"));
        assertTrue(credentials.getUserProfile().containsAttribute("name"));
        assertTrue(credentials.getUserProfile().containsAttribute("userData"));
    }

    @Test
    public void testGenerateAuthenticateSub() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET));
        val profile = createProfile();
        profile.addAttribute(JwtClaims.SUBJECT, VALUE);
        val token = generator.generate(profile);
        profile.removeAttribute("sub");
        assertToken(profile, token);
    }

    @Test(expected = CredentialsException.class)
    public void testPlainJwtWithSignatureConfigurations() {
        val generator = new JwtGenerator();
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPlainJwtWithoutSignatureConfigurations() {
        val generator = new JwtGenerator();
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator());
    }

    @Test
    public void testTwitterProfileJwt() {
        val generator = new JwtGenerator();
        val profile = new TwitterProfile();
        profile.addAttribute(TwitterProfileDefinition.PROFILE_LINK_COLOR, new Color(1, 2, 3));
        val token = generator.generate(profile);
        assertNotNull(token);
    }

    @Test
    public void testPlainJwtNotExpired() {
        val generator = new JwtGenerator();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow());
        val token = generator.generate(claims);
        var authenticator = new JwtAuthenticator();
        assertNotNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtExpired() {
        val generator = new JwtGenerator();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, yesterday());
        val token = generator.generate(claims);
        var authenticator = new JwtAuthenticator();
        assertNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtExpired2() {
        val generator = new JwtGenerator();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        generator.setExpirationTime(yesterday());
        val token = generator.generate(claims);
        var authenticator = new JwtAuthenticator();
        assertNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtExpiredByAuthenticator() {
        val generator = new JwtGenerator();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow());
        val token = generator.generate(claims);
        var authenticator = new JwtAuthenticator();
        val expDate = new Date();
        expDate.setHours(-1);
        authenticator.setExpirationTime(expDate);
        assertNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtNoSubject() {
        val generator = new JwtGenerator();
        val token = generator.generate(new HashMap<>());
        var authenticator = new JwtAuthenticator();
        TestsHelper.expectException(() -> authenticator.validateToken(token), TechnicalException.class,
            "The JWT must contain a subject or an id must be generated via the identifierGenerator");
    }

    @Test
    public void testPlainJwtNoSubjectButIdentifierGenerator() {
        val generator = new JwtGenerator();
        val token = generator.generate(new HashMap<>());
        var authenticator = new JwtAuthenticator();
        authenticator.setIdentifierGenerator(new StaticValueGenerator(VALUE));
        val profile = authenticator.validateToken(token);
        assertEquals(VALUE, profile.getId());
    }

    @Test
    public void testPemJwt() throws NoSuchAlgorithmException {
        val profile = createProfile();
        val signatureConfiguration = buildECSignatureConfiguration();
        val generator = new JwtGenerator(signatureConfiguration);
        val token = generator.generate(profile);
        val authenticator = new JwtAuthenticator();
        authenticator.addSignatureConfiguration(signatureConfiguration);
        assertToken(profile, token, authenticator);
    }

    @Test
    public void testGenerateAuthenticate() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateWithoutEncryption() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET));
        val profile = createProfile();
        profile.setLinkedId(VALUE);
        val token = generator.generate(profile);
        assertToken(profile, token);
        assertEquals(VALUE, profile.getLinkedId());
    }

    @Test
    public void testDoubleGenerateAuthenticate() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        val profile = createProfile();
        val token = generator.generate(profile);
        val authenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        val credentials = new TokenCredentials(token);
        authenticator.validate(credentials, null, null);
        val profile2 = (FacebookProfile) credentials.getUserProfile();
        generator.generate(profile2);
    }

    @Test
    public void testGenerateAuthenticateClaims() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, VALUE);
        val tomorrow = tomorrow();
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow);
        val token = generator.generate(claims);
        val jwtAuthenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        val profile = (JwtProfile) jwtAuthenticator.validateToken(token);
        assertEquals(VALUE, profile.getSubject());
        assertEquals(tomorrow.getTime() / 1000, profile.getExpirationDate().getTime() / 1000);
        val claims2 = jwtAuthenticator.validateTokenAndGetClaims(token);
        assertEquals(VALUE, claims2.get(JwtClaims.SUBJECT));
        assertEquals(tomorrow.getTime() / 1000, ((Date) claims2.get(JwtClaims.EXPIRATION_TIME)).getTime() / 1000);
    }

    private Date tomorrow() {
        val now = new Date();
        var tomorrow = now.getTime() + 24 * 3600 * 1000;
        return new Date(tomorrow);
    }

    private Date yesterday() {
        val now = new Date();
        var yesterday = now.getTime() - 24 * 3600 * 1000;
        return new Date(yesterday);
    }

    @Test
    public void testGenerateAuthenticateDifferentSecrets() {
        final SignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(KEY2);
        val generator = new JwtGenerator(signatureConfiguration, encryptionConfiguration);
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(signatureConfiguration, encryptionConfiguration));
    }

    @Test
    public void testGenerateAuthenticateUselessSignatureConfiguration() {
        final SignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(KEY2);
        final SignatureConfiguration signatureConfiguration2 = new SecretSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(MAC_SECRET);
        val generator = new JwtGenerator(signatureConfiguration, encryptionConfiguration);
        val profile = createProfile();
        val token = generator.generate(profile);
        val jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(signatureConfiguration);
        jwtAuthenticator.addSignatureConfiguration(signatureConfiguration2);
        jwtAuthenticator.setEncryptionConfiguration(encryptionConfiguration);
        assertToken(profile, token, jwtAuthenticator);
    }

    @Test
    public void testGenerateAuthenticateSlightlyDifferentSignatureConfiguration() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(KEY2));
        val profile = createProfile();
        val token = generator.generate(profile);
        val jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(MAC_SECRET));
        val e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("JWT verification failed"));
    }

    @Test
    public void testGenerateAuthenticateDifferentSignatureConfiguration() throws NoSuchAlgorithmException {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(KEY2));
        val profile = createProfile();
        val token = generator.generate(profile);
        val jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(buildECSignatureConfiguration());
        val e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("No signature algorithm found for JWT:"));
    }

    @Test
    public void testGenerateAuthenticateDifferentEncryptionConfiguration() {
        val generator = new JwtGenerator();
        generator.setEncryptionConfiguration(new SecretEncryptionConfiguration(KEY2));
        val profile = createProfile();
        val token = generator.generate(profile);
        val jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(MAC_SECRET));
        val e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("No encryption algorithm found for JWT:"));
    }

    @Test
    public void testGenerateAuthenticateNotEncrypted() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET));
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateNotSigned() {
        val generator = new JwtGenerator();
        generator.setEncryptionConfiguration(new SecretEncryptionConfiguration(MAC_SECRET));
        val profile = createProfile();
        val token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedWithRoles() {
        val generator = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET));
        val profile = createProfile();
        profile.addRoles(ROLES);
        val token = generator.generate(profile);
        val profile2 = assertToken(profile, token);
        assertEquals(ROLES, profile2.getRoles());
    }

    private UserProfile assertToken(FacebookProfile profile, String token) {
        return assertToken(profile, token, new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET)));
    }

    private UserProfile assertToken(FacebookProfile profile, String token, JwtAuthenticator authenticator) {
        val credentials = new TokenCredentials(token);
        authenticator.validate(credentials, null, null);
        val profile2 = credentials.getUserProfile();
        assertTrue(profile2 instanceof FacebookProfile);
        val fbProfile = (FacebookProfile) profile2;
        assertEquals(profile.getTypedId(), fbProfile.getTypedId());
        assertEquals(profile.getFirstName(), fbProfile.getFirstName());
        assertEquals(profile.getDisplayName(), fbProfile.getDisplayName());
        assertEquals(profile.getFamilyName(), fbProfile.getFamilyName());
        assertEquals(profile.getVerified(), fbProfile.getVerified());
        return profile2;
    }

    private FacebookProfile createProfile() {
        val profile = new FacebookProfile();
        profile.setId(ID);
        profile.addAttribute(FacebookProfileDefinition.NAME, NAME);
        profile.addAttribute(FacebookProfileDefinition.VERIFIED, true);
        return profile;
    }

    @Test(expected = CredentialsException.class)
    public void testAuthenticateFailed() {
        val authenticator =
            new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET), new SecretEncryptionConfiguration(MAC_SECRET));
        val credentials = new TokenCredentials("fakeToken");
        authenticator.validate(credentials, null, null);
    }

    @Test
    public void testJwtGenerationA256CBC() {
        val g = new JwtGenerator(new SecretSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET
            + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET),
            new SecretEncryptionConfiguration(KEY2 + KEY2)
        );
        ((SecretEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256CBC_HS512);
        val g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    @Test
    public void testJwtGenerationA256GCM() {
        val g = new JwtGenerator(
            new SecretSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET
                + MAC_SECRET + MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET)
        );
        ((SecretEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256GCM);
        val g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    private ECSignatureConfiguration buildECSignatureConfiguration() throws NoSuchAlgorithmException {
        val keyGen = KeyPairGenerator.getInstance("EC");
        val keyPair = keyGen.generateKeyPair();
        return new ECSignatureConfiguration(keyPair, JWSAlgorithm.ES256);
    }
}
