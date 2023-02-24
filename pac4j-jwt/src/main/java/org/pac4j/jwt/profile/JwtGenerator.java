package org.pac4j.jwt.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;

import java.util.Date;
import java.util.Map;

/**
 * Generates a JWT token from a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString
public class JwtGenerator {

    /** Constant <code>INTERNAL_ROLES="$int_roles"</code> */
    public static final String INTERNAL_ROLES = "$int_roles";
    /** Constant <code>INTERNAL_LINKEDID="$int_linkid"</code> */
    public static final String INTERNAL_LINKEDID = "$int_linkid";

    private SignatureConfiguration signatureConfiguration;

    private EncryptionConfiguration encryptionConfiguration;

    private Date expirationTime;

    /**
     * <p>Constructor for JwtGenerator.</p>
     */
    public JwtGenerator() {}

    /**
     * <p>Constructor for JwtGenerator.</p>
     *
     * @param signatureConfiguration a {@link org.pac4j.jwt.config.signature.SignatureConfiguration} object
     */
    public JwtGenerator(final SignatureConfiguration signatureConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
    }

    /**
     * <p>Constructor for JwtGenerator.</p>
     *
     * @param signatureConfiguration a {@link org.pac4j.jwt.config.signature.SignatureConfiguration} object
     * @param encryptionConfiguration a {@link org.pac4j.jwt.config.encryption.EncryptionConfiguration} object
     */
    public JwtGenerator(final SignatureConfiguration signatureConfiguration, final EncryptionConfiguration encryptionConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
        this.encryptionConfiguration = encryptionConfiguration;
    }

    /**
     * Generate a JWT from a map of claims.
     *
     * @param claims the map of claims
     * @return the created JWT
     */
    public String generate(final Map<String, Object> claims) {
        // claims builder
        val builder = new JWTClaimsSet.Builder();

        // add claims
        for (val entry : claims.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        if (this.expirationTime != null) {
            builder.expirationTime(this.expirationTime);
        }
        return internalGenerate(builder.build());
    }

    /**
     * Generate a JWT from a user profile.
     *
     * @param profile the given user profile
     * @return the created JWT
     */
    public String generate(final UserProfile profile) {
        verifyProfile(profile);

        return internalGenerate(buildJwtClaimsSet(profile));
    }

    /**
     * Generate a JWT from a claims set.
     *
     * @param claimsSet the claims set
     * @return the JWT
     */
    protected String internalGenerate(final JWTClaimsSet claimsSet) {
        JWT jwt;
        // signature?
        if (signatureConfiguration == null) {
            jwt = new PlainJWT(claimsSet);
        } else {
            jwt = signatureConfiguration.sign(claimsSet);
        }

        // encryption?
        if (encryptionConfiguration != null) {
            return encryptionConfiguration.encrypt(jwt);
        } else {
            return jwt.serialize();
        }
    }

    /**
     * <p>verifyProfile.</p>
     *
     * @param profile a {@link org.pac4j.core.profile.UserProfile} object
     */
    protected void verifyProfile(final UserProfile profile) {
        CommonHelper.assertNotNull("profile", profile);
        CommonHelper.assertNull(INTERNAL_ROLES, profile.getAttribute(INTERNAL_ROLES));
        CommonHelper.assertNull(INTERNAL_LINKEDID, profile.getAttribute(INTERNAL_LINKEDID));
    }

    /**
     * <p>buildJwtClaimsSet.</p>
     *
     * @param profile a {@link org.pac4j.core.profile.UserProfile} object
     * @return a {@link com.nimbusds.jwt.JWTClaimsSet} object
     */
    protected JWTClaimsSet buildJwtClaimsSet(final UserProfile profile) {
        // claims builder with subject and issue time
        val builder = new JWTClaimsSet.Builder()
                .issueTime(new Date());

        if (this.expirationTime != null) {
            builder.expirationTime(this.expirationTime);
        }

        // add attributes
        val attributes = profile.getAttributes();
        for (val entry : attributes.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        builder.claim(INTERNAL_ROLES, profile.getRoles());
        builder.claim(INTERNAL_LINKEDID, profile.getLinkedId());

        builder.subject(profile.getTypedId());

        // claims
        return builder.build();
    }

    /**
     * <p>Getter for the field <code>expirationTime</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getExpirationTime() {
        return new Date(expirationTime.getTime());
    }

    /**
     * <p>Setter for the field <code>expirationTime</code>.</p>
     *
     * @param expirationTime a {@link java.util.Date} object
     */
    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = new Date(expirationTime.getTime());
    }
}
