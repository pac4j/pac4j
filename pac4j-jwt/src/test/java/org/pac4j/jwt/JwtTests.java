package org.pac4j.jwt;

import org.junit.Test;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

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

    private final static String JWT_KEY = "12345678901234567890123456789012";
    private final static String JWT_KEY2 = "02345678901234567890123456789010";

    private final static Set<String> ROLES = new HashSet<>(Arrays.asList(new String[] { "role1", "role2"}));
    private final static Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(new String[] { "perm1"}));

    @Test
    public void testGenericJwt() throws HttpAction {
        final String token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDdXN0b20gSldUIEJ1aWxkZXIiLCJpYXQiOjE0NTAxNjQ0NTUsImV4cCI6MTQ4MTcwMDQ1NSwiYXVkIjoiaHR0cHM6Ly9naXRodWIuY29tL3BhYzRqIiwic3ViIjoidXNlckBwYWM0ai5vcmciLCJlbWFpbCI6InVzZXJAcGFjNGoub3JnIn0.zOPb7rbI3IY7iLXTK126Ggu2Q3pNCZsUzzgzgsqR7xU";

        final TokenCredentials credentials = new TokenCredentials(token, JwtAuthenticator.class.getName());
        final JwtAuthenticator authenticator = new JwtAuthenticator(JWT_KEY);
        authenticator.init(null);
        authenticator.validate(credentials);
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
        assertToken(profile, token);
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
        authenticator.init(null);
        authenticator.validate(credentials);
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
        authenticator.init(null);
        final TokenCredentials credentials = new TokenCredentials("fakeToken", CLIENT_NAME);
        authenticator.validate(credentials);
    }
}
