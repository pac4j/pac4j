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

        BearerAccessToken token = new BearerAccessToken(32, 128, Scope.parse("oidc email"));
        OidcProfile profile = new OidcProfile(token);
        byte[] result = SerializationUtils.serialize(profile);

        profile = (OidcProfile) SerializationUtils.deserialize(result);
        CommonHelper.assertNotNull("accessToken", profile.getAccessToken());
        CommonHelper.assertNotNull("value", profile.getAccessToken().getValue());
        CommonHelper.assertTrue(profile.getAccessToken().getLifetime() == token.getLifetime(),
                "lifetimes do not match");
        CommonHelper.assertTrue(profile.getAccessToken().getScope().equals(token.getScope()),
                "scopes do not match");

    }
}
