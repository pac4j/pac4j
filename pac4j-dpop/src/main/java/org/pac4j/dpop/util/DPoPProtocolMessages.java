package org.pac4j.dpop.util;

import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;
import org.pac4j.core.util.ProtocolMessages;

/**
 * The DPoP messages exchanged, logged raw on the {@code PROTOCOL_MESSAGE.DPOP} logger, at debug level: the proofs sent
 * to the servers and the nonces received from them and, on the server side, the proofs received from the clients
 * and the nonces sent to them.
 *
 * <p>The requests and the responses carrying them are logged by the protocol sending them (OAuth or OpenID Connect).
 * A proof holds the public key only: the private key is never logged.</p>
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@UtilityClass
public class DPoPProtocolMessages {

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages("DPOP");

    /** The other party: the authorization server or a resource server. */
    public static final String SERVER = "server";

    /** The other party, on the server side: the client sending the requests. */
    public static final String CLIENT = "client";

    /**
     * <p>Log a proof sent to a server.</p>
     *
     * @param method the method of the request
     * @param url the URL of the request, without query string
     * @param proof the proof
     */
    public void sentProof(final String method, final String url, final SignedJWT proof) {
        if (LOGGER.isEnabled()) {
            LOGGER.sent(SERVER, method + " " + url + " DPoP: " + proof.serialize());
        }
    }

    /**
     * <p>Log a nonce received from a server.</p>
     *
     * @param server the origin of the server
     * @param nonce the nonce
     * @param required whether the server refused the request to require this nonce
     */
    public void receivedNonce(final String server, final String nonce, final boolean required) {
        if (LOGGER.isEnabled()) {
            LOGGER.received(SERVER, server + " DPoP-Nonce: " + nonce
                + (required ? " (use_dpop_nonce: the request is sent again)" : ""));
        }
    }

    /**
     * <p>Log a proof received from a client.</p>
     *
     * @param method the method of the request
     * @param url the URL of the request, without query string
     * @param proof the proof, as received
     */
    public void receivedProof(final String method, final String url, final String proof) {
        if (LOGGER.isEnabled()) {
            LOGGER.received(CLIENT, method + " " + url + " DPoP: " + proof);
        }
    }

    /**
     * <p>Log a nonce sent to a client.</p>
     *
     * @param nonce the nonce
     * @param required whether the request is refused to require this nonce
     */
    public void sentNonce(final String nonce, final boolean required) {
        if (LOGGER.isEnabled()) {
            LOGGER.sent(CLIENT, "DPoP-Nonce: " + nonce + (required ? " (use_dpop_nonce)" : ""));
        }
    }
}
