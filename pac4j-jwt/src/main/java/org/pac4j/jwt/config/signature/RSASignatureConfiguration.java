package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RSASignatureConfiguration extends AbstractSignatureConfiguration {

    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    public RSASignatureConfiguration() {
        algorithm = JWSAlgorithm.RS256;
    }

    public RSASignatureConfiguration(final KeyPair keyPair) {
        this();
        setKeyPair(keyPair);
    }

    public RSASignatureConfiguration(final KeyPair keyPair, final JWSAlgorithm algorithm) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the RS256, RS384, RS512, PS256, PS384 and PS512 algorithms are supported for RSA signature");
        }
    }

    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && RSASSAVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();
        CommonHelper.assertNotNull("privateKey", privateKey);

        try {
            final JWSSigner signer = new RSASSASigner(this.privateKey);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();
        CommonHelper.assertNotNull("publicKey", publicKey);

        final JWSVerifier verifier = new RSASSAVerifier(this.publicKey);
        return jwt.verify(verifier);
    }

    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(final RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "keys", "[protected]", "algorithm", algorithm);
    }
}
