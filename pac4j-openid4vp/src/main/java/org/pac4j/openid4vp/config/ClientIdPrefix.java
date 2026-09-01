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

    X509_SAN_DNS("x509_san_dns"),
    X509_HASH("x509_hash"),
    REDIRECT_URI("redirect_uri"),
    DECENTRALIZED_IDENTIFIER("decentralized_identifier"),
    VERIFIER_ATTESTATION("verifier_attestation"),
    OPENID_FEDERATION("openid_federation");

    private final String value;
}
