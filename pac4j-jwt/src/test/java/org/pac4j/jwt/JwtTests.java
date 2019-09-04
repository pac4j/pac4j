package org.pac4j.jwt;

import com.nimbusds.jose.EncryptionMethod;
import org.junit.Test;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.pac4j.oauth.profile.facebook.FacebookProfileDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

import com.nimbusds.jose.JWSAlgorithm;

import java.security.KeyPair;
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

    private static final Set<String> ROLES = new HashSet<>(Arrays.asList(new String[] {"role1", "role2"}));
    private static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(new String[] {"perm1"}));

    @Test
    public void testGenericJwt() {
        final String token = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..NTvhJXwZ_sN4zYBK.exyLJWkOclCVcffz58CE-"
            + "3XWWV24aYyGWR5HVrfm4HLQi1xgmwglLlEIiFlOSTOSZ_LeAwl2Z3VFh-5EidocjwGkAPGQA_4_KCLbK8Im7M25ZZvDzCJ1kKN1JrDIIrBWCcuI4Mbw0O"
            + "_YGb8TfIECPkpeG7wEgBG30sb1kH-F_vg9yjYfB4MiJCSFmY7cRqN9-9O23tz3wYv3b-eJh5ACr2CGSVNj2KcMsOMJ6bbALgz6pzQTIWk_"
            + "fhcE9QSfaSY7RuZ8cRTV-UTjYgZk1gbd1LskgchS.ijMQmfPlObJv7oaPG8LCEg";
        final TokenCredentials credentials = new TokenCredentials(token);
        final JwtAuthenticator authenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        authenticator.validate(credentials, null);
        assertNotNull(credentials.getUserProfile());
    }

    public void testGenerateAuthenticateSub() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtClaims.SUBJECT, VALUE);
        final String token = generator.generate(profile);
        profile.removeAttribute("sub");
        assertToken(profile, token);
    }

    @Test(expected = CredentialsException.class)
    public void testPlainJwtWithSignatureConfigurations() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPlainJwtWithoutSignatureConfigurations() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator());
    }

    @Test
    public void testPlainJwtNotExpired() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow());
        final String token = generator.generate(claims);
        JwtAuthenticator authenticator = new JwtAuthenticator();
        assertNotNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtExpired() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, yesterday());
        final String token = generator.generate(claims);
        JwtAuthenticator authenticator = new JwtAuthenticator();
        assertNull(authenticator.validateToken(token));
    }

    @Test
    public void testPlainJwtExpiredByAuthenticator() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, ID);
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow());
        final String token = generator.generate(claims);
        JwtAuthenticator authenticator = new JwtAuthenticator();
        final Date expDate = new Date();
        expDate.setHours(-1);
        authenticator.setExpirationTime(expDate);
        assertNull(authenticator.validateToken(token));
    }
    
    @Test
    public void testPlainJwtNoSubject() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        final String token = generator.generate(new HashMap<>());
        JwtAuthenticator authenticator = new JwtAuthenticator();
        TestsHelper.expectException(() -> authenticator.validateToken(token), TechnicalException.class,
            "JWT must contain a subject ('sub' claim)");
    }

    @Test
    public void testPemJwt() throws NoSuchAlgorithmException {
        final FacebookProfile profile = createProfile();
        final ECSignatureConfiguration signatureConfiguration = buildECSignatureConfiguration();
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signatureConfiguration);
        final String token = generator.generate(profile);
        final JwtAuthenticator authenticator = new JwtAuthenticator();
        authenticator.addSignatureConfiguration(signatureConfiguration);
        assertToken(profile, token, authenticator);
    }

    @Test
    public void testGenerateAuthenticate() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testDoubleGenerateAuthenticate() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        final JwtAuthenticator authenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final TokenCredentials credentials = new TokenCredentials(token);
        authenticator.validate(credentials, null);
        final FacebookProfile profile2 = (FacebookProfile) credentials.getUserProfile();
        generator.generate(profile2);
    }

    @Test
    public void testGenerateAuthenticateClaims() {
        final JwtGenerator<JwtProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, VALUE);
        final Date tomorrow = tomorrow();
        claims.put(JwtClaims.EXPIRATION_TIME, tomorrow);
        final String token = generator.generate(claims);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final JwtProfile profile = (JwtProfile) jwtAuthenticator.validateToken(token);
        assertEquals(VALUE, profile.getSubject());
        assertEquals(tomorrow.getTime() / 1000, profile.getExpirationDate().getTime() / 1000);
        final Map<String, Object> claims2 = jwtAuthenticator.validateTokenAndGetClaims(token);
        assertEquals(VALUE, claims2.get(JwtClaims.SUBJECT));
        assertEquals(tomorrow.getTime() / 1000, ((Date) claims2.get(JwtClaims.EXPIRATION_TIME)).getTime() / 1000);
    }

    private Date tomorrow() {
        final Date now = new Date();
        long tomorrow = now.getTime() + 24 * 3600 * 1000;
        return new Date(tomorrow);
    }

    private Date yesterday() {
        final Date now = new Date();
        long tomorrow = now.getTime() - 24 * 3600 * 1000;
        return new Date(tomorrow);
    }

    @Test
    public void testGenerateAuthenticateDifferentSecrets() {
        final SignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(KEY2);
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(signatureConfiguration, encryptionConfiguration));
    }

    @Test
    public void testGenerateAuthenticateUselessSignatureConfiguration() {
        final SignatureConfiguration signatureConfiguration = new SecretSignatureConfiguration(KEY2);
        final SignatureConfiguration signatureConfiguration2 = new SecretSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new SecretEncryptionConfiguration(MAC_SECRET);
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(signatureConfiguration);
        jwtAuthenticator.addSignatureConfiguration(signatureConfiguration2);
        jwtAuthenticator.setEncryptionConfiguration(encryptionConfiguration);
        assertToken(profile, token, jwtAuthenticator);
    }

    @Test
    public void testGenerateAuthenticateSlightlyDifferentSignatureConfiguration() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(KEY2));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(MAC_SECRET));
        final Exception e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("JWT verification failed"));
    }

    @Test
    public void testGenerateAuthenticateDifferentSignatureConfiguration() throws NoSuchAlgorithmException {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(KEY2));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(buildECSignatureConfiguration());
        final Exception e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("No signature algorithm found for JWT:"));
    }

    @Test
    public void testGenerateAuthenticateDifferentEncryptionConfiguration() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        generator.setEncryptionConfiguration(new SecretEncryptionConfiguration(KEY2));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(MAC_SECRET));
        final Exception e = TestsHelper.expectException(() -> assertToken(profile, token, jwtAuthenticator));
        assertTrue(e.getMessage().startsWith("No encryption algorithm found for JWT:"));
    }

    @Test
    public void testGenerateAuthenticateNotEncrypted() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateNotSigned() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        generator.setEncryptionConfiguration(new SecretEncryptionConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedWithRolesPermissions() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        profile.addRoles(ROLES);
        profile.addPermissions(PERMISSIONS);
        final String token = generator.generate(profile);
        final CommonProfile profile2 = assertToken(profile, token);
        assertEquals(ROLES, profile2.getRoles());
        assertEquals(PERMISSIONS, profile2.getPermissions());
    }

    private CommonProfile assertToken(FacebookProfile profile, String token) {
        return assertToken(profile, token, new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET)));
    }

    private CommonProfile assertToken(FacebookProfile profile, String token, JwtAuthenticator authenticator) {
        final TokenCredentials credentials = new TokenCredentials(token);
        authenticator.validate(credentials, null);
        final CommonProfile profile2 = credentials.getUserProfile();
        assertTrue(profile2 instanceof FacebookProfile);
        final FacebookProfile fbProfile = (FacebookProfile) profile2;
        assertEquals(profile.getTypedId(), fbProfile.getTypedId());
        assertEquals(profile.getFirstName(), fbProfile.getFirstName());
        assertEquals(profile.getDisplayName(), fbProfile.getDisplayName());
        assertEquals(profile.getFamilyName(), fbProfile.getFamilyName());
        assertEquals(profile.getVerified(), fbProfile.getVerified());
        return profile2;
    }

    private FacebookProfile createProfile() {
        final FacebookProfile profile = new FacebookProfile();
        profile.setId(ID);
        profile.addAttribute(FacebookProfileDefinition.NAME, NAME);
        profile.addAttribute(FacebookProfileDefinition.VERIFIED, true);
        return profile;
    }

    @Test(expected = CredentialsException.class)
    public void testAuthenticateFailed() {
        final JwtAuthenticator authenticator =
            new JwtAuthenticator(new SecretSignatureConfiguration(MAC_SECRET), new SecretEncryptionConfiguration(MAC_SECRET));
        final TokenCredentials credentials = new TokenCredentials("fakeToken");
        authenticator.validate(credentials, null);
    }

    @Test
    public void testJwtGenerationA256CBC() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET
            + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET),
                new SecretEncryptionConfiguration(KEY2 + KEY2)
        );
        ((SecretEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256CBC_HS512);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    @Test
    public void testJwtGenerationA256GCM() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(
                new SecretSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET
                    + MAC_SECRET + MAC_SECRET),
                new SecretEncryptionConfiguration(MAC_SECRET)
        );
        ((SecretEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256GCM);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    private ECSignatureConfiguration buildECSignatureConfiguration() throws NoSuchAlgorithmException {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        final KeyPair keyPair = keyGen.generateKeyPair();
        return new ECSignatureConfiguration(keyPair, JWSAlgorithm.ES256);
    }
}
