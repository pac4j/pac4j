package org.pac4j.jwt.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.security.interfaces.ECPrivateKey;

/**
 * Elliptic curve signing configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class ECSigningConfiguration extends InitializableObject implements SigningConfiguration {

    private ECPrivateKey key;

    private JWSAlgorithm algorithm = JWSAlgorithm.ES256;

    public ECSigningConfiguration() {}

    public ECSigningConfiguration(final ECPrivateKey key) {
        this.key = key;
    }

    public ECSigningConfiguration(final ECPrivateKey key, final JWSAlgorithm algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("key", key);

        if (algorithm != JWSAlgorithm.ES256 && algorithm != JWSAlgorithm.ES384 && algorithm != JWSAlgorithm.ES512) {
            throw new TechnicalException("Only the ES256, ES384 and ES512 algorithms are supported for elliptic curve signature");
        }
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();

        try {
            final JWSSigner signer = new ECDSASigner(this.key);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    public ECPrivateKey getKey() {
        return key;
    }

    public void setKey(final ECPrivateKey key) {
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
