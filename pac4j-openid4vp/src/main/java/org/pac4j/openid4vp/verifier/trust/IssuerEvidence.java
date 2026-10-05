package org.pac4j.openid4vp.verifier.trust;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * What a credential says about its issuer, before its signature is verified: nothing here is trusted yet.
 *
 * @param identifier the issuer identifier, the {@code iss} of a SD-JWT VC, or null (always null for an mdoc)
 * @param keyId the identifier of the signing key, the {@code kid} header, or null
 * @param certificateChain the leaf-first certificate chain, the {@code x5c} of a SD-JWT VC or the {@code x5chain}
 *     of an mdoc; empty when there is none
 * @author Jerome LELEU
 * @since 6.6.0
 */
public record IssuerEvidence(String identifier, String keyId, List<X509Certificate> certificateChain) {

    /**
     * <p>Build the evidence, an absent chain being an empty one.</p>
     *
     * @param identifier the issuer identifier, or null
     * @param keyId the signing key identifier, or null
     * @param certificateChain the certificate chain, or null
     */
    public IssuerEvidence {
        certificateChain = certificateChain == null ? List.of() : List.copyOf(certificateChain);
    }
}
