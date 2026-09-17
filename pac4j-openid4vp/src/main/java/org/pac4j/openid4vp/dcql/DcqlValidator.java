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
 * Matches verified credentials against the request, independently of the wallet's selection.
 *
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
        LOGGER.debug("checking DCQL constraints for query {}: format={}, holder binding required={}", query.getId(), query.getFormat(),
            !Boolean.FALSE.equals(query.getRequireCryptographicHolderBinding()));
        if (credential == null || credential.getFormat() != query.getFormat() || credential.getClaims() == null) {
            throw failure(query, "missing credential or incorrect format");
        }
        if (!Boolean.FALSE.equals(query.getRequireCryptographicHolderBinding()) && !credential.isCryptographicHolderBinding()) {
            throw failure(query, "cryptographic holder binding was not verified");
        }
        val meta = query.getMeta();
        if (meta.containsKey(CredentialQuery.VCT_VALUES)
            && !((List<?>) meta.get(CredentialQuery.VCT_VALUES)).contains(credential.getType())) {
            throw failure(query, "unexpected credential type");
        }
        if (meta.containsKey(CredentialQuery.DOCTYPE_VALUE)
            && !meta.get(CredentialQuery.DOCTYPE_VALUE).equals(credential.getType())) {
            throw failure(query, "unexpected document type");
        }
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
        LOGGER.debug("DCQL credential constraints satisfied for query {}", query.getId());
    }

    private void validateClaims(final CredentialQuery query, final VerifiedCredential credential) {
        if (query.getClaims() == null || query.getClaims().isEmpty()) {
            return;
        }
        val matches = new HashSet<String>();
        val hasSets = query.getClaimSets() != null && !query.getClaimSets().isEmpty();
        LOGGER.debug("checking {} claim queries for query {}: claim alternatives={}", query.getClaims().size(), query.getId(), hasSets);
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
        // Wallet-side value matching is best effort. Enforce the configured values on verified data here.
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
     * Check all required credential queries, or all required credential sets when sets are specified.
     *
     * @param query the request
     * @param returnedIds the identifiers of successfully validated credential queries
     */
    public void validateSelection(final DcqlQuery query, final Set<String> returnedIds) {
        LOGGER.debug("checking DCQL selection: {} returned query identifiers out of {} requested", returnedIds.size(),
            query.getCredentials().size());
        if (query.getCredentialSets() == null || query.getCredentialSets().isEmpty()) {
            if (query.getCredentials().stream().anyMatch(credential -> !returnedIds.contains(credential.getId()))) {
                LOGGER.debug("DCQL selection rejected: a required credential query is missing");
                throw new OpenId4VpException("the presentation is missing a required credential query");
            }
        } else {
            for (val set : query.getCredentialSets()) {
                if (!Boolean.FALSE.equals(set.getRequired()) && set.getOptions().stream().noneMatch(returnedIds::containsAll)) {
                    LOGGER.debug("DCQL selection rejected: no complete option for a required credential set");
                    throw new OpenId4VpException("the presentation does not satisfy a required credential set");
                }
            }
        }
        LOGGER.debug("DCQL credential selection satisfied");
    }

    private OpenId4VpException failure(final CredentialQuery query, final String reason) {
        LOGGER.debug("DCQL credential rejected for query {}: {}", query.getId(), reason);
        return new OpenId4VpException("the presentation for " + query.getId() + " does not satisfy DCQL: " + reason);
    }
}
