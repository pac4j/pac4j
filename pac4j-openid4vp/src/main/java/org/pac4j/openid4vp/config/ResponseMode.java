package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The way the wallet returns the presentation, which depends on how it was reached.
 *
 * <p>A wallet invoked by a URL posts its answer back to the verifier, in clear or encrypted. A wallet
 * invoked through the digital credentials API hands its answer to the browser, which gives it to the page
 * that asked: nothing is posted between the wallet and the verifier.</p>
 *
 * <p>The high assurance interoperability profile, which the EUDI architecture and reference framework
 * relies on, mandates the encrypted form of whichever is used.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_encryption">
 *     OpenID4VP 1.0, response encryption</a>
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#dc_api_request">
 *     OpenID4VP 1.0, the dc_api and dc_api.jwt response modes</a>
 * @see <a href="https://openid.net/specs/openid4vc-high-assurance-interoperability-profile-1_0.html">
 *     HAIP, "Response encryption MUST be used"</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public enum ResponseMode {

    /** Posted to the verifier, in clear. */
    DIRECT_POST("direct_post", false, false),
    /** Posted to the verifier, encrypted. */
    DIRECT_POST_JWT("direct_post.jwt", true, false),
    /** Returned through the digital credentials API, in clear. */
    DC_API("dc_api", false, true),
    /** Returned through the digital credentials API, encrypted. */
    DC_API_JWT("dc_api.jwt", true, true);

    private final String value;

    /** Whether the answer is encrypted to the verifier, on top of whatever the transport provides. */
    private final boolean encrypted;

    /** Whether the answer comes back through the digital credentials API rather than being posted. */
    private final boolean overDcApi;
}
