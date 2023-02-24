package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jwt.util.JWKHelper;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * Elliptic curve signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-ec-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
public class ECSignatureConfiguration extends AbstractSignatureConfiguration {

    private ECPublicKey publicKey;

    private ECPrivateKey privateKey;

    /**
     * <p>Constructor for ECSignatureConfiguration.</p>
     */
    public ECSignatureConfiguration() {
        algorithm = JWSAlgorithm.ES256;
    }

    /**
     * <p>Constructor for ECSignatureConfiguration.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     */
    public ECSignatureConfiguration(final KeyPair keyPair) {
        this();
        setKeyPair(keyPair);
    }

    /**
     * <p>Constructor for ECSignatureConfiguration.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     * @param algorithm a {@link com.nimbusds.jose.JWSAlgorithm} object
     */
    public ECSignatureConfiguration(final KeyPair keyPair, final JWSAlgorithm algorithm) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("algorithm", algorithm);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the ES256, ES384 and ES512 algorithms are supported for elliptic curve signature");
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && ECDSAVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    /** {@inheritDoc} */
    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();
        CommonHelper.assertNotNull("privateKey", privateKey);

        try {
            val signer = new ECDSASigner(this.privateKey);
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

        val verifier = new ECDSAVerifier(this.publicKey);
        return jwt.verify(verifier);
    }

    /**
     * <p>setKeyPair.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     */
    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (ECPrivateKey) keyPair.getPrivate();
        this.publicKey = (ECPublicKey) keyPair.getPublic();
    }

    /**
     * <p>setKeysFromJwk.</p>
     *
     * @param json a {@link java.lang.String} object
     */
    public void setKeysFromJwk(final String json) {
        val pair = JWKHelper.buildECKeyPairFromJwk(json);
        this.publicKey = (ECPublicKey) pair.getPublic();
        this.privateKey = (ECPrivateKey) pair.getPrivate();
    }
}
