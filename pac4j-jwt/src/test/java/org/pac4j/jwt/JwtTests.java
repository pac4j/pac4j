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
package org.pac4j.jwt;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JwtGenerator} and {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtTests implements TestsConstants {

    private final static String JWT_KEY = "12345678901234567890123456789012";
    private final static String JWT_KEY2 = "02345678901234567890123456789010";

    @Test
    public void testGenericJwt() {
        final String token =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDdXN0b20gSldUIEJ1aWxkZXIiLCJpYXQiOjE0NTAxNjQ0NTUsImV4cCI6MTQ4MTcwMDQ1NSwiYXVkIjoiaHR0cHM6Ly9naXRodWIuY29tL3BhYzRqIiwic3ViIjoidXNlckBwYWM0ai5vcmciLCJlbWFpbCI6InVzZXJAcGFjNGoub3JnIn0.zOPb7rbI3IY7iLXTK126Ggu2Q3pNCZsUzzgzgsqR7xU";

        final TokenCredentials credentials = new TokenCredentials(token, JwtAuthenticator.class.getName());
        final JwtAuthenticator authenticator = new JwtAuthenticator(JWT_KEY);
        authenticator.validate(credentials);
        assertNotNull(credentials.getUserProfile());
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateSub() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtConstants.SUBJECT, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test(expected = TechnicalException.class)
    public void testGenerateAuthenticateIat() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        profile.addAttribute(JwtConstants.ISSUE_TIME, VALUE);
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticate() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateNotEncrypted() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateAndEncrypted() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, JWT_KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateAndEncryptedDifferentKeys() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<>(JWT_KEY, JWT_KEY2);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token, new JwtAuthenticator(JWT_KEY, JWT_KEY2));
    }

    private void assertToken(FacebookProfile profile, String token) {
        assertToken(profile, token, new JwtAuthenticator(JWT_KEY));
    }

    private void assertToken(FacebookProfile profile, String token, JwtAuthenticator authenticator) {
        final TokenCredentials credentials = new TokenCredentials(token, CLIENT_NAME);
        authenticator.validate(credentials);
        final UserProfile profile2 = credentials.getUserProfile();
        assertTrue(profile2 instanceof FacebookProfile);
        final FacebookProfile fbProfile = (FacebookProfile) profile2;
        assertEquals(profile.getTypedId(), fbProfile.getTypedId());
        assertEquals(profile.getFirstName(), fbProfile.getFirstName());
        assertEquals(profile.getDisplayName(), fbProfile.getDisplayName());
        assertEquals(profile.getFamilyName(), fbProfile.getFamilyName());
        assertEquals(profile.getVerified(), fbProfile.getVerified());
    }

    private FacebookProfile createProfile() {
        final FacebookProfile profile = new FacebookProfile();
        profile.setId(ID);
        profile.addAttribute(FacebookAttributesDefinition.NAME, NAME);
        profile.addAttribute(FacebookAttributesDefinition.VERIFIED, true);
        return profile;
    }

    @Test(expected = TechnicalException.class)
    public void testAuthenticateFailed() {
        final JwtAuthenticator authenticator = new JwtAuthenticator(JWT_KEY);
        final TokenCredentials credentials = new TokenCredentials("fakeToken", CLIENT_NAME);
        authenticator.validate(credentials);
    }
}
