package org.pac4j.http.credentials;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains utility methods related to credential encryption algorithms
 * (MD5 for http digest)
 *
 * @author Mircea Carasel
 */
public class CredentialUtil {

    private static final char[] toHex = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Defined in rfc 2617 as H(data) = MD5(data);
     *
     * @param data data
     * @return MD5(data)
     */
    public static String H(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            return toHexString(digest.digest(data.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            // shouldn't happen
            throw new RuntimeException("Failed to instantiate an MD5 algorithm", ex);
        }
    }

    /**
     * Converts b[] to hex string.
     *
     * @param b the bte array to convert
     * @return a Hex representation of b.
     */
    public static String toHexString(byte b[]) {
        int pos = 0;
        char[] c = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            c[pos++] = toHex[(b[i] >> 4) & 0x0F];
            c[pos++] = toHex[b[i] & 0x0f];
        }
        return new String(c);
    }

    /**
     * Defined in rfc 2617 as KD(secret, data) = H(concat(secret, ":", data))
     *
     * @param data data
     * @param secret secret
     * @return H(concat(secret, ":", data));
     */
    public static String KD(String secret, String data) {
        return H(secret + ":" + data);
    }
}
