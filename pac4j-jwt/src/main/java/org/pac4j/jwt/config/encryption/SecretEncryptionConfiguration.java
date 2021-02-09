package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.util.Base64;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Secret encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class SecretEncryptionConfiguration extends AbstractEncryptionConfiguration {

    private byte[] secret;

    public SecretEncryptionConfiguration() {
        algorithm = JWEAlgorithm.DIR;
        method = EncryptionMethod.A256GCM;
    }

    public SecretEncryptionConfiguration(final byte[] secret){
        this();
        this.secret = Arrays.copyOf(secret, secret.length);
    }

    public SecretEncryptionConfiguration(final String secret) {
        this(secret.getBytes(UTF_8));
    }

    public SecretEncryptionConfiguration(final byte[] secret, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        this.secret = Arrays.copyOf(secret,secret.length);
        this.algorithm = algorithm;
        this.method = method;
    }

    public SecretEncryptionConfiguration(final String secret, final JWEAlgorithm algorithm, final EncryptionMethod method) {
        this(secret.getBytes(UTF_8), algorithm, method);
    }

    @Override
    public boolean supports(final JWEAlgorithm algorithm, final EncryptionMethod method) {
        if (algorithm != null && method != null) {
            final var isDirect = DirectDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)
                && DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
            final var isAes = AESDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)
                && AESDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(method);
            return isDirect || isAes;
        }
        return false;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("secret", secret);
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
                return new DirectEncrypter(this.secret);
            } else {
                return new AESEncrypter(this.secret);
            }
        } catch (final KeyLengthException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    protected JWEDecrypter buildDecrypter() {
        try {
            if (DirectDecrypter.SUPPORTED_ALGORITHMS.contains(algorithm)) {
                return new DirectDecrypter(this.secret);
            } else {
                return new AESDecrypter(this.secret);
            }
        } catch (final KeyLengthException e) {
            throw new TechnicalException(e);
        }
    }

    public String getSecret() {
        return new String(secret,UTF_8);
    }

    public void setSecret(final String secret) {
        this.secret = secret.getBytes(UTF_8);
    }

    public byte[] getSecretBytes() {
        return  Arrays.copyOf(secret,secret.length);
    }

    public void setSecretBytes(final byte[] secretBytes) {
        this.secret = Arrays.copyOf(secretBytes,secretBytes.length);
    }

    public String getSecretBase64() {
        return Base64.encode(secret).toString();
    }

    public void setSecretBase64(final String secret) {
        this.secret = new Base64(secret).decode();
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "secret", "[protected]", "algorithm", algorithm, "method", method);
    }
}
