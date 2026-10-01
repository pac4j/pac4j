package org.pac4j.openid4vp.verifier.trust;

import org.pac4j.openid4vp.config.CredentialFormat;

import java.util.Optional;

/**
 * A trusted issuer definition: given what a credential says about its issuer, the keys which verify its signature,
 * if it trusts that issuer. One definition may trust several issuers, such as all those under a certificate authority.
 *
 * <p>The definitions are grouped in {@link TrustedIssuers}, which the format verifiers consult. Two are provided,
 * {@link KeysTrustedIssuer} and {@link CertificateTrustedIssuer}; other ecosystems (trusted lists, federations, issuer
 * metadata) implement this interface. A definition never downloads anything by itself unless it
 * is documented to.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public interface TrustedIssuer {

    /**
     * <p>Whether this definition recognizes issuers by their identifier: such definitions are consulted first, so
     * that a certificate chain in a credential cannot switch a configured identifier to another verification
     * process.</p>
     *
     * @return false by default
     */
    default boolean isIdentifierBased() {
        return false;
    }

    /**
     * <p>Whether this definition can resolve the issuers of a credential format: it is ignored for other formats.</p>
     *
     * @param format the credential format
     * @return true by default
     */
    default boolean supports(final CredentialFormat format) {
        return true;
    }

    /**
     * <p>Resolve the issuer of a credential, before its signature is verified with the returned keys.</p>
     *
     * @param evidence what the credential says about its issuer, not verified yet
     * @return the issuer, or empty if this definition does not know it
     * @throws org.pac4j.openid4vp.exceptions.OpenId4VpException if this definition knows the issuer but rejects the
     *     evidence: the decision is final
     */
    Optional<ResolvedIssuer> resolve(IssuerEvidence evidence);
}
