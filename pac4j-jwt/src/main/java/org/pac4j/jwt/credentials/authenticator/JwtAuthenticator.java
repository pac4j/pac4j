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
package org.pac4j.jwt.credentials.authenticator;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for JWT. It creates the user profile and stores it in the credentials
 * for the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtAuthenticator implements TokenAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String secret;

    public JwtAuthenticator() {}

    public JwtAuthenticator(final String secret) {
        this.secret = secret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(TokenCredentials credentials) {
        CommonHelper.assertNotBlank("secret", secret);

        final String token = credentials.getToken();
        boolean verified = false;
        SignedJWT signedJWT = null;

        try {
            // Parse the JWE string
            JWEObject jweObject = JWEObject.parse(token);

            // Decrypt with shared key
            jweObject.decrypt(new DirectDecrypter(this.secret.getBytes("UTF-8")));

            // Extract payload
            signedJWT = jweObject.getPayload().toSignedJWT();

            verified = signedJWT.verify(new MACVerifier(this.secret));
        } catch (final Exception e) {
            logger.error("Cannot decrypt / verify JWT", e);
            throw new TechnicalException("Cannot decrypt / verify JWT: " + e.getMessage());
        }

        if (!verified) {
            final String message = "JWT verification failed: " + token;
            logger.error(message);
            throw new CredentialsException(message);
        }

        try {
            final ReadOnlyJWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
            final UserProfile profile = ProfileHelper.buildProfile(claimSet.getSubject(), claimSet.getCustomClaims());
            credentials.setUserProfile(profile);
        } catch (final Exception e) {
            logger.error("Cannot get claimSet", e);
            throw new TechnicalException("Cannot get claimSet: " + e.getMessage());
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
