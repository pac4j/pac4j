package org.pac4j.dpop.util;

/**
 * The DPoP constants.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public interface DPoPConstants {

    /** The prefix of the session attributes holding the DPoP key pairs as JSON JWKs, followed by the signature algorithm. */
    String KEY_SESSION_ATTRIBUTE_PREFIX = "pac4jDPoPKey|";

    /** The prefix of the session attributes holding the last nonce of a server, followed by the origin of its URL. */
    String NONCE_SESSION_ATTRIBUTE_PREFIX = "pac4jDPoPNonce|";

    /** The header carrying the proof (RFC 9449, section 4.1). */
    String DPOP_HEADER = "DPoP";

    /** The header by which a server provides a nonce (RFC 9449, section 8). */
    String DPOP_NONCE_HEADER = "DPoP-Nonce";

    /** The error by which a server requires a nonce (RFC 9449, section 8). */
    String USE_DPOP_NONCE_ERROR = "use_dpop_nonce";

    /** The member carrying the error code in an error response (RFC 6749, section 5.2). */
    String ERROR = "error";

    /** The error by which a resource server refuses a DPoP proof (RFC 9449, section 7.1). */
    String INVALID_DPOP_PROOF_ERROR = "invalid_dpop_proof";

    /** The error by which a resource server refuses an access token (RFC 6750, section 3.1). */
    String INVALID_TOKEN_ERROR = "invalid_token";

    /** The request attribute holding the error for which the DPoP authentication of the request was refused. */
    String ERROR_REQUEST_ATTRIBUTE = "pac4jDPoPError";

}
