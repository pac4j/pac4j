package org.pac4j.jwt.credentials.authenticator;

import com.nimbusds.jose.JWSAlgorithm;
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
import org.pac4j.jwt.config.MacSignatureConfiguration;
import org.pac4j.jwt.config.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<SignatureConfiguration> signatureConfigurations = new ArrayList<>();

    public JwtAuthenticator() {}

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations) {
        this.signatureConfigurations = signatureConfigurations;
    }

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations, final EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfigurations = signatureConfigurations;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Deprecated
    public JwtAuthenticator(final String signingSecret) {
        this(signingSecret, signingSecret);
        logger.warn("Using the same key for signature and encryption may lead to security vulnerabilities. Consider using different keys");
    }

    @Deprecated
    public JwtAuthenticator(final String signingSecret, final String encryptionSecret) {
        if (signingSecret != null) {
            addSignatureConfiguration(new MacSignatureConfiguration(signingSecret));
        }
        if (encryptionSecret != null) {
            this.encryptionConfiguration = new DirectEncryptionConfiguration(encryptionSecret);
        }
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

                boolean found = false;
                final JWSAlgorithm jwtAlgorithm = signedJWT.getHeader().getAlgorithm();
                for (final SignatureConfiguration config : signatureConfigurations) {
                    if (config.supports(jwtAlgorithm)) {
                        verified = config.verify(signedJWT);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new CredentialsException("No signature algorithm found for JWT: " + token);
                }
            }
        	if (!verified) {
            	throw new CredentialsException("JWT verification failed: " + token);
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

    public EncryptionConfiguration getEncryptionConfiguration() {
        return encryptionConfiguration;
    }

    public void setEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        this.encryptionConfiguration = encryptionConfiguration;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "signatureConfigurations", signatureConfigurations, "encryptionConfiguration", encryptionConfiguration);
    }
}
