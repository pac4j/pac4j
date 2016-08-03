package org.pac4j.jwt.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.security.interfaces.RSAPrivateKey;

/**
 * RSA signing configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RSASigningConfiguration extends InitializableObject implements SigningConfiguration {

    private RSAPrivateKey key;

    private JWSAlgorithm algorithm = JWSAlgorithm.RS256;

    public RSASigningConfiguration() {}

    public RSASigningConfiguration(final RSAPrivateKey key) {
        this.key = key;
    }

    public RSASigningConfiguration(final RSAPrivateKey key, final JWSAlgorithm algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("key", key);

        if (algorithm != JWSAlgorithm.RS256 && algorithm != JWSAlgorithm.RS384 && algorithm != JWSAlgorithm.RS512 &&
                algorithm != JWSAlgorithm.PS256 && algorithm != JWSAlgorithm.PS384 && algorithm != JWSAlgorithm.PS512) {
            throw new TechnicalException("Only the RS256, RS384, RS512, PS256, PS384 and PS512 algorithms are supported for RSA signature");
        }
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();

        try {
            final JWSSigner signer = new RSASSASigner(this.key);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    public RSAPrivateKey getKey() {
        return key;
    }

    public void setKey(final RSAPrivateKey key) {
        this.key = key;
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "key", "[protected]", "algorithm", algorithm);
    }
}
