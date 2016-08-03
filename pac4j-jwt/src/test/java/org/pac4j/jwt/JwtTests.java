package org.pac4j.jwt;

import com.nimbusds.jose.EncryptionMethod;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Test;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.config.DirectEncryptionConfiguration;
import org.pac4j.jwt.config.ECSigningConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

import com.nimbusds.jose.JWSAlgorithm;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JwtGenerator} and {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class JwtTests implements TestsConstants {

    private static final String JWT_KEY = "12345678901234567890123456789012";
    private static final String JWT_KEY2 = "02345678901234567890123456789010";

    private static final Set<String> ROLES = new HashSet<>(Arrays.asList(new String[] { "role1", "role2"}));
    private static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(new String[] { "perm1"}));

    @Test
    public void testGenericJwt() throws HttpAction {
        final String token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDdXN0b20gSldUIEJ1aWxkZXIiLCJpYXQiOjE0NTAxNjQ0NTUsImV4cCI6MTQ4MTcwMDQ1NSwiYXVkIjoiaHR0cHM6Ly9naXRodWIuY29tL3BhYzRqIiwic3ViIjoidXNlckBwYWM0ai5vcmciLCJlbWFpbCI6InVzZXJAcGFjNGoub3JnIn0.zOPb7rbI3IY7iLXTK126Ggu2Q3pNCZsUzzgzgsqR7xU";

        final TokenCredentials credentials = new TokenCredentials(token, JwtAuthenticator.class.getName());
        final JwtAuthenticator authenticator = new JwtAuthenticator(JWT_KEY);
        authenticator.validate(credentials, null);
        assertNotNull(credentials.getUserProfile());
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateSub() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtConstants.SUBJECT, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateIat() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtConstants.ISSUE_TIME, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPlainJwt() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>((String) null, null);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testPemJwt() throws Exception {
        final FacebookProfile profile = createProfile();

        KeyPair keyPair = createECKeyPair(EC256SPEC);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
		ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        final ECSigningConfiguration signingConfiguration = new ECSigningConfiguration(privateKey, JWSAlgorithm.ES256);
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(signingConfiguration);
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(getPem("PUBLIC KEY", publicKey.getEncoded()), "EC", null));
    }
    private static final int COFACTOR = 1;
	private static final ECParameterSpec EC256SPEC = new ECParameterSpec(
			new EllipticCurve(
				new ECFieldFp(new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")),
				new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
				new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")),
			new ECPoint(
				new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
				new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")),
			new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
			COFACTOR);
    
	private static KeyPair createECKeyPair(final AlgorithmParameterSpec spec)
			throws Exception {

			// Create the public and private keys
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("EC");
			keyGenerator.initialize(spec);
			return keyGenerator.generateKeyPair();
	}
	private String getPem(String keyTitle, byte[] encodedKey) throws IOException {
		StringWriter writer = new StringWriter();
		PemWriter pemWriter = new PemWriter(writer);
		pemWriter.writeObject(new PemObject(keyTitle, encodedKey));
		pemWriter.flush();
		pemWriter.close();
		return writer.toString();
	}

    @Test
    public void testGenerateAuthenticate() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateNotEncrypted() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, false);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateAndEncrypted() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, JWT_KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(JWT_KEY, JWT_KEY));
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedWithRolesPermissions() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, JWT_KEY);
        final FacebookProfile profile = createProfile();
        profile.addRoles(ROLES);
        profile.addPermissions(PERMISSIONS);
        final String token = generator.generate(profile);
        final CommonProfile profile2 = assertToken(profile, token);
        assertEquals(ROLES, profile2.getRoles());
        assertEquals(PERMISSIONS, profile2.getPermissions());
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedDifferentKeys() throws HttpAction {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, JWT_KEY2);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(JWT_KEY, JWT_KEY2));
    }

    private CommonProfile assertToken(FacebookProfile profile, String token) throws HttpAction {
        return assertToken(profile, token, new JwtAuthenticator(JWT_KEY));
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
        final JwtAuthenticator authenticator = new JwtAuthenticator(JWT_KEY);
        final TokenCredentials credentials = new TokenCredentials("fakeToken", CLIENT_NAME);
        authenticator.validate(credentials, null);
    }
    
    @Test
    public void testJwtGenerationA256CBC() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(
                JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY , 
                JWT_KEY2 + JWT_KEY2
        );
        ((DirectEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256CBC_HS512);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }

    @Test
    public void testJwtGenerationA256GCM() {
        final JwtGenerator<CommonProfile> g = new JwtGenerator<>(
                JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY + JWT_KEY ,
                JWT_KEY 
        );
        ((DirectEncryptionConfiguration) g.getEncryptionConfiguration()).setMethod(EncryptionMethod.A256GCM);
        final String g1 = g.generate(new CommonProfile());
        assertNotNull(g1);
    }
}
