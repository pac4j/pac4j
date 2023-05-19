package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.util.JWKHelper;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
public class RSASignatureConfiguration extends AbstractSignatureConfiguration {

    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    /**
     * <p>Constructor for RSASignatureConfiguration.</p>
     */
    public RSASignatureConfiguration() {
        algorithm = JWSAlgorithm.RS256;
    }

    /**
     * <p>Constructor for RSASignatureConfiguration.</p>
     *
     * @param keyPair a {@link KeyPair} object
     */
    public RSASignatureConfiguration(final KeyPair keyPair) {
        this();
        setKeyPair(keyPair);
    }

    /**
     * <p>Constructor for RSASignatureConfiguration.</p>
     *
     * @param keyPair a {@link KeyPair} object
     * @param algorithm a {@link JWSAlgorithm} object
     */
    public RSASignatureConfiguration(final KeyPair keyPair, final JWSAlgorithm algorithm) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("algorithm", algorithm);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the RS256, RS384, RS512, PS256, PS384 and PS512 algorithms are supported for RSA signature");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && RSASSAVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    /** {@inheritDoc} */
    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();
        CommonHelper.assertNotNull("privateKey", privateKey);

        try {
            JWSSigner signer = new RSASSASigner(this.privateKey);
            val signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();
        CommonHelper.assertNotNull("publicKey", publicKey);

        JWSVerifier verifier = new RSASSAVerifier(this.publicKey);
        return jwt.verify(verifier);
    }

    /**
     * <p>setKeyPair.</p>
     *
     * @param keyPair a {@link KeyPair} object
     */
    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    /**
     * <p>setKeysFromJwk.</p>
     *
     * @param json a {@link String} object
     */
    public void setKeysFromJwk(final String json) {
        val pair = JWKHelper.buildRSAKeyPairFromJwk(json);
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }
}
