package org.pac4j.jwt.credentials.authenticator;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.jwt.JwtClaims;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * Authenticator for JWT. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtAuthenticator implements Authenticator<TokenCredentials> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<EncryptionConfiguration> encryptionConfigurations = new ArrayList<>();

    private List<SignatureConfiguration> signatureConfigurations = new ArrayList<>();

    public JwtAuthenticator() {}

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations) {
        this.signatureConfigurations = signatureConfigurations;
    }

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations, final List<EncryptionConfiguration> encryptionConfigurations) {
        this.signatureConfigurations = signatureConfigurations;
        this.encryptionConfigurations = encryptionConfigurations;
    }

    public JwtAuthenticator(final SignatureConfiguration signatureConfiguration, final EncryptionConfiguration encryptionConfiguration) {
        setSignatureConfiguration(signatureConfiguration);
        setEncryptionConfiguration(encryptionConfiguration);
    }


    @Deprecated
    public JwtAuthenticator(final String signingSecret) {
        this(signingSecret, signingSecret);
        logger.warn("Using the same key for signature and encryption may lead to security vulnerabilities. Consider using different keys");
    }

    @Deprecated
    public JwtAuthenticator(final String signingSecret, final String encryptionSecret) {
        if (signingSecret != null) {
            addSignatureConfiguration(new SecretSignatureConfiguration(signingSecret));
        }
        if (encryptionSecret != null) {
            addEncryptionConfiguration(new SecretEncryptionConfiguration(encryptionSecret));
        }
    }

    /**
     * Validates the token and returns the corresponding user profile.
     *
     * @param token the JWT
     * @return the corresponding user profile
     */
    public Map<String, Object> validateTokenAndGetClaims(final String token) {
        final CommonProfile profile = validateToken(token);

        final Map<String, Object> claims = new HashMap<>(profile.getAttributes());
        claims.put(JwtClaims.SUBJECT, profile.getId());

        return claims;
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

        try {
            // Parse the token
            JWT jwt = JWTParser.parse(token);

			if (jwt instanceof PlainJWT) {
                if (signatureConfigurations.isEmpty()) {
                    logger.debug("JWT is not signed and no signature configurations -> verified");
                } else {
                    throw new CredentialsException("A non-signed JWT cannot be accepted as signature configurations have been defined");
                }
            } else {

                SignedJWT signedJWT = null;
                if (jwt instanceof SignedJWT) {
                    signedJWT = (SignedJWT) jwt;
                }

                // encrypted?
                if (jwt instanceof EncryptedJWT) {
                    logger.debug("JWT is encrypted");

                    final EncryptedJWT encryptedJWT = (EncryptedJWT) jwt;
                    boolean found = false;
                    final JWEHeader header = encryptedJWT.getHeader();
                    final JWEAlgorithm algorithm = header.getAlgorithm();
                    final EncryptionMethod method = header.getEncryptionMethod();
                    for (final EncryptionConfiguration config : encryptionConfigurations) {
                        if (config.supports(algorithm, method)) {
                            logger.debug("Using encryption configuration: {}", config);
                            try {
                                config.decrypt(encryptedJWT);
                                signedJWT = encryptedJWT.getPayload().toSignedJWT();
                                if (signedJWT != null) {
                                    jwt = signedJWT;
                                }
                                found = true;
                                break;
                            } catch (final JOSEException e) {
                                logger.debug("Decryption fails with encryption configuration: {}, passing to the next one", config);
                            }
                        }
                    }
                    if (!found) {
                        throw new CredentialsException("No encryption algorithm found for JWT: " + token);
                    }
                }

                // signed?
                if (signedJWT != null) {
                    logger.debug("JWT is signed");

                    boolean verified = false;
                    boolean found = false;
                    final JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
                    for (final SignatureConfiguration config : signatureConfigurations) {
                        if (config.supports(algorithm)) {
                            logger.debug("Using signature configuration: {}", config);
                            try {
                                verified = config.verify(signedJWT);
                                found = true;
                                break;
                            } catch (final JOSEException e) {
                                logger.debug("Verification fails with signature configuration: {}, passing to the next one", config);
                            }
                        }
                    }
                    if (!found) {
                        throw new CredentialsException("No signature algorithm found for JWT: " + token);
                    }
                    if (!verified) {
                        throw new CredentialsException("JWT verification failed: " + token);
                    }
                }
            }

          	createJwtProfile(credentials, jwt);

        } catch (final ParseException e) {
            throw new TechnicalException("Cannot decrypt / verify JWT", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void createJwtProfile(final TokenCredentials credentials, final JWT jwt) throws ParseException {
        final JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
        String subject = claimSet.getSubject();
        if (subject == null) {
            throw new TechnicalException("JWT must contain a subject ('sub' claim)");
        }
        if (!subject.contains(CommonProfile.SEPARATOR)) {
            subject = JwtProfile.class.getName() + CommonProfile.SEPARATOR + subject;
        }

        final Date expirationTime = claimSet.getExpirationTime();
        if (expirationTime != null) {
            final Date now = new Date();
            if (expirationTime.before(now)) {
                logger.error("The JWT is expired: no profile is built");
                return;
            }
        }

        final Map<String, Object> attributes = new HashMap<>(claimSet.getClaims());
        attributes.remove(JwtClaims.SUBJECT);

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

    public List<SignatureConfiguration> getSignatureConfigurations() {
        return signatureConfigurations;
    }

    public void setSignatureConfiguration(final SignatureConfiguration signatureConfiguration) {
        addSignatureConfiguration(signatureConfiguration);
    }

    public void addSignatureConfiguration(final SignatureConfiguration signatureConfiguration) {
        CommonHelper.assertNotNull("signatureConfiguration", signatureConfiguration);
        signatureConfigurations.add(signatureConfiguration);
    }

    public void setSignatureConfigurations(final List<SignatureConfiguration> signatureConfigurations) {
        CommonHelper.assertNotNull("signatureConfigurations", signatureConfigurations);
        this.signatureConfigurations = signatureConfigurations;
    }

    public List<EncryptionConfiguration> getEncryptionConfigurations() {
        return encryptionConfigurations;
    }

    public void setEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        addEncryptionConfiguration(encryptionConfiguration);
    }

    public void addEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        CommonHelper.assertNotNull("encryptionConfiguration", encryptionConfiguration);
        encryptionConfigurations.add(encryptionConfiguration);
    }

    public void setEncryptionConfigurations(final List<EncryptionConfiguration> encryptionConfigurations) {
        CommonHelper.assertNotNull("encryptionConfigurations", encryptionConfigurations);
        this.encryptionConfigurations = encryptionConfigurations;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "signatureConfigurations", signatureConfigurations, "encryptionConfigurations", encryptionConfigurations);
    }
}
