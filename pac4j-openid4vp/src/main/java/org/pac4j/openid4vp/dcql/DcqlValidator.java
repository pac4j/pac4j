package org.pac4j.openid4vp.dcql;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Matches verified credentials against the request, independently of the wallet's selection: the Verifier "MUST NOT
 * rely on the Wallet to enforce these constraints [...] and the Verifier MUST perform its own security checks on the
 * returned Credentials and Presentations".
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.9">
 *     OpenID4VP 1.0, security checks on the returned credentials and presentations</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Slf4j
public class DcqlValidator {

    /**
     * Check one credential, using only evidence established by its format verifier.
     *
     * @param query the credential query
     * @param credential the verified credential
     */
    public void validateCredential(final CredentialQuery query, final VerifiedCredential credential) {
        if (credential == null || credential.getFormat() != query.getFormat() || credential.getClaims() == null) {
            throw failure(query, "missing credential or incorrect format");
        }
        // require_cryptographic_holder_binding: "The default value is true"
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.1
        if (!Boolean.FALSE.equals(query.getRequireCryptographicHolderBinding()) && !credential.isCryptographicHolderBinding()) {
            throw failure(query, "cryptographic holder binding was not verified");
        }
        val meta = query.getMeta();
        // "The Wallet MAY return Credentials that inherit from any of the specified types": an inheritance the issuer
        // signed in aka_vcts is honoured, "Holders and Verifiers can treat the SD-JWT VC as a credential of any of these
        // types"; one only declared by the extends of the type metadata is not, no metadata being fetched
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#appendix-B.3.5
        // https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.2
        if (meta.containsKey(CredentialQuery.VCT_VALUES)) {
            val types = (List<?>) meta.get(CredentialQuery.VCT_VALUES);
            if (!types.contains(credential.getType()) && (credential.getAdditionalTypes() == null
                || credential.getAdditionalTypes().stream().noneMatch(types::contains))) {
                throw failure(query, "unexpected credential type");
            }
        }
        if (meta.containsKey(CredentialQuery.DOCTYPE_VALUE)
            && !meta.get(CredentialQuery.DOCTYPE_VALUE).equals(credential.getType())) {
            throw failure(query, "unexpected document type");
        }
        // "A Credential is identified as a match to a Trusted Authorities Query if it matches with one of the provided
        // values in one of the provided types"
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.1.1
        if (query.getTrustedAuthorities() != null && !query.getTrustedAuthorities().isEmpty()) {
            val authorities = credential.getTrustedAuthorities();
            if (authorities == null || query.getTrustedAuthorities().stream().noneMatch(authority -> {
                val verifiedValues = authorities.get(authority.getType());
                return verifiedValues != null && authority.getValues().stream().anyMatch(verifiedValues::contains);
            })) {
                throw failure(query, "no matching verified trusted authority");
            }
        }
        validateClaims(query, credential);
    }

    /**
     * "If claims is present, but claim_sets is absent, the Verifier requests all claims listed in claims"; with
     * claim_sets, one of its combinations must be satisfied.
     *
     * @param query the credential query
     * @param credential the verified credential
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.4.1">
     *     OpenID4VP 1.0, selecting claims</a>
     */
    private void validateClaims(final CredentialQuery query, final VerifiedCredential credential) {
        if (query.getClaims() == null || query.getClaims().isEmpty()) {
            return;
        }
        val matches = new HashSet<String>();
        val hasSets = query.getClaimSets() != null && !query.getClaimSets().isEmpty();
        for (val claim : query.getClaims()) {
            if (matchesClaim(claim, credential.getClaims(), credential.getFormat())) {
                matches.add(claim.getId());
            } else if (!hasSets) {
                LOGGER.debug("required claim constraint failed for query {}: claim path={}", query.getId(), claim.getPath());
                throw failure(query, "a required claim is missing or does not match");
            }
        }
        if (hasSets && query.getClaimSets().stream().noneMatch(matches::containsAll)) {
            throw failure(query, "no complete matching claim set");
        }
    }

