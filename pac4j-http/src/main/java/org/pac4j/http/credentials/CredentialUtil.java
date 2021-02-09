package org.pac4j.http.credentials;

import static java.lang.String.copyValueOf;

import org.apache.commons.codec.binary.Hex;
import org.pac4j.core.exception.TechnicalException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains utility methods related to credential encryption algorithms
 * (MD5 for http digest)
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public final class CredentialUtil {

    /**
     * Defined in rfc 2617 as H(data) = MD5(data);
     *
     * @param data data
     * @return MD5(data)
     */
    public static String encryptMD5(String data) {
        try {
            var digest = MessageDigest.getInstance("MD5");
            return copyValueOf(Hex.encodeHex(digest.digest(data.getBytes(StandardCharsets.UTF_8))));
        } catch (final NoSuchAlgorithmException ex) {
            throw new TechnicalException("Failed to instantiate an MD5 algorithm", ex);
        }
    }

    /**
     * Defined in rfc 2617 as KD(secret, data) = H(concat(secret, ":", data))
     *
     * @param data data
     * @param secret secret
     * @return H(concat(secret, ":", data));
     */
    public static String encryptMD5(String secret, String data) {
        return encryptMD5(secret + ":" + data);
    }
}
