package org.pac4j.dpop.util;

/**
 * The DPoP constants.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public interface DPoPConstants {

    /** The session attribute holding the DPoP key pair, as a JWK in JSON. */
    String KEY_SESSION_ATTRIBUTE = "pac4jDPoPKey";

    /** The prefix of the session attributes holding the last nonce of a server, followed by the origin of its URL. */
    String NONCE_SESSION_ATTRIBUTE_PREFIX = "pac4jDPoPNonce|";

    /** The header carrying the proof (RFC 9449, section 4.1). */
    String DPOP_HEADER = "DPoP";

    /** The header by which a server provides a nonce (RFC 9449, section 8). */
    String DPOP_NONCE_HEADER = "DPoP-Nonce";

    /** The prefix of the authorization header carrying a DPoP-bound access token (RFC 9449, section 7.1). */
    String DPOP_HEADER_PREFIX = "DPoP ";

    /** The error by which a server requires a nonce (RFC 9449, section 8). */
    String USE_DPOP_NONCE_ERROR = "use_dpop_nonce";

    /** The member carrying the error code in an error response (RFC 6749, section 5.2). */
    String ERROR = "error";

    /** The size of the RSA key pairs generated. */
    int RSA_KEY_SIZE = 2048;
}
