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
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.jwt.JwtConstants;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

/**
 * Authenticator for JWT. It creates the user profile and stores it in the credentials
 * for the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator}.
 * 
 *  If the secret is a pem you need to specify the algorithm which will create the right Key object for underlying validator. 
 *  In addition you can create the key object of the right type yourself and pass it in.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtAuthenticator implements TokenAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String encryptionSecret;
    
    private JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

	private Key key;

    public JwtAuthenticator() {}

    public JwtAuthenticator(final String signingSecret) {
        this(signingSecret, signingSecret);
        warning();
    }

    private void warning() {
        logger.warn("Using the same key for signing and encryption may lead to security vulnerabilities. Consider using different keys");
    }

    /**
     * @since 1.8.2
     */
    public JwtAuthenticator(final String signingSecret, final String encryptionSecret) {
    	setSigningSecret(signingSecret);
        this.encryptionSecret = encryptionSecret;
    }
    
    /**
     * @throws InvalidKeySpecException 
     * @throws NoSuchAlgorithmException 
     * @since 1.8.2
     */
    public JwtAuthenticator(final String publicKeyPEM, final String algorithm, final String encryptionSecret) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	setSigningPem(publicKeyPEM, algorithm);
        this.encryptionSecret = encryptionSecret;
    }

    /**
     * Validates the token and returns the corresponding user profile.
     *
     * @param token the JWT
     * @return the corresponding user profile
     */
    public UserProfile validateToken(final String token) {
        final TokenCredentials credentials = new TokenCredentials(token, "(validateToken)Method");
        validate(credentials);
        return credentials.getUserProfile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final TokenCredentials credentials) {
        CommonHelper.assertNotNull("key", this.key);

        final String token = credentials.getToken();
        boolean verified = false;
        SignedJWT signedJWT = null;

        try {
            // Parse the token
            final JWT jwt = JWTParser.parse(token);

            if (jwt instanceof SignedJWT) {
                signedJWT = (SignedJWT) jwt;
            } else if (jwt instanceof EncryptedJWT) {
                CommonHelper.assertNotBlank("encryptionSecret", encryptionSecret);

                final JWEObject jweObject = (JWEObject) jwt;
                jweObject.decrypt(new DirectDecrypter(this.encryptionSecret.getBytes("UTF-8")));

                // Extract payload
                signedJWT = jweObject.getPayload().toSignedJWT();
            } else {
                throw new TechnicalException("unsupported unsecured jwt");
            }
            
    		JWSVerifier verifier = factory.createJWSVerifier(signedJWT.getHeader(), key);

            verified = signedJWT.verify(verifier);
        } catch (final Exception e) {
            throw new TechnicalException("Cannot decrypt / verify JWT", e);
        }

        if (!verified) {
            final String message = "JWT verification failed: " + token;
            throw new CredentialsException(message);
        }

        try {
            createJwtProfile(credentials, signedJWT);
        } catch (final Exception e) {
            throw new TechnicalException("Cannot get claimSet", e);
        }
    }

    private static void createJwtProfile(final TokenCredentials credentials, final SignedJWT signedJWT) throws ParseException {
        final JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
        String subject = claimSet.getSubject();

        if (!subject.contains(UserProfile.SEPARATOR)) {
            subject = JwtProfile.class.getSimpleName() + UserProfile.SEPARATOR + subject;
        }

        final Map<String, Object> attributes = new HashMap<>(claimSet.getClaims());
        attributes.remove(JwtConstants.SUBJECT);
        @SuppressWarnings("unchecked")
		final List<String> roles = (List<String>) attributes.get(JwtGenerator.INTERNAL_ROLES);
        attributes.remove(JwtGenerator.INTERNAL_ROLES);
        @SuppressWarnings("unchecked")
		final List<String> permissions = (List<String>) attributes.get(JwtGenerator.INTERNAL_PERMISSIONS);
        attributes.remove(JwtGenerator.INTERNAL_PERMISSIONS);
        final UserProfile profile = ProfileHelper.buildProfile(subject, attributes);
        if (roles != null) {
            profile.addRoles(roles);
        }
        if (permissions != null) {
            profile.addPermissions(permissions);
        }
        credentials.setUserProfile(profile);
    }

    /**
     * @since 1.8.2
     */
    public String getSigningSecret() {
        return new String(key.getEncoded(), Charset.forName("UTF-8"));
    }

    /**
     * @since 1.8.2
     */
    public void setSigningSecret(final String signingSecret) {
    	X509Certificate cert = X509CertUtils.parse(signingSecret);
    	if (cert == null) {
    		this.key = new SecretKeySpec(signingSecret.getBytes(Charset.forName("UTF-8")), "AES");
    	} else {
    		this.key = cert.getPublicKey();
    	}
    }
    
    /**
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     * @since 1.8.9
     */
    public void setSigningPem(final String publicKeyPEM, final String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	// from http://stackoverflow.com/questions/35739932/how-do-i-decode-a-jwt-token-using-an-rsa-public-key-in-pem-format
    	// decode to its constituent bytes
    	String publicKeyPEMToUse = publicKeyPEM;
    	publicKeyPEMToUse = publicKeyPEMToUse.replaceAll("-----BEGIN PUBLIC KEY-----[\\r\\n]+", "");
    	publicKeyPEMToUse = publicKeyPEMToUse.replace("-----END PUBLIC KEY-----", "");
    	byte[] publicKeyBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(publicKeyPEMToUse);

    	java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(publicKeyBytes);

    	KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
    	this.key = keyFactory.generatePublic(keySpec);
    }

    /**
     * @since 1.8.2
     */
    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    /**
     * @since 1.8.2
     */
    public void setEncryptionSecret(final String encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }

    @Deprecated
    public void setSecret(String secret) {
        this.encryptionSecret = secret;
        setSigningSecret(secret);
        warning();
    }

    /**
     * @since 1.8.9
     */
	public Key getKey() {
		return key;
	}

    /**
     * @since 1.8.9
     */
	public void setKey(Key key) {
		this.key = key;
	}
    
    
}
