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
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.TokenAuthenticator;
import org.pac4j.core.util.InitializableWebObject;
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
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtAuthenticator extends InitializableWebObject implements TokenAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String encryptionSecret;
    
    private JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

	private Key key;

    public JwtAuthenticator() {
    }

    public JwtAuthenticator(final String signingSecret) {
        this(signingSecret, signingSecret);
        warning();
    }

    private void warning() {
        logger.warn("Using the same key for signing and encryption may lead to security vulnerabilities. Consider using different keys");
    }

    /**
     * Initializes the authenticator that will validate JWT tokens.
     *
     * @param signingSecret    The signingSecret. Must be at least 256 bits long and not {@code null}
     * @param encryptionSecret The encryptionSecret. Must be at least 256 bits long and not {@code null} for encrypted JWT
     * @since 1.8.2
     */
    public JwtAuthenticator(final String signingSecret, final String encryptionSecret) {
    	setSigningSecret(signingSecret);
        this.encryptionSecret = encryptionSecret;
    }
    
    /**
     * Define the signing certificate and the encryption secret.
     *
     * @param publicKeyPEM the public key certificate
     * @param algorithm the key algorithm
     * @param encryptionSecret the encryption secret
     * @throws NoSuchAlgorithmException No such algorithm exception
     * @throws InvalidKeySpecException  Invalid key exception
     * @since 1.8.2
     */
    public JwtAuthenticator(final String publicKeyPEM, final String algorithm, final String encryptionSecret) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	setSigningPem(publicKeyPEM, algorithm);
        this.encryptionSecret = encryptionSecret;
    }

    @Override
    protected void internalInit(final WebContext context) {

    }

    /**
     * Validates the token and returns the corresponding user profile.
     *
     * @param token the JWT
     * @return the corresponding user profile
     */
    public CommonProfile validateToken(final String token) {
        final TokenCredentials credentials = new TokenCredentials(token, "(validateToken)Method");
        try {
            validate(credentials, null);
        } catch (final HttpAction e) {
            throw new TechnicalException(e);
        }
        return credentials.getUserProfile();
    }

    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) throws HttpAction {
        final String token = credentials.getToken();
        boolean verified = false;

        try {
            // Parse the token
            JWT jwt = JWTParser.parse(token);

			if (jwt instanceof PlainJWT) {
                verified = true;
            } else {
            	SignedJWT signedJWT;
            	if (jwt instanceof SignedJWT) {
                    logger.debug("JWT is signed");
                    signedJWT = (SignedJWT) jwt;
            	} else if (jwt instanceof EncryptedJWT) {
                	CommonHelper.assertNotBlank("encryptionSecret", encryptionSecret);

                	final JWEObject jweObject = (JWEObject) jwt;
                	jweObject.decrypt(new DirectDecrypter(this.encryptionSecret.getBytes("UTF-8")));

                	// Extract payload
                	signedJWT = jweObject.getPayload().toSignedJWT();
                	jwt = signedJWT;
            	} else {
                	throw new TechnicalException("unsupported unsecured jwt");
            	}
            	CommonHelper.assertNotNull("key", key);
    			JWSVerifier verifier = factory.createJWSVerifier(signedJWT.getHeader(), key);

            	verified = signedJWT.verify(verifier);
            }
        	if (!verified) {
            	final String message = "JWT verification failed: " + token;
            	throw new CredentialsException(message);
        	}

        	try {
            	createJwtProfile(credentials, jwt);
        	} catch (final Exception e) {
            	throw new TechnicalException("Cannot get claimSet", e);
        	}
        } catch (final Exception e) {
            throw new TechnicalException("Cannot decrypt / verify JWT", e);
        }


    }

    private static void createJwtProfile(final TokenCredentials credentials, final JWT jwt) throws ParseException {
        final JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
        String subject = claimSet.getSubject();

        if (!subject.contains(CommonProfile.SEPARATOR)) {
            subject = JwtProfile.class.getName() + CommonProfile.SEPARATOR + subject;
        }

        final Map<String, Object> attributes = new HashMap<>(claimSet.getClaims());
        attributes.remove(JwtConstants.SUBJECT);
        @SuppressWarnings("unchecked")
		final List<String> roles = (List<String>) attributes.get(JwtGenerator.INTERNAL_ROLES);
        attributes.remove(JwtGenerator.INTERNAL_ROLES);
        @SuppressWarnings("unchecked")
		final List<String> permissions = (List<String>) attributes.get(JwtGenerator.INTERNAL_PERMISSIONS);
        attributes.remove(JwtGenerator.INTERNAL_PERMISSIONS);
        final CommonProfile profile = ProfileHelper.buildProfile(subject, attributes);
        if (roles != null) {
            profile.addRoles(roles);
        }
        if (permissions != null) {
            profile.addPermissions(permissions);
        }
        credentials.setUserProfile(profile);
    }

    public String getSigningSecret() {
        return new String(key.getEncoded(), Charset.forName("UTF-8"));
    }

    public void setSigningSecret(final String signingSecret) {
    	X509Certificate cert = X509CertUtils.parse(signingSecret);
    	if (cert == null) {
    		this.key = new SecretKeySpec(signingSecret.getBytes(Charset.forName("UTF-8")), "AES");
    	} else {
    		this.key = cert.getPublicKey();
    	}
    }
    
    /**
     * Define the signing certificate.
     *
     * @param publicKeyPEM the public key certificate
     * @param algorithm the key algorithm
     * @throws NoSuchAlgorithmException No such algorithm exception
     * @throws InvalidKeySpecException  Invalid key exception
     * @since 1.8.2
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

    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public void setEncryptionSecret(final String encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }
    
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}
}
