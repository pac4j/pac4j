package org.pac4j.jwt.profile;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.JwtConstants;
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
public class JwtGenerator<U extends CommonProfile> {

    public static final String INTERNAL_ROLES = "$int_roles";
    public static final String INTERNAL_PERMISSIONS = "$int_perms";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String signingSecret;
    private String encryptionSecret;

    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
    private EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;

    public JwtGenerator(final String secret) {
        this(secret, true);
    }

    public JwtGenerator(final String secret, final boolean encrypted) {
        this.signingSecret = secret;
        if (encrypted) {
            this.encryptionSecret = secret;
            logger.warn("Using the same key for signing and encryption may lead to security vulnerabilities. Consider using different keys");
        }
    }

    /**
     * Initializes the generator that will create JWT tokens that is signed and optionally encrypted.
     *
     * @param signingSecret    The signingSecret. Must be at least 256 bits long and not {@code null}
     * @param encryptionSecret The encryptionSecret. Must be at least 256 bits long and not {@code null} if you want encryption
     * @since 1.8.2
     */
    public JwtGenerator(final String signingSecret, final String encryptionSecret) {
        this.signingSecret = signingSecret;
        this.encryptionSecret = encryptionSecret;
    }

    /**
     * Generates a JWT from a user profile.
     *
     * @param profile the given user profile
     * @return the created JWT
     */
    public String generate(final U profile) {
         verifyProfile(profile);

        try {
            final JWTClaimsSet claims = buildJwtClaimsSet(profile);
            if (CommonHelper.isNotBlank(this.signingSecret)) {
                CommonHelper.assertNotNull("jwsAlgorithm", this.jwsAlgorithm);
                
                final SignedJWT signedJWT = signJwt(claims);
                
                if (CommonHelper.isNotBlank(this.encryptionSecret)) {
                    CommonHelper.assertNotNull("jweAlgorithm", jweAlgorithm);
                    CommonHelper.assertNotNull("encryptionMethod", encryptionMethod);

                    return encryptJwt(signedJWT);
                }
                return signedJWT.serialize();
            }

            return new PlainJWT(claims).serialize();

        } catch (final Exception e) {
            throw new TechnicalException("Cannot generate JWT", e);
        }
    }

    protected String encryptJwt(final SignedJWT signedJWT) throws Exception {
        // Create JWE object with signed JWT as payload
        final JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(jweAlgorithm, encryptionMethod).contentType("JWT").build(),
                new Payload(signedJWT));

        // Perform encryption
        jweObject.encrypt(new DirectEncrypter(this.encryptionSecret.getBytes("UTF-8")));

        // Serialise to JWE compact form
        return jweObject.serialize();
    }

    protected SignedJWT signJwt(final JWTClaimsSet claims) throws JOSEException {
        // Create HMAC signer
        final JWSSigner signer = new MACSigner(this.signingSecret);
        final SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claims);
        // Apply the HMAC
        signedJWT.sign(signer);
        return signedJWT;
    }

    protected JWTClaimsSet buildJwtClaimsSet(final U profile) {
        // Build claims
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(profile.getTypedId())
                .issueTime(new Date());

        // add attributes
        final Map<String, Object> attributes = profile.getAttributes();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        builder.claim(INTERNAL_ROLES, profile.getRoles());
        builder.claim(INTERNAL_PERMISSIONS, profile.getPermissions());

        // claims
        return builder.build();
    }

    private void verifyProfile(final U profile) {
        CommonHelper.assertNotNull("profile", profile);
        CommonHelper.assertNull("profile.sub", profile.getAttribute(JwtConstants.SUBJECT));
        CommonHelper.assertNull("profile.iat", profile.getAttribute(JwtConstants.ISSUE_TIME));
        CommonHelper.assertNull(INTERNAL_ROLES, profile.getAttribute(INTERNAL_ROLES));
        CommonHelper.assertNull(INTERNAL_PERMISSIONS, profile.getAttribute(INTERNAL_PERMISSIONS));
    }

    public String getSigningSecret() {
        return signingSecret;
    }

    public void setSigningSecret(final String signingSecret) {
        this.signingSecret = signingSecret;
    }

    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public void setEncryptionSecret(final String encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }

    public JWSAlgorithm getJwsAlgorithm() {
        return jwsAlgorithm;
    }

    /**
     * Only the HS256, HS384 and HS512 are currently supported.
     *
     * @param jwsAlgorithm the signing algorithm
     */
    public void setJwsAlgorithm(final JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }

    public JWEAlgorithm getJweAlgorithm() {
        return jweAlgorithm;
    }

    public void setJweAlgorithm(final JWEAlgorithm jweAlgorithm) {
        this.jweAlgorithm = jweAlgorithm;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(final EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