    /**
     * Select the claims a path points to, from left to right: a string selects a key of the selected objects, null all
     * the elements and an integer one element of the selected arrays; an mdoc path is a namespace and an element
     * name. "If the set of elements currently selected is empty, abort processing and return an error": the claim does
     * not match.
     *
     * @param query the claims query
     * @param claims the verified claims
     * @param format the format of the credential
     * @return whether the claim is present, with one of the expected values when some are given
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-7.1.1">
     *     OpenID4VP 1.0, claims path pointer processing</a>
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-7.2.1">
     *     OpenID4VP 1.0, claims path pointer processing for mdocs</a>
     */
    private boolean matchesClaim(final ClaimsQuery query, final Map<String, Object> claims, final CredentialFormat format) {
        if (format == CredentialFormat.MSO_MDOC && (query.getPath().size() != 2
            || query.getPath().stream().anyMatch(segment -> !(segment instanceof String)))) {
            return false;
        }
        List<Object> selected = List.of(claims);
        for (val segment : query.getPath()) {
            val next = new ArrayList<Object>();
            for (val value : selected) {
                if (segment instanceof String name) {
                    if (!(value instanceof Map<?, ?> object)) {
                        return false;
                    }
                    if (object.containsKey(name)) {
                        next.add(object.get(name));
                    }
                } else {
                    if (!(value instanceof List<?> array)) {
                        return false;
                    }
                    if (segment == null) {
                        next.addAll(array);
                    } else if (segment instanceof Integer index && index >= 0) {
                        if (index < array.size()) {
                            next.add(array.get(index));
                        }
                    } else {
                        return false;
                    }
                }
            }
            if (next.isEmpty()) {
                return false;
            }
            selected = next;
        }
        // "Verifiers MUST treat restrictions expressed using values as a best-effort way to improve user privacy, but MUST
        // NOT rely on it for security checks": the expected values are enforced here, on the verified claims
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.4.1
        return query.getValues() == null || query.getValues().isEmpty()
            || selected.stream().anyMatch(actual -> query.getValues().stream().anyMatch(expected -> sameValue(actual, expected)));
    }

    private boolean sameValue(final Object actual, final Object expected) {
        if (actual instanceof Number number && expected instanceof Number expectedNumber) {
            try {
                return new BigDecimal(number.toString()).compareTo(new BigDecimal(expectedNumber.toString())) == 0;
            } catch (final NumberFormatException e) {
                return false;
            }
        }
        return expected.equals(actual);
    }

    /**
     * Check all required credential queries, or all required credential sets when sets are specified: "If
     * credential_sets is not provided, the Verifier requests presentations for all Credentials in credentials";
     * otherwise, "all of the Credential Set Queries in the credential_sets array where the required attribute is true
     * or omitted", each satisfied by "one of the options".
     *
     * @param query the request
     * @param returnedIds the identifiers of successfully validated credential queries
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.4.2">
     *     OpenID4VP 1.0, selecting credentials</a>
     */
    public void validateSelection(final DcqlQuery query, final Set<String> returnedIds) {
        if (query.getCredentialSets() == null || query.getCredentialSets().isEmpty()) {
            if (query.getCredentials().stream().anyMatch(credential -> !returnedIds.contains(credential.getId()))) {
                throw new OpenId4VpException("the presentation is missing a required credential query");
            }
        } else {
            for (val set : query.getCredentialSets()) {
                if (!Boolean.FALSE.equals(set.getRequired()) && set.getOptions().stream().noneMatch(returnedIds::containsAll)) {
                    throw new OpenId4VpException("the presentation does not satisfy a required credential set");
                }
            }
        }
    }

    private OpenId4VpException failure(final CredentialQuery query, final String reason) {
        return new OpenId4VpException("the presentation for " + query.getId() + " does not satisfy DCQL: " + reason);
    }
}
