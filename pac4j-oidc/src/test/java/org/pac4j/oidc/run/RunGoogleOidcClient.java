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
package org.pac4j.oidc.run;

import com.esotericsoftware.kryo.Kryo;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.kryo.AccessTokenTypeSerializer;
import org.pac4j.oidc.profile.GoogleOidcProfile;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link GoogleOidcClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunGoogleOidcClient extends RunClient {

    public static void main(final String[] args) throws Exception {
        new RunGoogleOidcClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup91";
    }

    @Override
    protected IndirectClient getClient() {
        final GoogleOidcClient client = new GoogleOidcClient();
        client.setKey("682158564078-ndcjc83kp5v7vudikqu1fudtkcs2odeb.apps.googleusercontent.com");
        client.setSecret("gLB2U7LPYBFTxqYtyG81AhLH");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(GoogleOidcProfile.class);
        kryo.register(AccessTokenType.class, new AccessTokenTypeSerializer());
    }

    @Override
    protected void verifyProfile(final UserProfile userProfile) {
        final GoogleOidcProfile profile = (GoogleOidcProfile) userProfile;
        assertEquals("113675986756217860428", profile.getId());
        assertEquals(GoogleOidcProfile.class.getSimpleName() + UserProfile.SEPARATOR + "113675986756217860428",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), GoogleOidcProfile.class));
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(userProfile, getLogin(), "Jérôme", "ScribeUP", "Jérôme ScribeUP", null,
                Gender.MALE, Locale.ENGLISH,
                "https://lh4.googleusercontent.com/-fFUNeYqT6bk/AAAAAAAAAAI/AAAAAAAAAAA/5gBL6csVWio/photo.jpg",
                "https://plus.google.com/113675986756217860428", null);
        assertEquals(10, profile.getAttributes().size());
        assertTrue(profile.getEmailVerified());
    }
}
