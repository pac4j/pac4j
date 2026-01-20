package org.pac4j.oidc.config;

import java.security.PrivateKey;

import com.nimbusds.jose.JWSAlgorithm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The configuration for the client authentication method: private_key_jwt.
 *
 * @author Jerome LELEU
 * @since 5.7.0
 */
@Getter
@Setter
@ToString(exclude = "privateKey")
public class PrivateKeyJWTClientAuthnMethodConfig {

    private JWSAlgorithm jwsAlgorithm;

    private PrivateKey privateKey;

    private String keyID;
    /** Default JWT token expiration time in seconds */
    private long validity = 60;
    /** Clock skew used to not reuse a token to close to expire */
    private int keyClockSkew = 10;
    /** Manage expiration of private key JWT when true */
    private boolean useExpiration = true;

    /**
     * <p>Constructor for PrivateKeyJWTClientAuthnMethodConfig.</p>
     */
    public PrivateKeyJWTClientAuthnMethodConfig() {}

    /**
     * <p>Constructor for PrivateKeyJWTClientAuthnMethodConfig.</p>
     *
     * @param jwsAlgorithm a {@link JWSAlgorithm} object
     * @param privateKey a {@link PrivateKey} object
     */
    public PrivateKeyJWTClientAuthnMethodConfig(final JWSAlgorithm jwsAlgorithm, final PrivateKey privateKey) {
        this.jwsAlgorithm = jwsAlgorithm;
        this.privateKey = privateKey;
    }

    /**
     * <p>Constructor for PrivateKeyJWTClientAuthnMethodConfig.</p>
     *
     * @param jwsAlgorithm a {@link JWSAlgorithm} object
     * @param privateKey a {@link PrivateKey} object
     * @param keyID a {@link String} object
     */
    public PrivateKeyJWTClientAuthnMethodConfig(final JWSAlgorithm jwsAlgorithm, final PrivateKey privateKey, final String keyID) {
        this(jwsAlgorithm, privateKey);
        this.keyID = keyID;
    }
}
