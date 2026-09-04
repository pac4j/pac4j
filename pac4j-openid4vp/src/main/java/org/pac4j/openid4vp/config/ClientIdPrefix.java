package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The prefix qualifying how the verifier identifier must be authenticated by the wallet.
 * In the EUDI context, {@link #X509_SAN_DNS} is used: the client identifier is the DNS name
 * of the verifier, bound to its relying party access certificate.
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
