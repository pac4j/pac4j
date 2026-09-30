package org.pac4j.openid4vp.transaction;

/**
 * Where the presentation the browser session waits for stands, for a page polling before it comes back to
 * the callback: the wallet answers on another channel, the response URI, which the browser never sees.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public enum PresentationStatus {

    /** The transaction is live and the wallet has not answered yet. */
    PENDING,

    /** The wallet answered, with presentations or with an error: the browser can come back to the callback. */
    RECEIVED,

    /** No live transaction for this browser session: none was opened, it expired, or it was already consumed. */
    EXPIRED
}
