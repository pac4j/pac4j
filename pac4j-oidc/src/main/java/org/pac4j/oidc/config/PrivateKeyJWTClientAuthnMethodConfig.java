package org.pac4j.oidc.config;

import com.nimbusds.jose.JWSAlgorithm;
import org.pac4j.core.util.CommonHelper;

import java.security.PrivateKey;

/**
 * The configuration for the client authentication method: private_key_jwt.
 *
 * @author Jerome LELEU
 * @since 5.7.0
 */
public class PrivateKeyJWTClientAuthnMethodConfig {

    private JWSAlgorithm jwsAlgorithm;

    private PrivateKey privateKey;

    private String keyID;

    public PrivateKeyJWTClientAuthnMethodConfig() {}

    public PrivateKeyJWTClientAuthnMethodConfig(final JWSAlgorithm jwsAlgorithm, final PrivateKey privateKey) {
        this.jwsAlgorithm = jwsAlgorithm;
        this.privateKey = privateKey;
    }

    public PrivateKeyJWTClientAuthnMethodConfig(final JWSAlgorithm jwsAlgorithm, final PrivateKey privateKey, final String keyID) {
        this(jwsAlgorithm, privateKey);
        this.keyID = keyID;
    }

    public JWSAlgorithm getJwsAlgorithm() {
        return jwsAlgorithm;
    }

    public void setJwsAlgorithm(final JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(final PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(final String keyID) {
        this.keyID = keyID;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(getClass(), "jwsAlgorithm", jwsAlgorithm, "keyID", keyID);
    }
}
