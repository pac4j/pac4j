package org.pac4j.jwt.config;

import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;

/**
 * Encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface EncryptionConfiguration {

    /**
     * Encrypt a signed JWT.
     *
     * @param signedJWT the signed JWT
     * @return the encrypted signed JWT
     */
    String encrypt(SignedJWT signedJWT);

    /**
     * Decrypt an encrypted (signed) JWT.
     *
     * @param encryptedJWT the encrypted JWT
     * @return the decrypted (signed) JWT
     */
    SignedJWT decrypt(EncryptedJWT encryptedJWT);
}
