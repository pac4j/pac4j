package org.pac4j.openid4vp.transaction;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test {@link VpTransactionStore}: the expiration must come from the transaction itself, not from a
 * timeout of the store.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class VpTransactionStoreTests {

    private static final String ID = "tx-1";

    @Test
    void testLiveTransactionIsReturned() {
        val store = new VpTransactionStore();
        store.set(ID, new VpTransaction().setId(ID).setNonce("nonce")
            .setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES)));

        assertEquals("nonce", store.get(ID).get().getNonce());
    }

    @Test
    void testTransactionExpiredOnItsOwnDate() {
        val store = new VpTransactionStore();
        store.set(ID, new VpTransaction().setId(ID)
            .setExpiresAt(Instant.now().minus(1, ChronoUnit.SECONDS)));

        assertTrue(store.get(ID).isEmpty());
    }
}
