package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.pac4j.core.util.CommonHelper;

/**
 * This class tests the serialization features for the {@link OidcProfile}.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class TestOidcProfileSerialization {

    @Test
    public void testReadWriteObject() throws Exception {

        String idToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbXBsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.";
        BearerAccessToken token = new BearerAccessToken(32, 128, Scope.parse("oidc email"));
        OidcProfile profile = new OidcProfile(token);
        profile.setIdTokenString(idToken);

        byte[] result = SerializationUtils.serialize(profile);

        profile = (OidcProfile) SerializationUtils.deserialize(result);
        CommonHelper.assertNotNull("accessToken", profile.getAccessToken());
        CommonHelper.assertNotNull("value", profile.getAccessToken().getValue());
        CommonHelper.assertTrue(profile.getAccessToken().getLifetime() == token.getLifetime(),
                "lifetimes do not match");
        CommonHelper.assertTrue(profile.getAccessToken().getScope().equals(token.getScope()),
                "scopes do not match");
        CommonHelper.assertTrue(idToken.equals(profile.getIdTokenString()), "id tokens do not match");

    }
}
