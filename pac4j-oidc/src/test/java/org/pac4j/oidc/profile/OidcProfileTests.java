package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * General test cases for {@link OidcProfile}.
 *
 * @author Jacob Severson
 * @author Misagh Moayyed
 * @since  1.8.0
 */
public final class OidcProfileTests implements TestsConstants {

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
    public void testClearProfile() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(new BearerAccessToken());
        profile.setIdTokenString(ID);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        profile.clearSensitiveData();
        assertNull(profile.getAccessToken());
        assertNull(profile.getIdTokenString());
    }

    @Test
    public void testReadWriteObject() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));

        byte[] result = SerializationUtils.serialize(profile);

        profile = SerializationUtils.deserialize(result);
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
        OidcProfile profile = new OidcProfile();
        profile.setIdTokenString(ID_TOKEN);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        byte[] result = SerializationUtils.serialize(profile);
        profile = SerializationUtils.deserialize(result);
        assertNull(profile.getAccessToken());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
        assertEquals(profile.getRefreshToken().getValue(), REFRESH_TOKEN);
    }

    /**
     * Test that serialization and deserialization of the OidcProfile work when the Id token is null.
     */
    @Test
    public void testReadWriteObjectNullIdToken() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setRefreshToken(new RefreshToken(REFRESH_TOKEN));
        byte[] result = SerializationUtils.serialize(profile);
        profile = SerializationUtils.deserialize(result);
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
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.setIdTokenString(ID_TOKEN);
        byte[] result = SerializationUtils.serialize(profile);
        profile = SerializationUtils.deserialize(result);
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
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(populatedAccessToken);
        profile.clearSensitiveData();

        byte[] result = SerializationUtils.serialize(profile);
        profile = SerializationUtils.deserialize(result);
        assertNull(profile.getAccessToken());
        assertNull(profile.getIdTokenString());
        assertNull(profile.getRefreshToken());
    }
}
