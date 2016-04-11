package org.pac4j.jwt.profile;

import com.nimbusds.jose.EncryptionMethod;
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

    public final static String INTERNAL_ROLES = "$int_roles";
    public final static String INTERNAL_PERMISSIONS = "$int_perms";

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
        CommonHelper.assertNotNull("profile", profile);
        CommonHelper.assertNull("profile.sub", profile.getAttribute(JwtConstants.SUBJECT));
        CommonHelper.assertNull("profile.iat", profile.getAttribute(JwtConstants.ISSUE_TIME));
        CommonHelper.assertNull(INTERNAL_ROLES, profile.getAttribute(INTERNAL_ROLES));
        CommonHelper.assertNull(INTERNAL_PERMISSIONS, profile.getAttribute(INTERNAL_PERMISSIONS));
        CommonHelper.assertNotBlank("signingSecret", signingSecret);
        CommonHelper.assertNotNull("jwsAlgorithm", jwsAlgorithm);

        try {
            // Create HMAC signer
            final JWSSigner signer = new MACSigner(this.signingSecret);

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
            final JWTClaimsSet claims = builder.build();

            // signed
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), claims);

            // Apply the HMAC
            signedJWT.sign(signer);

            if (CommonHelper.isNotBlank(this.encryptionSecret)) {
                CommonHelper.assertNotNull("jweAlgorithm", jweAlgorithm);
                CommonHelper.assertNotNull("encryptionMethod", encryptionMethod);

                // Create JWE object with signed JWT as payload
                final JWEObject jweObject = new JWEObject(
                        new JWEHeader.Builder(jweAlgorithm, encryptionMethod).contentType("JWT").build(),
                        new Payload(signedJWT));

                // Perform encryption
                jweObject.encrypt(new DirectEncrypter(this.encryptionSecret.getBytes("UTF-8")));

                // Serialise to JWE compact form
                return jweObject.serialize();
            }
            return signedJWT.serialize();

        } catch (final Exception e) {
            throw new TechnicalException("Cannot generate JWT", e);
        }
    }

    public String getSigningSecret() {
        return signingSecret;
    }

    public void setSigningSecret(String signingSecret) {
        this.signingSecret = signingSecret;
    }

    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public void setEncryptionSecret(String encryptionSecret) {
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
    public void setJwsAlgorithm(JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }

    public JWEAlgorithm getJweAlgorithm() {
        return jweAlgorithm;
    }

    public void setJweAlgorithm(JWEAlgorithm jweAlgorithm) {
        this.jweAlgorithm = jweAlgorithm;
    }

    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
