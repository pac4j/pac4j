package org.pac4j.openid4vp.verifier.trust;

import lombok.ToString;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * The trusted issuers of a format verifier. The same instance can be shared by several verifiers, each one ignoring
 * the definitions which do not support its format.
 *
 * <p>The definitions recognizing issuers by their identifier are consulted first, whatever the order they were added
 * in, then the others in the order they were added. The first definition which knows the issuer decides, and its
 * decision is final: "A Verifier MUST ensure that for any given iss value, an attacker cannot influence the type of
 * verification process used". A credential no definition knows is rejected by the verifier.</p>
 *
 * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-7.3">
 *     SD-JWT VC draft 19, ecosystem-specific public key verification methods</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@ToString
public class TrustedIssuers {

    private final List<TrustedIssuer> issuers = new ArrayList<>();

    /**
     * <p>Build the trusted issuers.</p>
     *
     * @param issuers the trusted issuer definitions, none to add them later
     */
    public TrustedIssuers(final TrustedIssuer... issuers) {
        for (val issuer : issuers) {
            add(issuer);
        }
    }

    /**
     * <p>Trust more issuers.</p>
     *
     * @param issuer the trusted issuer definition
     * @return these trusted issuers
     */
    public TrustedIssuers add(final TrustedIssuer issuer) {
        assertNotNull("issuer", issuer);
        issuers.add(issuer);
        return this;
    }

    /**
     * <p>The trusted issuer definitions, in the order they were added.</p>
     *
     * @return an unmodifiable view of the definitions
     */
    public List<TrustedIssuer> getIssuers() {
        return Collections.unmodifiableList(issuers);
    }

    /**
     * <p>Resolve the issuer of a credential: the definitions supporting its format and recognizing issuers by their
     * identifier first, then the others. The first definition which knows the issuer decides.</p>
     *
     * @param format the credential format
     * @param evidence what the credential says about its issuer
     * @return the resolved issuer, empty when no definition knows it
     */
    public Optional<ResolvedIssuer> resolve(final CredentialFormat format, final IssuerEvidence evidence) {
        val ordered = issuers.stream().filter(issuer -> issuer.supports(format))
            .sorted(Comparator.comparing(issuer -> !issuer.isIdentifierBased())).toList();
        for (val issuer : ordered) {
            val resolved = issuer.resolve(evidence);
            if (resolved.isPresent()) {
                return resolved;
            }
        }
        return Optional.empty();
    }
}
