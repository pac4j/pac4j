package org.pac4j.oidc.config;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.security.PrivateKey;

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
