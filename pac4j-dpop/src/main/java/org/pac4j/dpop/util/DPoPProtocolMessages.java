package org.pac4j.dpop.util;

import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;
import org.pac4j.core.util.ProtocolMessages;

/**
 * Logs DPoP exchanges on the {@code PROTOCOL_MESSAGE.DPOP} logger at debug level.
 * Incoming proofs are identified by the request method and URL.
 *
 * <p>The requests and the responses carrying them are logged by the protocol sending them (OAuth or OpenID Connect).
 * Locally generated proofs contain only the public key.</p>
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
     */
    public void receivedProof(final String method, final String url) {
        if (LOGGER.isEnabled()) {
            LOGGER.received(CLIENT, method + " " + url + " DPoP proof received");
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
