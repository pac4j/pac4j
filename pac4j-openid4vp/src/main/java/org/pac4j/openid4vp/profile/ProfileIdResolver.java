package org.pac4j.openid4vp.profile;

import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.dcql.DcqlQuery;

/**
 * Derives the profile identifier from the verified credentials. Set on the
 * {@link org.pac4j.openid4vp.config.OpenId4VpConfiguration}, as the SAML client sets the attribute used as
 * identifier on its configuration.
 *
 * <p>The identifier should be "stable, unique within its issuer and never reassigned": which claim provides
 * it, if any, depends on the credentials and on their ecosystem. {@link #issuerAndClaim(String)} covers the
 * common case of a subject claim.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4">
 *     OpenID4VP 1.0, End-User Authentication using Credentials</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@FunctionalInterface
public interface ProfileIdResolver {

    /**
     * The profile identifier made of the issuer and a top-level claim of a single verified SD-JWT VC.
     *
     * @param claim the name of the claim identifying the subject within its issuer, such as {@code sub}
     * @return the resolver
     */
    static ProfileIdResolver issuerAndClaim(final String claim) {
        return new IssuerAndClaimProfileIdResolver(claim);
    }

    /**
     * Derive the profile identifier.
     *
     * @param credentials the validated presentation, indexed by DCQL credential query identifier
     * @return the identifier, never blank
     */
    String resolve(VerifiablePresentationCredentials credentials);

    /**
     * Check, when the client initializes, that the DCQL query can return what {@link #resolve} needs, so that
     * a query which never could fails at startup rather than at every login. Nothing is checked by default.
     *
     * @param query the DCQL query of the client
     */
    default void check(final DcqlQuery query) {
    }
}
