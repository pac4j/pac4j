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
package org.pac4j.jwt.profile;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * Generates a JWT token from a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtGenerator<U extends UserProfile> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String secret;
    private boolean encrypted = true;

    /**
     * Initializes the generator that will create JWT tokens that are both signed and encrypted.
     *
     * @param secret The secret. Must be at least 256 bits long and not {@code null}
     */
    public JwtGenerator(String secret) {
        this.secret = secret;
    }

    /**
     * Initializes the generator that will create JWT tokens that is signed and optionally encrypted.
     *
     * @param secret    The secret. Must be at least 256 bits long and not {@code null}
     * @param encrypted whether the JWT will be encrypted or not
     */
    public JwtGenerator(final String secret, final boolean encrypted) {
        this.secret = secret;
        this.encrypted = encrypted;
    }

    /**
     * Generates a JWT from a user profile.
     *
     * @param profile the given user profile
     * @return the created JWT
     */
    public String generate(final U profile) {
        try {
            // Create HMAC signer
            final JWSSigner signer = new MACSigner(this.secret);

            // Build claims
            final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(profile.getTypedId())
                    .issueTime(new Date())
                    .issuer(this.getClass().getSimpleName());

            // add attributes
            final Map<String, Object> attributes = profile.getAttributes();
            for (final String key : attributes.keySet()) {
                builder.claim(key, attributes.get(key));
            }

            // claims
            final JWTClaimsSet claims = builder.build();

            // signed
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);

            // Apply the HMAC
            signedJWT.sign(signer);

            if (encrypted) {
                // Create JWE object with signed JWT as payload
                final JWEObject jweObject = new JWEObject(
                        new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM).contentType("JWT").build(),
                        new Payload(signedJWT));

                // Perform encryption
                jweObject.encrypt(new DirectEncrypter(this.secret.getBytes("UTF-8")));

                // Serialise to JWE compact form
                return jweObject.serialize();
            } else {
                return signedJWT.serialize();
            }
        } catch (final Exception e) {
            throw new TechnicalException("Cannot generate JWT", e);
        }
    }
}
