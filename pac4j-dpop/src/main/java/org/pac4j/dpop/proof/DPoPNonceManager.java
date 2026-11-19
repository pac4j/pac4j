package org.pac4j.dpop.proof;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

/**
 * Generates and checks the nonces a server provides for the DPoP proofs (RFC 9449, sections 8 and 9).
 *
 * <p>A nonce is {@code base64url(timestamp + "." + base64url(HMAC-SHA256(secret, timestamp)))}, the timestamp being
 * the time at the server, in seconds: nothing is stored, and all the nodes sharing the secret accept the nonces
 * of each other.</p>
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@RequiredArgsConstructor
public class DPoPNonceManager {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;

    private final int maxAgeSeconds;

    /**
     * Generate a nonce.
     *
     * @return the nonce
     */
    public String generate() {
        return generate(Instant.now());
    }

    /**
     * Generate a nonce created at a given time.
     *
     * @param creation the creation time
     * @return the nonce
     */
    public String generate(final Instant creation) {
        val timestamp = String.valueOf(creation.getEpochSecond());
        return encode((timestamp + "." + sign(timestamp)).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Whether a nonce was generated with the secret, less than the maximum age ago.
     *
     * @param nonce the nonce
     * @return whether it is valid
     */
    public boolean isValid(final String nonce) {
        val age = age(nonce);
        return age != null && age <= maxAgeSeconds;
    }

    /**
     * Whether a valid nonce has passed half of its maximum age: the server then provides a new one, before the
     * client is refused for an expired nonce (RFC 9449, section 8.2).
     *
     * @param nonce the nonce
     * @return whether a new nonce should be provided
     */
    public boolean shouldBeRenewed(final String nonce) {
        val age = age(nonce);
        return age != null && age <= maxAgeSeconds && 2 * age >= maxAgeSeconds;
    }

    /**
     * Compute the age of a nonce.
     *
     * @param nonce the nonce
     * @return its age in seconds, or null when it was not generated with the secret
     */
    private Long age(final String nonce) {
        try {
            val parts = new String(Base64.getUrlDecoder().decode(nonce), StandardCharsets.UTF_8).split("\\.", 2);
            if (parts.length != 2 || !MessageDigest.isEqual(sign(parts[0]).getBytes(StandardCharsets.UTF_8),
                parts[1].getBytes(StandardCharsets.UTF_8))) {
                return null;
            }
            return Instant.now().getEpochSecond() - Long.parseLong(parts[0]);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    private String sign(final String timestamp) {
        try {
            val mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return encode(mac.doFinal(timestamp.getBytes(StandardCharsets.UTF_8)));
        } catch (final GeneralSecurityException e) {
            throw new TechnicalException("Cannot sign the DPoP nonce", e);
        }
    }

    private String encode(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
