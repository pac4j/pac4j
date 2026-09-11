package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * How the wallet fetches the request object from the request URI.
 *
 * <p>With {@code post}, the wallet may first tell the verifier what it supports, "with information about
 * its capabilities", and hand it a nonce of its own, so that the request object is built for that very
 * wallet. Announcing it costs nothing: "Wallets not supporting the post method will send a GET request to
 * the Request URI (default behavior as defined in [RFC9101])".</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#request_uri_method_post">
 *     OpenID4VP 1.0, request URI method post</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public enum RequestUriMethod {

    /** The wallet fetches the request object with a plain GET, as RFC 9101 defines. */
    GET("get"),
    /** The wallet may post its capabilities and a nonce first, then gets a request object fit for it. */
    POST("post");

    private final String value;
}
