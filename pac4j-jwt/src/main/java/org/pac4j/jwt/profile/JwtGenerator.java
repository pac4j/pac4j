package org.pac4j.jwt.profile;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.pac4j.core.profile.CommonProfile;
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
public class JwtGenerator<U extends CommonProfile> {

    public static final String INTERNAL_ROLES = "$int_roles";
    public static final String INTERNAL_PERMISSIONS = "$int_perms";

    private SignatureConfiguration signatureConfiguration;

    private EncryptionConfiguration encryptionConfiguration;

    private Date expirationTime;
    
    public JwtGenerator() {}

    public JwtGenerator(final SignatureConfiguration signatureConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
    }

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
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        // add claims
        for (final Map.Entry<String, Object> entry : claims.entrySet()) {
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
    public String generate(final U profile) {
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
        final JWT jwt;
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

    protected void verifyProfile(final U profile) {
        CommonHelper.assertNotNull("profile", profile);
        CommonHelper.assertNull(INTERNAL_ROLES, profile.getAttribute(INTERNAL_ROLES));
        CommonHelper.assertNull(INTERNAL_PERMISSIONS, profile.getAttribute(INTERNAL_PERMISSIONS));
    }

    protected JWTClaimsSet buildJwtClaimsSet(final U profile) {
        // claims builder with subject and issue time
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .issueTime(new Date());

        if (this.expirationTime != null) {
            builder.expirationTime(this.expirationTime);
        }
        
        // add attributes
        final Map<String, Object> attributes = profile.getAttributes();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        builder.claim(INTERNAL_ROLES, profile.getRoles());
        builder.claim(INTERNAL_PERMISSIONS, profile.getPermissions());

        builder.subject(profile.getTypedId());

        // claims
        return builder.build();
    }

    public SignatureConfiguration getSignatureConfiguration() {
        return signatureConfiguration;
    }

    public void setSignatureConfiguration(final SignatureConfiguration signatureConfiguration) {
        this.signatureConfiguration = signatureConfiguration;
    }

    public EncryptionConfiguration getEncryptionConfiguration() {
        return encryptionConfiguration;
    }

    public void setEncryptionConfiguration(final EncryptionConfiguration encryptionConfiguration) {
        this.encryptionConfiguration = encryptionConfiguration;
    }

    public Date getExpirationTime() {
        return new Date(expirationTime.getTime());
    }

    public void setExpirationTime(final Date expirationTime) {
        this.expirationTime = new Date(expirationTime.getTime());
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "signatureConfiguration", signatureConfiguration,
            "encryptionConfiguration", encryptionConfiguration);
    }
}
