package org.pac4j.jwt.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * HMac signing configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class MacSigningConfiguration extends InitializableObject implements SigningConfiguration {

    private String secret;

    private JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    public MacSigningConfiguration() {}

    public MacSigningConfiguration(final String secret) {
        this.secret = secret;
    }

    public MacSigningConfiguration(final String secret, final JWSAlgorithm algorithm) {
        this.secret = secret;
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotBlank("secret", secret);

        if (algorithm != JWSAlgorithm.HS256 && algorithm != JWSAlgorithm.HS384 && algorithm != JWSAlgorithm.HS512) {
            throw new TechnicalException("Only the HS256, HS384 and HS512 algorithms are supported for HMac signature");
        }
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();

        try {
            final JWSSigner signer = new MACSigner(this.secret);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "secret", "[protected]", "algorithm", algorithm);
    }
}
