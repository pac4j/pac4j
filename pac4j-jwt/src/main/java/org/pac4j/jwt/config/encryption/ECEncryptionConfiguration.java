package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * Elliptic curve encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class ECEncryptionConfiguration extends AbstractEncryptionConfiguration {

    private ECPublicKey publicKey;

    private ECPrivateKey privateKey;

    public ECEncryptionConfiguration() {}

    public ECEncryptionConfiguration(final KeyPair keyPair) {
        setKeyPair(keyPair);
    }

    public ECEncryptionConfiguration(final KeyPair keyPair, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
        this.method = method;
    }

    @Override
    public boolean supports(final JWEAlgorithm algorithm, final EncryptionMethod method) {
        if (algorithm != null && method != null) {
            return ECDHDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm) && ECDHDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
        }
        return false;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("method", method);

        if (!supports(this.algorithm, this.method)) {
            throw new TechnicalException("Only Elliptic-curve algorithms are supported with the appropriate encryption method");
        }
    }

    @Override
    protected JWEEncrypter buildEncrypter() {
        CommonHelper.assertNotNull("publicKey", publicKey);

        try {
            return new ECDHEncrypter(this.publicKey);
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    protected JWEDecrypter buildDecrypter() {
        CommonHelper.assertNotNull("privateKey", privateKey);

        try {
            return new ECDHDecrypter(this.privateKey);
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }


    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (ECPrivateKey) keyPair.getPrivate();
        this.publicKey = (ECPublicKey) keyPair.getPublic();
    }

    public ECPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final ECPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public ECPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(final ECPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "keys", "[protected]", "algorithm", algorithm, "method", method);
    }
}
