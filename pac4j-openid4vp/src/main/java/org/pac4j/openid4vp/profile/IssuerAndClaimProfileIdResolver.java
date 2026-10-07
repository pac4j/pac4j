package org.pac4j.openid4vp.profile;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.dcql.CredentialQuery;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * Identifies the profile by the issuer and a top-level claim of a single verified SD-JWT VC, such as {@code sub}.
 * Each component is encoded with unpadded Base64url and separated by a dot, to avoid delimiter collisions.
 *
 * <p>The application must ensure that this claim is stable, unique within the issuer and never reassigned, and
 * request it in DCQL. Missing identifiers and multiple credentials are rejected.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.4">
 *     OpenID4VP 1.0, End-User Authentication using Credentials</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@ToString
@Slf4j
public class IssuerAndClaimProfileIdResolver implements ProfileIdResolver {

    private final String claim;

    /**
     * Creates the resolver.
     *
     * @param claim the name of the top-level claim identifying the subject within its issuer
     */
    public IssuerAndClaimProfileIdResolver(final String claim) {
        assertNotBlank("claim", claim);
        this.claim = claim;
    }

    /** {@inheritDoc} */
    @Override
    public String resolve(final VerifiablePresentationCredentials credentials) {
        val verified = credentials.getVerifiedCredentials().values().stream().flatMap(Collection::stream).toList();
        if (verified.size() != 1) {
            throw new OpenId4VpException("the issuer and claim profile identifier requires exactly one verified credential");
        }
        val credential = verified.get(0);
        val issuer = credential.getIssuer();
        val subject = credential.getClaims().get(claim);
        if (isBlank(issuer) || !(subject instanceof String subjectId) || isBlank(subjectId)) {
            throw new OpenId4VpException("the profile identifier requires an issuer and a non-blank string claim: " + claim);
        }
        val encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(issuer.getBytes(StandardCharsets.UTF_8)) + "."
            + encoder.encodeToString(subjectId.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Check that the query can return the claim: a credential query listing its claims must list this one; a
     * credential query without claims still returns "the claims that are mandatory to present", which may include
     * it. A mobile document never does, its claims being grouped by namespace.
     *
     * @param query the DCQL query of the client
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#selecting_claims">
     *     OpenID4VP 1.0, selecting claims</a>
     */
    @Override
    public void check(final DcqlQuery query) {
        assertTrue(query.getCredentials().stream().anyMatch(this::mayReturnClaim),
            "no SD-JWT VC credential query of the DCQL query can return the claim " + claim
                + " used as profile identifier: request it, or configure another profileIdResolver");
        LOGGER.debug("profile identifier mapping checked: subject claim={}", claim);
    }

    private boolean mayReturnClaim(final CredentialQuery credential) {
        return credential.getFormat() == CredentialFormat.SD_JWT_VC && (credential.getClaims() == null
            || credential.getClaims().isEmpty()
            || credential.getClaims().stream().anyMatch(item -> List.of(claim).equals(item.getPath())));
    }
}
