package org.pac4j.openid4vp.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.core.util.serializer.JavaSerializer;
import org.pac4j.openid4vp.config.ResponseMode;

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

    private static final JavaSerializer SERIALIZER = new JavaSerializer();

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

    /** The authorization parameters actually sent, kept as JSON for response validation and holder binding. */
    private String requestParameters;

    /** The configured DCQL query saved as JSON for response validation, even when only a scope alias was sent. */
    private String dcqlQuery;

    /** The response mode saved when the authorization request was built. */
    private ResponseMode responseMode;

    /** The state returned outside an encrypted response. */
    private String responseState;

    private Instant createdAt;

    private Instant expiresAt;

    private Status status = Status.CREATED;

    /**
     * The nonce the wallet posted to the request URI, if it did: the request object must carry it back in a
     * {@code wallet_nonce} claim, or the wallet terminates.
     */
    private String walletNonce;

    /** The metadata the wallet posted to the request URI, if it did: a JSON object, kept raw. */
    private String walletMetadata;

    /** The ephemeral response encryption key of this transaction, in its JWK form, private part included. */
    private String encryptionKey;

    /** The raw response posted by the wallet in an encrypted response mode: the JWE of the {@code response} parameter. */
    private String rawResponse;

    /** The raw {@code vp_token} posted by the wallet in a clear response mode: a JSON object, as a string. */
    private String rawVpToken;

    /** The error the wallet answered with, if it did, and its description. */
    private String error;

    private String errorDescription;

    /** The code handed to the wallet and given back by the browser to claim the response. */
    private String responseCode;

    /**
     * <p>Whether the wallet answered, with presentations or with an error.</p>
     *
     * @return a boolean
     */
    public boolean isAnswered() {
        return rawResponse != null || rawVpToken != null || error != null;
    }

    /**
     * <p>A copy of this transaction, to read an answer into without touching the stored transaction: an
     * in-memory store hands out the very instance it holds, and an answer refused at reception must leave no
     * trace in it.</p>
     *
     * @return the copy
     */
    public VpTransaction copy() {
        return (VpTransaction) SERIALIZER.deserializeFromBytes(SERIALIZER.serializeToBytes(this));
    }

    /**
     * <p>The answer of the wallet alone, to be stored under its own key: whatever rewrites the transaction
     * meanwhile, such as a request object served again, cannot erase it. It expires with the transaction.</p>
     *
     * @return the answer, as a transaction holding only its identifier, its expiration and the response
     */
    public VpTransaction toResponse() {
        return new VpTransaction().setId(id).setExpiresAt(expiresAt).setStatus(Status.RESPONSE_RECEIVED)
            .setRawResponse(rawResponse).setRawVpToken(rawVpToken).setResponseState(responseState)
            .setError(error).setErrorDescription(errorDescription);
    }

    /**
     * <p>Take the answer of the wallet back into this transaction, when the browser claims it.</p>
     *
     * @param response the answer, as built by {@link #toResponse()}
     * @return this transaction, answered
     */
    public VpTransaction withResponse(final VpTransaction response) {
        return setStatus(Status.RESPONSE_RECEIVED).setRawResponse(response.getRawResponse())
            .setRawVpToken(response.getRawVpToken()).setResponseState(response.getResponseState())
            .setError(response.getError()).setErrorDescription(response.getErrorDescription());
    }
}
