package org.pac4j.openid4vp.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A pending presentation request.
 *
 * <p>Unlike the other indirect clients, the nonce cannot live in the web session: the two wallet legs
 * (fetching the request object and posting the response) carry no session at all. It lives here instead,
 * the session only keeping the transaction identifier.</p>
 *
 * <p>Everything held here is a {@link String} on purpose, so that the transaction can be stored in a
 * distributed store without any custom serializer. The ephemeral encryption key is kept in its JWK form.</p>
 *
 * <p>The expiration date is held here rather than in the store, because it is also the one sent to the
 * wallet in the request object: there is only one lifetime, and this is it. The store reads it when the
 * transaction is stored.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString(exclude = {"encryptionKey", "rawResponse"})
@Accessors(chain = true)
public class VpTransaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 5811913231571905129L;

    /**
     * The lifecycle of a transaction. A transaction is removed from the store as soon as it is consumed.
     */
    public enum Status {
        /** Created by the redirection action builder, waiting for the wallet. */
        CREATED,
        /** The wallet fetched the signed request object. */
        REQUEST_RETRIEVED,
        /** The wallet posted its response, the browser can now be served. */
        RESPONSE_RECEIVED
    }

    private String id;

    private String nonce;

    private String state;

    private Instant createdAt;

    private Instant expiresAt;

    private Status status = Status.CREATED;

    /** The ephemeral response encryption key of this transaction, in its JWK form, private part included. */
    private String encryptionKey;

    /** The raw response posted by the wallet: a JWE in the {@code direct_post.jwt} response mode. */
    private String rawResponse;

    /** The code handed to the wallet and given back by the browser to claim the response. */
    private String responseCode;

}
