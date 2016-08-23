package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * Secret encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class SecretEncryptionConfiguration extends InitializableObject implements EncryptionConfiguration {

    private String secret;

    private JWEAlgorithm algorithm = JWEAlgorithm.DIR;

    private EncryptionMethod method = EncryptionMethod.A256GCM;

    public SecretEncryptionConfiguration() {}

    public SecretEncryptionConfiguration(final String secret) {
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
    public String encrypt(final JWT jwt) {
        init();

        if (jwt instanceof SignedJWT) {
            // Create JWE object with signed JWT as payload
            final JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(this.algorithm, this.method).contentType("JWT").build(),
                    new Payload((SignedJWT) jwt));

            try {
                // Perform encryption
                jweObject.encrypt(buildEncrypter());
            } catch (final JOSEException e) {
                throw new TechnicalException(e);
            }

            // Serialise to JWE compact form
            return jweObject.serialize();
        } else {
            // create header
            final JWEHeader header = new JWEHeader(this.algorithm, this.method);

            try {
                // encrypted jwt
                EncryptedJWT encryptedJwt = new EncryptedJWT(header, jwt.getJWTClaimsSet());

                // Perform encryption
                encryptedJwt.encrypt(buildEncrypter());

                // serialize
                return encryptedJwt.serialize();
            } catch (final JOSEException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    /**
     * Build the appropriate encrypter.
     *
     * @return the appropriate encrypter
     */
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
    public void decrypt(final EncryptedJWT encryptedJWT) throws JOSEException {
        init();

        // decrypt
        encryptedJWT.decrypt(buildDecrypter());
    }

    /**
     * Build the appropriate decrypter.
     *
     * @return the appropriate decrypter
     */
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

    public JWEAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWEAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public EncryptionMethod getMethod() {
        return method;
    }

    public void setMethod(final EncryptionMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "secret", "[protected]", "algorithm", algorithm, "method", method);
    }
}
