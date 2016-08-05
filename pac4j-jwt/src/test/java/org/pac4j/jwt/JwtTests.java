package org.pac4j.jwt;

import com.nimbusds.jose.EncryptionMethod;
import org.junit.Test;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.config.*;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

import com.nimbusds.jose.JWSAlgorithm;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JwtGenerator} and {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class JwtTests implements TestsConstants {

    private static final String KEY2 = "02345678901234567890123456789010";

    private static final Set<String> ROLES = new HashSet<>(Arrays.asList(new String[] { "role1", "role2"}));
    private static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(new String[] { "perm1"}));

    @Test
    public void testGenericJwt() throws HttpAction {
        final String token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDdXN0b20gSldUIEJ1aWxkZXIiLCJpYXQiOjE0NTAxNjQ0NTUsImV4cCI6MTQ4MTcwMDQ1NSwiYXVkIjoiaHR0cHM6Ly9naXRodWIuY29tL3BhYzRqIiwic3ViIjoidXNlckBwYWM0ai5vcmciLCJlbWFpbCI6InVzZXJAcGFjNGoub3JnIn0.zOPb7rbI3IY7iLXTK126Ggu2Q3pNCZsUzzgzgsqR7xU";

        final TokenCredentials credentials = new TokenCredentials(token, JwtAuthenticator.class.getName());
        final JwtAuthenticator authenticator = new JwtAuthenticator(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET));
        authenticator.validate(credentials, null);
        assertNotNull(credentials.getUserProfile());
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateSub() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtClaims.SUBJECT, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateIat() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtClaims.ISSUED_AT, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPlainJwt() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>();
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPemJwt() throws Exception {
        final FacebookProfile profile = createProfile();

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        final KeyPair keyPair = keyGen.generateKeyPair();

        final ECSignatureConfiguration signatureConfiguration = new ECSignatureConfiguration(keyPair, JWSAlgorithm.ES256);
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signatureConfiguration);
        final String token = generator.generate(profile);
        final JwtAuthenticator authenticator = new JwtAuthenticator();
        authenticator.addSignatureConfiguration(signatureConfiguration);
        assertToken(profile, token, authenticator);
    }

    @Test
    public void testGenerateAuthenticate() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateClaims() throws HttpAction {
        final JwtGenerator<JwtProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET));
        final Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, VALUE);
        final Date now = new Date();
        claims.put(JwtClaims.EXPIRATION_TIME, now);
        final String token = generator.generate(claims);
        final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET));
        final JwtProfile profile = (JwtProfile) jwtAuthenticator.validateToken(token);
        assertEquals(VALUE, profile.getSubject());
        assertEquals(now.getTime() / 1000, profile.getExpirationDate().getTime() / 1000);
        final Map<String, Object> claims2 = jwtAuthenticator.validateTokenAndGetClaims(token);
        assertEquals(VALUE, claims2.get(JwtClaims.SUBJECT));
        assertEquals(now.getTime() / 1000, ((Date) claims2.get(JwtClaims.EXPIRATION_TIME)).getTime() / 1000);
    }

    @Test
    public void testGenerateAuthenticateDifferentSecrets() throws HttpAction {
        final SignatureConfiguration signatureConfiguration = new MacSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new DirectEncryptionConfiguration(KEY2);
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signatureConfiguration, encryptionConfiguration);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(signatureConfiguration, encryptionConfiguration));
    }

    @Test
    public void testGenerateAuthenticateUselessSignatureConfiguration() throws HttpAction {
        final SignatureConfiguration signatureConfiguration = new MacSignatureConfiguration(KEY2);
        final SignatureConfiguration signatureConfiguration2 = new MacSignatureConfiguration(MAC_SECRET);
        final EncryptionConfiguration encryptionConfiguration = new DirectEncryptionConfiguration(MAC_SECRET);
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
    public void testGenerateAuthenticateNotEncrypted() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Deprecated
    @Test
    public void testGenerateAuthenticateAndEncrypted() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(MAC_SECRET, MAC_SECRET);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(MAC_SECRET, MAC_SECRET));
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedWithRolesPermissions() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET));
        final FacebookProfile profile = createProfile();
        profile.addRoles(ROLES);
        profile.addPermissions(PERMISSIONS);
        final String token = generator.generate(profile);
        final CommonProfile profile2 = assertToken(profile, token);
        assertEquals(ROLES, profile2.getRoles());
        assertEquals(PERMISSIONS, profile2.getPermissions());
    }

    @Deprecated
    @Test
    public void testGenerateAuthenticateAndEncryptedDifferentKeys() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(MAC_SECRET, KEY2);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(MAC_SECRET, KEY2));
    }

    private CommonProfile assertToken(FacebookProfile profile, String token) throws HttpAction {
        return assertToken(profile, token, new JwtAuthenticator(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET)));
    }

    private CommonProfile assertToken(FacebookProfile profile, String token, JwtAuthenticator authenticator) throws HttpAction {
        final TokenCredentials credentials = new TokenCredentials(token, CLIENT_NAME);
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
        profile.addAttribute(FacebookAttributesDefinition.NAME, NAME);
        profile.addAttribute(FacebookAttributesDefinition.VERIFIED, true);
        return profile;
    }

    @Test(expected = TechnicalException.class)
    public void testAuthenticateFailed() throws HttpAction {
        final JwtAuthenticator authenticator = new JwtAuthenticator(new MacSignatureConfiguration(MAC_SECRET), new DirectEncryptionConfiguration(MAC_SECRET));
        final TokenCredentials credentials = new TokenCredentials("fakeToken", CLIENT_NAME);
        authenticator.validate(credentials, null);
    }
    
    @Test
    public void testJwtGenerationA256CBC() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(new MacSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET),
                new DirectEncryptionConfiguration(KEY2 + KEY2)
        );
        ((DirectEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256CBC_HS512);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    @Test
    public void testJwtGenerationA256GCM() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(
                new MacSignatureConfiguration(MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET + MAC_SECRET),
                new DirectEncryptionConfiguration(MAC_SECRET)
        );
        ((DirectEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256GCM);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }
}
