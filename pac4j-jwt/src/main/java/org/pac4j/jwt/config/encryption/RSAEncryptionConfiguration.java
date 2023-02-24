package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
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
 * RSA encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
public class RSAEncryptionConfiguration extends AbstractEncryptionConfiguration {

    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    /**
     * <p>Constructor for RSAEncryptionConfiguration.</p>
     */
    public RSAEncryptionConfiguration() {
    }

    /**
     * <p>Constructor for RSAEncryptionConfiguration.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     */
    public RSAEncryptionConfiguration(final KeyPair keyPair) {
        setKeyPair(keyPair);
    }

    /**
     * <p>Constructor for RSAEncryptionConfiguration.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     * @param algorithm a {@link com.nimbusds.jose.JWEAlgorithm} object
     * @param method a {@link com.nimbusds.jose.EncryptionMethod} object
     */
    public RSAEncryptionConfiguration(final KeyPair keyPair, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
        this.method = method;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final JWEAlgorithm algorithm, final EncryptionMethod method) {
        return algorithm != null && method != null
            && RSADecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)
            && RSADecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("method", method);

        if (!supports(this.algorithm, this.method)) {
            throw new TechnicalException("Only RSA algorithms are supported with the appropriate encryption method");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected JWEEncrypter buildEncrypter() {
        CommonHelper.assertNotNull("publicKey", publicKey);

        return new RSAEncrypter(this.publicKey);
    }

    /** {@inheritDoc} */
    @Override
    protected JWEDecrypter buildDecrypter() {
        CommonHelper.assertNotNull("privateKey", privateKey);

        return new RSADecrypter(this.privateKey);
    }

    /**
     * <p>setKeyPair.</p>
     *
     * @param keyPair a {@link java.security.KeyPair} object
     */
    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
    }

    /**
     * <p>setKeysFromJwk.</p>
     *
     * @param json a {@link java.lang.String} object
     */
    public void setKeysFromJwk(final String json) {
        val pair = JWKHelper.buildRSAKeyPairFromJwk(json);
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }
}
