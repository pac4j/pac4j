package org.pac4j.openid4vp.transaction;

import lombok.ToString;
import org.pac4j.core.store.AbstractConcurrentMapStore;

import java.time.Instant;

/**
 * Store the pending presentation requests, each one expiring on its own date.
 *
 * <p>The expiration is the transaction's business, not the store's: it is the very date sent to the wallet
 * in the request object, so there is only ever one lifetime to configure.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString(callSuper = true)
public class VpTransactionStore extends AbstractConcurrentMapStore<String, VpTransaction> {

    /** {@inheritDoc} */
    @Override
    protected Instant expiresAt(final VpTransaction transaction) {
        return transaction.getExpiresAt();
    }
}
