package org.pac4j.oidc.profile;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.serializer.JavaSerializer;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oidc.profile.google.GoogleOidcProfile;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * General test cases for {@link OidcProfile}.
 *
 * @author Jacob Severson
 * @author Misagh Moayyed
 * @author Juan José Vázquez
 * @since  1.8.0
 */
public final class OidcProfileTests implements TestsConstants {

    private static final JavaSerializer JAVA_SERIALIZER = new JavaSerializer();

    public static final String ID_TOKEN = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbX"
            + "BsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2"
            + "MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.";

    private static final String REFRESH_TOKEN = "13/FuJRLB-4xn_4rd9iJPAUL0-gApRRtpDYuXH5ub5uW5Ne0-"
            + "oSohI6jUTnlb1cYPMIHq0Ne63h8HdZjAidLFlgNg==";

    private BearerAccessToken populatedAccessToken;

    @Before
    public void before() {
        populatedAccessToken = new BearerAccessToken(32, 128, Scope.parse("oidc email"));
    }

    @Test
    public void testRemoveLoginData() {
        var profile = new OidcProfile();
        profile.setAccessToken(new BearerAccessToken());
        profile.setIdTokenString(ID);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        profile.removeLoginData();
        assertNull(profile.getAccessToken());
        assertNull(profile.getIdTokenString());
    }

    @Test
    public void testReadWriteObject() {
        var profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));

        var result = JAVA_SERIALIZER.serializeToBytes(profile);
        profile = (OidcProfile) JAVA_SERIALIZER.deserializeFromBytes(result);

        assertNotNull("accessToken", profile.getAccessToken());
        assertNotNull("value", profile.getAccessToken().getValue());
        assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
        assertEquals(profile.getRefreshToken().getValue(), REFRESH_TOKEN);
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the BearerAccessToken is null.
     */
    @Test
    public void testReadWriteObjectNullAccessToken() {
        var profile = new OidcProfile();
        profile.setIdTokenString(ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        var result = JAVA_SERIALIZER.serializeToBytes(profile);
        profile = (OidcProfile) JAVA_SERIALIZER.deserializeFromBytes(result);
        assertNull(profile.getAccessToken());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
        assertEquals(profile.getRefreshToken().getValue(), REFRESH_TOKEN);
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the Id token is null.
     */
    @Test
    public void testReadWriteObjectNullIdToken() {
        var profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        var result = JAVA_SERIALIZER.serializeToBytes(profile);
        profile = (OidcProfile) JAVA_SERIALIZER.deserializeFromBytes(result);
        assertNotNull("accessToken", profile.getAccessToken());
        assertNotNull("value", profile.getAccessToken().getValue());
        assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        assertEquals(profile.getRefreshToken().getValue(), REFRESH_TOKEN);
        assertNull(profile.getIdTokenString());
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the Refresh token is null.
     */
    @Test
    public void testReadWriteObjectNullRefreshToken() {
        var profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(ID_TOKEN);
        var result = JAVA_SERIALIZER.serializeToBytes(profile);
        profile = (OidcProfile) JAVA_SERIALIZER.deserializeFromBytes(result);
        assertNotNull("accessToken", profile.getAccessToken());
        assertNotNull("value", profile.getAccessToken().getValue());
        assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
        assertNull(profile.getRefreshToken());
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when tokens are null, after a call
     * to clearSensitiveData().
     */
    @Test
    public void testReadWriteObjectNullTokens() {
        var profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.removeLoginData();

        var result = JAVA_SERIALIZER.serializeToBytes(profile);
        profile = (OidcProfile) JAVA_SERIALIZER.deserializeFromBytes(result);
        assertNull(profile.getAccessToken());
        assertNull(profile.getIdTokenString());
        assertNull(profile.getRefreshToken());
    }

    /**
     * Default behavior. No expiration info.
     */
    @Test
    public void testNullTokenExpiration() {
        var profile = new OidcProfile();
        assertFalse(profile.isExpired());
    }

    /**
     * If the token is not expired, then the session is not considered expired.
     */
    @Test
    public void testNoExpirationWithNoExpiredToken() {
        final AccessToken token = new BearerAccessToken("token_value", 3600, new Scope("scope"));
        val profile = new OidcProfile();
        profile.setAccessToken(token);
        profile.setTokenExpirationAdvance(0);
        assertFalse(profile.isExpired());
    }

    /**
     * If the token is expired, then the session is considered expired.
     */
    @Test
    public void testExpirationWithExpiredToken() {
        final AccessToken token = new BearerAccessToken("token_value", -1, new Scope("scope"));
        val profile = new OidcProfile();
        profile.setAccessToken(token);
        profile.setTokenExpirationAdvance(0);
        assertTrue(profile.isExpired());
    }

    /**
     * The token is not expired but the session will be consider expired if
     * a long enough token expiration advance is established.
     */
    @Test
    public void testAdvancedExpirationWithNoExpiredToken() {
        final AccessToken token = new BearerAccessToken("token_value", 3600, new Scope("scope"));
        val profile = new OidcProfile();
        profile.setAccessToken(token);
        profile.setTokenExpirationAdvance(3600); // 1 hour
        assertTrue(profile.isExpired());
    }

    /**
     * Test experation based on access token exp date.
     */
    @Test
    public void testAccessTokenExpiration(){

        val profile = new OidcProfile();
        profile.setAccessToken(new BearerAccessToken(ID_TOKEN));

        assertTrue(profile.isExpired());

        var cs = new JWTClaimsSet.Builder().expirationTime(Date.from(Instant.now().plusSeconds(30))).build();

        profile.setAccessToken(new BearerAccessToken(new PlainJWT(cs).serialize()));

        assertFalse(profile.isExpired());
    }

    @Test
    public void testGoogleProfile() {
        final GoogleOidcProfile googleOidcProfile = new GoogleOidcProfile();
        googleOidcProfile.setTokenExpirationAdvance(-1);

        final Date expiration = Date.from(Instant.now().plusSeconds(3600));
        //googleOidcProfile.addAttribute(OidcProfileDefinition.EXPIRATION, expiration.getTime());
        googleOidcProfile.setExpiration(expiration);

        final String secret = "12345678901234567890123456789012";
        final SignatureConfiguration secretSignatureConfiguration = new SecretSignatureConfiguration(secret);
        final JwtGenerator generator = new JwtGenerator();
        generator.setSignatureConfiguration(secretSignatureConfiguration);
        String token = generator.generate(googleOidcProfile);

        JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.setSignatureConfiguration(secretSignatureConfiguration);

        GoogleOidcProfile userProfile = (GoogleOidcProfile) jwtAuthenticator.validateToken(token);
        assertFalse(userProfile.isExpired());
        assertEquals(userProfile.getExpiration(), expiration);
    }
}
