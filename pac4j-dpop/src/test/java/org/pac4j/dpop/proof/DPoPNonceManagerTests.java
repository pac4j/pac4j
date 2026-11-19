package org.pac4j.dpop.proof;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DPoPNonceManager}.
 */
final class DPoPNonceManagerTests {

    private static final String SECRET = "a-secret-shared-by-all-the-nodes";

    private final DPoPNonceManager manager = new DPoPNonceManager(SECRET, 300);

    @Test
    void acceptsANonceItGenerated() {
        assertTrue(manager.isValid(manager.generate()));
    }

    @Test
    void acceptsANonceGeneratedByAnotherNodeSharingTheSecret() {
        assertTrue(manager.isValid(new DPoPNonceManager(SECRET, 300).generate()));
    }

    @Test
    void generatesATimestampAndItsHmacBase64UrlEncoded() {
        val before = Instant.now().getEpochSecond();

        val nonce = manager.generate();

        val parts = new String(Base64.getUrlDecoder().decode(nonce), StandardCharsets.UTF_8).split("\\.");
        assertEquals(2, parts.length);
        val timestamp = Long.parseLong(parts[0]);
        assertTrue(timestamp >= before && timestamp <= Instant.now().getEpochSecond());
        // HMAC-SHA256: 32 bytes
        assertEquals(32, Base64.getUrlDecoder().decode(parts[1]).length);
        // NQCHAR only (RFC 9449, section 8.1)
        assertTrue(nonce.matches("[A-Za-z0-9_-]+"), nonce);
    }

    @Test
    void refusesANonceOfAnotherSecret() {
        assertFalse(manager.isValid(new DPoPNonceManager("another-secret", 300).generate()));
    }

    @Test
    void refusesANonceWhoseTimestampWasChanged() {
        val parts = new String(Base64.getUrlDecoder().decode(manager.generate(Instant.now().minusSeconds(1000))),
            StandardCharsets.UTF_8).split("\\.");
        val forged = Instant.now().getEpochSecond() + "." + parts[1];

        assertFalse(manager.isValid(Base64.getUrlEncoder().withoutPadding().encodeToString(forged.getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    void refusesAnExpiredNonce() {
        assertTrue(manager.isValid(manager.generate(Instant.now().minusSeconds(290))));
        assertFalse(manager.isValid(manager.generate(Instant.now().minusSeconds(310))));
    }

    @Test
    void renewsAValidNoncePastHalfOfItsMaximumAge() {
        assertFalse(manager.shouldBeRenewed(manager.generate()));
        assertFalse(manager.shouldBeRenewed(manager.generate(Instant.now().minusSeconds(140))));
        assertTrue(manager.shouldBeRenewed(manager.generate(Instant.now().minusSeconds(160))));
        assertTrue(manager.shouldBeRenewed(manager.generate(Instant.now().minusSeconds(290))));
        // an expired or unknown nonce is refused, not renewed
        assertFalse(manager.shouldBeRenewed(manager.generate(Instant.now().minusSeconds(310))));
        assertFalse(manager.shouldBeRenewed(new DPoPNonceManager("another-secret", 300).generate(Instant.now().minusSeconds(160))));
        assertFalse(manager.shouldBeRenewed("not base64 !"));
    }

    @Test
    void refusesWhatIsNotANonce() {
        assertFalse(manager.isValid(""));
        assertFalse(manager.isValid("not base64 !"));
        assertFalse(manager.isValid(Base64.getUrlEncoder().encodeToString("no-separator".getBytes(StandardCharsets.UTF_8))));
        assertFalse(manager.isValid(Base64.getUrlEncoder().encodeToString("not-a-timestamp.hmac".getBytes(StandardCharsets.UTF_8))));
    }
}
