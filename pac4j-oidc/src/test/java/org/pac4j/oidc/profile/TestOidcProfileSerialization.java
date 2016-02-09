package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This class tests the serialization features for the {@link OidcProfile}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class TestOidcProfileSerialization {

    private static final String ID_TOKEN = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbX"
            + "BsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2"
            + "MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.";
    private BearerAccessToken populatedAccessToken;

    @Before
    public void before() {
        populatedAccessToken = new BearerAccessToken(32, 128, Scope.parse("oidc email"));
    }

    @Test
    public void testClearProfile() {
        OidcProfile profile = new OidcProfile(new BearerAccessToken());
        profile.clear();
        assertNull(profile.getAccessToken());
    }

    @Test
    public void testReadWriteObject() throws Exception {
        OidcProfile profile = new OidcProfile(populatedAccessToken);
        profile.setIdTokenString(ID_TOKEN);

        byte[] result = SerializationUtils.serialize(profile);

        profile = (OidcProfile) SerializationUtils.deserialize(result);

        assertNotNull("accessToken", profile.getAccessToken());
        assertNotNull("value", profile.getAccessToken().getValue());
        assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
    }
    
    /**
     * Test that serialization and deserialization of the OidcProfile work when the BearerAccessToken is null.
     */
    @Test
    public void testReadWriteObjectNullAccessToken() {
        OidcProfile profile = new OidcProfile();
        profile.setIdTokenString(ID_TOKEN);
        byte[] result = SerializationUtils.serialize(profile);
        profile = (OidcProfile) SerializationUtils.deserialize(result);
        assertNull(profile.getAccessToken());
        assertEquals(profile.getIdTokenString(), ID_TOKEN);
    }
    
    /**
     * Test that serialization and deserialization of the OidcProfile work when the Id token is null.
     */
    @Test
    public void testReadWriteObjectNullIdToken() {
        OidcProfile profile = new OidcProfile(populatedAccessToken);
        byte[] result = SerializationUtils.serialize(profile);
        profile = (OidcProfile) SerializationUtils.deserialize(result);
        assertNotNull("accessToken", profile.getAccessToken());
        assertNotNull("value", profile.getAccessToken().getValue());
        assertEquals(profile.getAccessToken().getLifetime(), populatedAccessToken.getLifetime());
        assertEquals(profile.getAccessToken().getScope(), populatedAccessToken.getScope());
        assertNull(profile.getIdTokenString());
    }
    
    /**
     * Test that serialization and deserialization of the OidcProfile work when both tokens are null, after a call
     * to clear().
     */
    @Test
    public void testReadWriteObjectNullTokens() {
        OidcProfile profile = new OidcProfile(populatedAccessToken);
        profile.clear();

        byte[] result = SerializationUtils.serialize(profile);
        profile = (OidcProfile) SerializationUtils.deserialize(result);
        assertNull(profile.getAccessToken());
        assertNull(profile.getIdTokenString());
    }
}
