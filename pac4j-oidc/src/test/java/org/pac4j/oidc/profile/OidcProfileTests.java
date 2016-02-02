/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.pac4j.core.util.CommonHelper;

import static org.junit.Assert.*;

/**
 * General test cases for {@link OidcProfile}.
 *
 * @author Jacob Severson
 * @author Misagh Moayyed
 * @since  1.8.0
 */
public class OidcProfileTests {

    @Test
    public void testClearProfile() {
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(new BearerAccessToken());
        profile.clearSensitiveData();
        assertNull(profile.getAccessToken());
    }

    @Test
    public void testReadWriteObject() throws Exception {

        String idToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwczovL2p3dC1pZHAuZXhhbXBsZS5jb20iLCJzdWIiOiJtYWlsdG86cGVyc29uQGV4YW1wbGUuY29tIiwibmJmIjoxNDQwMTEyMDE1LCJleHAiOjE0NDAxMTU2MTUsImlhdCI6MTQ0MDExMjAxNSwianRpIjoiaWQxMjM0NTYiLCJ0eXAiOiJodHRwczovL2V4YW1wbGUuY29tL3JlZ2lzdGVyIn0.";
        BearerAccessToken token = new BearerAccessToken(32, 128, Scope.parse("oidc email"));
        OidcProfile profile = new OidcProfile();
        profile.setAccessToken(token);
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
