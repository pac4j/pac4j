package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;

/**
 * Encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface EncryptionConfiguration {

    /**
     * Whether this encryption configuration supports this algorithm and encryption method.
     *
     * @param algorithm the encryption algorithm
     * @param method the encryption method
     * @return whether this encryption configuration supports this algorithm and encryption method
     */
    boolean supports(JWEAlgorithm algorithm, EncryptionMethod method);

    /**
     * Encrypt a JWT.
     *
     * @param jwt the JWT
     * @return the encrypted JWT
     */
    String encrypt(JWT jwt);

    /**
     * Decrypt an encrypted JWT.
     *
     * @param encryptedJWT the encrypted JWT
     * @throws com.nimbusds.jose.JOSEException exception when decrypting the JWT
     */
    void decrypt(EncryptedJWT encryptedJWT) throws JOSEException;
}
