package org.pac4j.jwt.credentials.authenticator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
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
import org.pac4j.jwt.JwtConstants;
import org.pac4j.jwt.config.DirectEncryptionConfiguration;
import org.pac4j.jwt.config.EncryptionConfiguration;
import org.pac4j.jwt.config.SigningConfiguration;
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
import java.util.ArrayList;
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
public class JwtAuthenticator implements TokenAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private EncryptionConfiguration encryptionConfiguration;

    private List<SigningConfiguration> signingConfigurations = new ArrayList<>();

    private JWSVerifierFactory factory = new DefaultJWSVerifierFactory();

	private Key key;

    public JwtAuthenticator() {}

    public JwtAuthenticator(final List<SigningConfiguration> signingConfigurations) {
        this.signingConfigurations = signingConfigurations;
    }

    public JwtAuthenticator(final List<SigningConfiguration> signingConfigurations, final EncryptionConfiguration encryptionConfiguration) {
        this.signingConfigurations = signingConfigurations;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Deprecated
    public JwtAuthenticator(final String signingSecret) {
        this(signingSecret, signingSecret);
        logger.warn("Using the same key for signing and encryption may lead to security vulnerabilities. Consider using different keys");
    }

    @Deprecated
    public JwtAuthenticator(final String signingSecret, final String encryptionSecret) {
    	setSigningSecret(signingSecret);
        if (encryptionSecret != null) {
            this.encryptionConfiguration = new DirectEncryptionConfiguration(encryptionSecret);
        }
    }

    @Deprecated
    public JwtAuthenticator(final String publicKeyPEM, final String algorithm, final String encryptionSecret) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	setSigningPem(publicKeyPEM, algorithm);
        this.encryptionConfiguration = new DirectEncryptionConfiguration(encryptionSecret);
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
                logger.debug("JWT is not signed -> verified");
                verified = true;
            } else {

            	SignedJWT signedJWT;
            	if (jwt instanceof SignedJWT) {
                    logger.debug("JWT is signed");
                    signedJWT = (SignedJWT) jwt;

            	} else if (jwt instanceof EncryptedJWT) {
                    logger.debug("JWT is encrypted and signed");
                    CommonHelper.assertNotNull("encryptionConfiguration", encryptionConfiguration);

                    signedJWT = encryptionConfiguration.decrypt((EncryptedJWT) jwt);
                    jwt = signedJWT;

            	} else {
                	throw new TechnicalException("unsupported unsecured jwt");
            	}

                CommonHelper.assertNotNull("signingConfigurations", signingConfigurations);

            	CommonHelper.assertNotNull("key", key);
    			JWSVerifier verifier = factory.createJWSVerifier(signedJWT.getHeader(), key);
            	verified = signedJWT.verify(verifier);
            }
        	if (!verified) {
            	final String message = "JWT verification failed: " + token;
            	throw new CredentialsException(message);
        	}

          	createJwtProfile(credentials, jwt);

        } catch (final ParseException | JOSEException e) {
            throw new TechnicalException("Cannot decrypt / verify JWT", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void createJwtProfile(final TokenCredentials credentials, final JWT jwt) throws ParseException {
        final JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
        String subject = claimSet.getSubject();

        if (!subject.contains(CommonProfile.SEPARATOR)) {
            subject = JwtProfile.class.getName() + CommonProfile.SEPARATOR + subject;
        }

        final Map<String, Object> attributes = new HashMap<>(claimSet.getClaims());
        attributes.remove(JwtConstants.SUBJECT);

		final List<String> roles = (List<String>) attributes.get(JwtGenerator.INTERNAL_ROLES);
        attributes.remove(JwtGenerator.INTERNAL_ROLES);
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

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

    public EncryptionConfiguration getEncryptionConfiguration() {
        return encryptionConfiguration;
    }

    public void setEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        this.encryptionConfiguration = encryptionConfiguration;
    }
}
