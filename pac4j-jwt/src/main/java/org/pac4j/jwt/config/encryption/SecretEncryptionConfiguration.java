package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.io.UnsupportedEncodingException;

/**
 * Secret encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class SecretEncryptionConfiguration extends AbstractEncryptionConfiguration {

    private String secret;

    public SecretEncryptionConfiguration() {
        algorithm = JWEAlgorithm.DIR;
        method = EncryptionMethod.A256GCM;
    }

    public SecretEncryptionConfiguration(final String secret) {
        this();
        this.secret = secret;
    }

    public SecretEncryptionConfiguration(final String secret, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        this.secret = secret;
        this.algorithm = algorithm;
        this.method = method;
    }

    @Override
    public boolean supports(final JWEAlgorithm algorithm, final EncryptionMethod method) {
        if (algorithm != null && method != null) {
            final boolean isDirect = DirectDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm) && DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
            final boolean isAes = AESDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm) && AESDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
            return isDirect || isAes;
        }
        return false;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("secret", secret);
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("method", method);

        if (!supports(this.algorithm, this.method)) {
            throw new TechnicalException("Only the direct and AES algorithms are supported with the appropriate encryption method");
        }
    }

    @Override
    protected JWEEncrypter buildEncrypter() {
        try {
            if (DirectDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)) {
                return new DirectEncrypter(this.secret.getBytes("UTF-8"));
            } else {
                return new AESEncrypter(this.secret.getBytes("UTF-8"));
            }
        } catch (final UnsupportedEncodingException | KeyLengthException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    protected JWEDecrypter buildDecrypter() {
        try {
            if (DirectDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)) {
                return new DirectDecrypter(this.secret.getBytes("UTF-8"));
            } else {
                return new AESDecrypter(this.secret.getBytes("UTF-8"));
            }
        } catch (final UnsupportedEncodingException | KeyLengthException e) {
            throw new TechnicalException(e);
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "secret", "[protected]", "algorithm", algorithm, "method", method);
    }
}
