package org.pac4j.jwt.credentials.authenticator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Authenticator for JWT. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@ToString
@Getter
@Setter
public class JwtAuthenticator extends ProfileDefinitionAware implements Authenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<EncryptionConfiguration> encryptionConfigurations = new ArrayList<>();

    private List<SignatureConfiguration> signatureConfigurations = new ArrayList<>();

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    private Date expirationTime;

    private ValueGenerator identifierGenerator;

    public JwtAuthenticator() {}

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations) {
        this.signatureConfigurations = signatureConfigurations;
    }

    public JwtAuthenticator(final List<SignatureConfiguration> signatureConfigurations,
        final List<EncryptionConfiguration> encryptionConfigurations) {
        this.signatureConfigurations = signatureConfigurations;
        this.encryptionConfigurations = encryptionConfigurations;
    }

    public JwtAuthenticator(final SignatureConfiguration signatureConfiguration) {
        setSignatureConfiguration(signatureConfiguration);
    }

    public JwtAuthenticator(final SignatureConfiguration signatureConfiguration, final EncryptionConfiguration encryptionConfiguration) {
        setSignatureConfiguration(signatureConfiguration);
        setEncryptionConfiguration(encryptionConfiguration);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);
        setProfileDefinitionIfUndefined(new JwtProfileDefinition());

        if (signatureConfigurations.isEmpty()) {
            logger.warn("No signature configurations have been defined: non-signed JWT will be accepted!");
        }
    }

    /**
     * Validates the token and returns the corresponding user profile.
     *
     * @param token the JWT
     * @return the corresponding user profile
     */
    public Map<String, Object> validateTokenAndGetClaims(final String token) {
        val profile = validateToken(token);

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
    public UserProfile validateToken(final String token) {
        val credentials = new TokenCredentials(token);
        try {
            validate(credentials, null, null);
        } catch (final HttpAction e) {
            throw new TechnicalException(e);
        } catch (final CredentialsException e) {
            logger.info("Failed to retrieve or validate credentials: {}", e.getMessage());
            logger.debug("Failed to retrieve or validate credentials", e);
            return null;
        }
        return credentials.getUserProfile();
    }

    @Override
    public Optional<Credentials> validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        val credentials = (TokenCredentials) cred;
        val token = credentials.getToken();

        if (context != null) {
            // set the www-authenticate in case of error
            context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Bearer realm=\"" + realmName + "\"");
        }

        try {
            // Parse the token
            var jwt = JWTParser.parse(token);

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

                    val encryptedJWT = (EncryptedJWT) jwt;
                    var found = false;
                    val header = encryptedJWT.getHeader();
                    val algorithm = header.getAlgorithm();
                    val method = header.getEncryptionMethod();
                    for (val config : encryptionConfigurations) {
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

                    var verified = false;
                    var found = false;
                    val algorithm = signedJWT.getHeader().getAlgorithm();
                    for (val config : signatureConfigurations) {
                        if (config.supports(algorithm)) {
                            logger.debug("Using signature configuration: {}", config);
                            try {
                                verified = config.verify(signedJWT);
                                found = true;
                                if (verified) {
                                    break;
                                }
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

            createJwtProfile(credentials, jwt, context, sessionStore);

        } catch (final ParseException e) {
            throw new CredentialsException("Cannot decrypt / verify JWT", e);
        }

        return Optional.of(credentials);
    }

    @SuppressWarnings("unchecked")
    protected void createJwtProfile(final TokenCredentials credentials, final JWT jwt, final WebContext context,
                                    final SessionStore sessionStore) throws ParseException {
        val claimSet = jwt.getJWTClaimsSet();
        var subject = claimSet.getSubject();
        if (subject == null) {
            if (identifierGenerator != null) {
                subject = identifierGenerator.generateValue(context, sessionStore);
            }
            if (subject == null) {
                throw new TechnicalException("The JWT must contain a subject or an id must be generated via the identifierGenerator");
            }
        }

        val expTime = claimSet.getExpirationTime();
        if (expTime != null) {
            val now = new Date();
            if (expTime.before(now)) {
                logger.error("The JWT is expired: no profile is built");
                return;
            }
            if (this.expirationTime != null && expTime.after(this.expirationTime)) {
                logger.error("The JWT is expired: no profile is built");
                return;
            }
        }

        val attributes = new HashMap<String, Object>(claimSet.getClaims());
        attributes.remove(JwtClaims.SUBJECT);

        val roles = (List<String>) attributes.get(JwtGenerator.INTERNAL_ROLES);
        attributes.remove(JwtGenerator.INTERNAL_ROLES);
        val linkedId = (String) attributes.get(JwtGenerator.INTERNAL_LINKEDID);
        attributes.remove(JwtGenerator.INTERNAL_LINKEDID);

        val profile = getProfileDefinition().newProfile(subject);
        profile.setId(ProfileHelper.sanitizeIdentifier(subject));
        getProfileDefinition().convertAndAdd(profile, attributes, null);

        if (roles != null) {
            profile.addRoles(roles);
        }
        if (linkedId != null) {
            profile.setLinkedId(linkedId);
        }
        credentials.setUserProfile(profile);
    }

    public void setSignatureConfiguration(final SignatureConfiguration signatureConfiguration) {
        addSignatureConfiguration(signatureConfiguration);
    }

    public void addSignatureConfiguration(final SignatureConfiguration signatureConfiguration) {
        assertNotNull("signatureConfiguration", signatureConfiguration);
        signatureConfigurations.add(signatureConfiguration);
    }

    public void setSignatureConfigurations(final List<SignatureConfiguration> signatureConfigurations) {
        assertNotNull("signatureConfigurations", signatureConfigurations);
        this.signatureConfigurations = signatureConfigurations;
    }

    public void setEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        addEncryptionConfiguration(encryptionConfiguration);
    }

    public void addEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        assertNotNull("encryptionConfiguration", encryptionConfiguration);
        encryptionConfigurations.add(encryptionConfiguration);
    }

    public void setEncryptionConfigurations(final List<EncryptionConfiguration> encryptionConfigurations) {
        assertNotNull("encryptionConfigurations", encryptionConfigurations);
        this.encryptionConfigurations = encryptionConfigurations;
    }

    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = new Date(expirationTime.getTime());
    }

    public Date getExpirationTime() {
        return new Date(expirationTime.getTime());
    }
}
