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
        assertTrue(manager.isValid(manager.generate(Instant.now())));
    }

    @Test
    void acceptsANonceGeneratedByAnotherNodeSharingTheSecret() {
        assertTrue(manager.isValid(new DPoPNonceManager(SECRET, 300).generate(Instant.now())));
    }

    @Test
    void generatesATimestampAndItsHmacBase64UrlEncoded() {
        val creation = Instant.parse("2026-01-01T12:00:00Z");

        val nonce = manager.generate(creation);

        // Reference vector for HMAC-SHA256 of the ASCII timestamp 1767268800 with SECRET.
        assertEquals("MTc2NzI2ODgwMC5FandUQ1dTREFMR3VMZlJXQjh6cVNtb01tREpLdDI2LTlUWkhZY2JZQk1Z", nonce);
    }

    @Test
    void refusesANonceOfAnotherSecret() {
        assertFalse(manager.isValid(new DPoPNonceManager("another-secret", 300).generate(Instant.now())));
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
        assertFalse(manager.shouldBeRenewed(manager.generate(Instant.now())));
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
