package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The prefix qualifying how the verifier identifier must be authenticated by the wallet.
 * The EUDI wallet, through the high assurance profile, uses {@link #X509_HASH}: the client identifier is
 * the hash of the relying party access certificate of the verifier, published with the request.
 *
 * <p>A client identifier without any prefix, "if a : character is not present in the Client Identifier",
 * names a client pre-registered with the wallet, whose metadata the wallet already holds: the request
 * must then carry no {@code client_metadata}, hence no ephemeral encryption key. That case is not offered:
 * it shares its mechanics, metadata known beforehand rather than carried, with the openid_federation
 * prefix, and will come with it.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes">
 *     OpenID4VP 1.0, client identifier prefixes, and its fallback for pre-registered clients</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public enum ClientIdPrefix {

    X509_SAN_DNS("x509_san_dns", true),
    X509_HASH("x509_hash", true),
    /**
     * The identifier is the redirect or response URI of the verifier itself. Requests using it cannot be
     * signed: there is no way for the wallet to obtain a key it can trust, so a signature would prove
     * nothing. The request parameters travel in the wallet URL instead of a request object.
     *
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#client_identifier_prefixes">
 *     OpenID4VP 1.0, defined Client Identifier Prefixes</a>
     */
    REDIRECT_URI("redirect_uri", false),
    DECENTRALIZED_IDENTIFIER("decentralized_identifier", true);

    // the verifier_attestation and openid_federation prefixes of the specification are not offered yet: the
    // first needs the attestation in a "jwt" header of the request object, the second a trust chain and the
    // federation machinery, and a prefix which cannot be honoured is worse than none

    private final String value;

    /** Whether the wallet can obtain a trusted key for this prefix, and the request is therefore signed. */
    private final boolean signedRequest;
}
