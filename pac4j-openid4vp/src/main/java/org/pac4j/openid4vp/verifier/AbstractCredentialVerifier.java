package org.pac4j.openid4vp.verifier;

import lombok.Getter;
import org.pac4j.openid4vp.verifier.trust.IssuerEvidence;
import org.pac4j.openid4vp.verifier.trust.ResolvedIssuer;
import org.pac4j.openid4vp.verifier.trust.TrustedIssuer;
import org.pac4j.openid4vp.verifier.trust.TrustedIssuers;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * A credential verifier relying on {@link TrustedIssuers} to establish the trust in the issuer of a credential: the
 * format verifiers read the issuer evidence from the credential and verify its signature with the keys resolved here.
 *
 * <p>The format verifiers redefine its setters to return their own type, so that they chain with those of the
 * format.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
public abstract class AbstractCredentialVerifier implements CredentialVerifier {

    /** The trusted issuers, which can be shared with other verifiers; none by default. */
    private TrustedIssuers trustedIssuers = new TrustedIssuers();

    /**
     * <p>Set the trusted issuers, for instance to share them with other verifiers.</p>
     *
     * @param trustedIssuers the trusted issuers
     * @return this verifier
     */
    public AbstractCredentialVerifier setTrustedIssuers(final TrustedIssuers trustedIssuers) {
        this.trustedIssuers = trustedIssuers;
        return this;
    }

    /**
     * <p>Trust more issuers: add a definition to the {@link #trustedIssuers} of this verifier.</p>
     *
     * @param issuer the trusted issuer definition
     * @return this verifier
     */
    public AbstractCredentialVerifier addTrustedIssuer(final TrustedIssuer issuer) {
        assertNotNull("trustedIssuers", trustedIssuers);
        trustedIssuers.add(issuer);
        return this;
    }

    /**
     * <p>Resolve the issuer of a credential of the format of this verifier through its trusted issuers.</p>
     *
     * @param evidence what the credential says about its issuer
     * @return the resolved issuer, empty when no trusted issuer knows it
     */
    protected Optional<ResolvedIssuer> resolveIssuer(final IssuerEvidence evidence) {
        assertNotNull("trustedIssuers", trustedIssuers);
        return trustedIssuers.resolve(getFormat(), evidence);
    }
}
