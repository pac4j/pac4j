package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * RSA encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RSAEncryptionConfiguration extends AbstractEncryptionConfiguration {

    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    public RSAEncryptionConfiguration() {
    }

    public RSAEncryptionConfiguration(final KeyPair keyPair) {
        setKeyPair(keyPair);
    }

    public RSAEncryptionConfiguration(final KeyPair keyPair, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
        this.method = method;
    }

    @Override
    public boolean supports(final JWEAlgorithm algorithm, final EncryptionMethod method) {
        return algorithm != null && method != null
            && RSADecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)
            && RSADecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("method", method);

        if (!supports(this.algorithm, this.method)) {
            throw new TechnicalException("Only RSA algorithms are supported with the appropriate encryption method");
        }
    }

    @Override
    protected JWEEncrypter buildEncrypter() {
        CommonHelper.assertNotNull("publicKey", publicKey);

        return new RSAEncrypter(this.publicKey);
    }

    @Override
    protected JWEDecrypter buildDecrypter() {
        CommonHelper.assertNotNull("privateKey", privateKey);

        return new RSADecrypter(this.privateKey);
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
        return CommonHelper.toNiceString(this.getClass(), "keys", "[protected]", "algorithm", algorithm, "method", method);
    }
}
