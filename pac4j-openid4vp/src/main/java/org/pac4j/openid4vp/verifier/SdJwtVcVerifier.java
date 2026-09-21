package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Validates an IETF SD-JWT verifiable credential.
 *
 * <p>Uses the optional EUDI SD-JWT library for disclosure and key-binding verification. Applications must
 * configure trusted issuer keys; no issuer is trusted by default. A credential with a status claim requires
 * a status checker. No remote metadata, keys or status lists are fetched by this verifier.</p>
 *
 * <p>This class can be constructed without EUDI, so applications replacing it through
 * {@link OpenId4VpConfiguration#addCredentialVerifier(CredentialVerifier)} need not add that dependency.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class SdJwtVcVerifier implements CredentialVerifier {

    /** Trusted issuer identifiers mapped to keys and verified authority evidence. */
    private Map<String, SdJwtVcTrustedIssuer> trustedIssuers = Map.of();

    /**
     * Called after cryptographic verification, only when a status claim is present. Must throw if the
     * status is invalid, unsupported or unavailable, and must authenticate any status list it uses.
     * Without this checker, credentials carrying status are rejected.
     */
    private Consumer<VerifiedCredential> statusChecker;

    private Set<JWSAlgorithm> issuerAlgorithms = Set.of(JWSAlgorithm.ES256);

    private Set<JWSAlgorithm> holderAlgorithms = Set.of(JWSAlgorithm.ES256);

    /** Clock tolerance for the holder proof, in seconds. Credential exp/nbf are checked by EUDI without skew. */
    private int clockSkewSeconds = 30;

    /** {@inheritDoc} */
    @Override
    public CredentialFormat getFormat() {
        return CredentialFormat.SD_JWT_VC;
    }

    /** {@inheritDoc} */
    @Override
    public VerifiedCredential verify(final String rawCredential, final VpTransaction transaction,
                                     final OpenId4VpConfiguration configuration) {
        checkDependency();
        try {
            checkTransaction(transaction);
            checkAlgorithms(issuerAlgorithms);
            checkAlgorithms(holderAlgorithms);
            if (rawCredential == null || rawCredential.indexOf('~') < 1) {
                throw new OpenId4VpException("invalid SD-JWT VC serialization");
            }
            val jwt = SignedJWT.parse(rawCredential.substring(0, rawCredential.indexOf('~')));
            val issuerName = jwt.getJWTClaimsSet().getIssuer();
            val issuer = trustedIssuers == null ? null : trustedIssuers.get(issuerName);
            if (issuer == null || issuer.getKeys() == null || issuer.getKeys().getKeys().isEmpty()) {
                throw new OpenId4VpException("the SD-JWT VC issuer has no configured trusted keys");
            }
            val processor = new DefaultJWTProcessor<SecurityContext>();
            processor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("dc+sd-jwt")));
            processor.setJWSKeySelector(new JWSVerificationKeySelector<>(issuerAlgorithms, new ImmutableJWKSet<>(issuer.getKeys())));
            processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(issuerName).build(), Set.of("iss", "vct")));

            val verified = EudiSdJwtAdapter.verify(rawCredential, processor);
            val claims = verified.getClaims();
            if (!(claims.get("vct") instanceof String type) || type.isBlank()
                || !(claims.get("iss") instanceof String iss) || iss.isBlank() || !issuerName.equals(iss)) {
                throw new OpenId4VpException("the SD-JWT VC must contain an issuer and a credential type");
            }
            if (verified.getHolderProof() != null) {
                checkHolderProof(verified.getHolderProof(), transaction);
            }
            val result = new VerifiedCredential().setFormat(getFormat()).setType(type).setIssuer(iss)
                .setClaims(new LinkedHashMap<>(claims))
                .setCryptographicHolderBinding(verified.getHolderProof() != null);
            val authorities = new LinkedHashMap<String, List<String>>();
            issuer.getTrustedAuthorities().forEach((name, values) -> authorities.put(name, List.copyOf(values)));
            result.setTrustedAuthorities(authorities);
            if (claims.containsKey("status")) {
                if (statusChecker == null) {
                    throw new OpenId4VpException("the SD-JWT VC contains a status claim: configure a statusChecker to validate it");
                }
                statusChecker.accept(result);
            }
            LOGGER.debug("SD-JWT VC verified for transaction {}: holder binding={}",
                transaction.getId(), result.isCryptographicHolderBinding());
            return result;
        } catch (final NoClassDefFoundError e) {
            throw missingDependency(e);
        } catch (final OpenId4VpException e) {
            throw e;
        } catch (final Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            // EUDI errors can contain disclosures. Do not expose their messages, causes or toString() values.
            LOGGER.debug("SD-JWT VC verification rejected: {}", e.getClass().getSimpleName());
            throw new OpenId4VpException("SD-JWT VC verification failed");
        }
    }

    private void checkDependency() {
        try {
            Class.forName("eu.europa.ec.eudi.sdjwt.NimbusSdJwtOps", false, SdJwtVcVerifier.class.getClassLoader());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            throw missingDependency(e);
        }
    }

    private OpenId4VpException missingDependency(final Throwable cause) {
        return new OpenId4VpException("SD-JWT VC verification requires the optional dependency "
            + "eu.europa.ec.eudi:eudi-lib-jvm-sdjwt-kt:0.20.1. Add it explicitly to your application dependencies, "
            + "including its transitive dependencies, or register another CredentialVerifier", cause);
    }

    private void checkAlgorithms(final Set<JWSAlgorithm> algorithms) {
        if (algorithms == null || algorithms.isEmpty() || algorithms.stream().anyMatch(algorithm ->
            algorithm == null || !JWSAlgorithm.Family.SIGNATURE.contains(algorithm))) {
            throw new OpenId4VpException("SD-JWT VC algorithms must be asymmetric signature algorithms");
        }
    }

    private void checkTransaction(final VpTransaction transaction) {
        val now = Instant.now();
        if (clockSkewSeconds < 0 || transaction == null || transaction.getRequestParameters() == null
            || transaction.getResponseMode() == null || transaction.getCreatedAt() == null
            || transaction.getExpiresAt() == null || !now.isBefore(transaction.getExpiresAt())
            || !transaction.getCreatedAt().isBefore(transaction.getExpiresAt())) {
            throw new OpenId4VpException("SD-JWT VC verification requires a valid saved transaction");
        }
    }

    private void checkHolderProof(final SignedJWT proof, final VpTransaction transaction) throws Exception {
        if (!holderAlgorithms.contains(proof.getHeader().getAlgorithm())) {
            throw new OpenId4VpException("the SD-JWT VC holder proof uses an unsupported signature algorithm");
        }
        val request = JSONObjectUtils.parse(transaction.getRequestParameters());
        val nonce = JSONObjectUtils.getString(request, NONCE);
        val claims = proof.getJWTClaimsSet();
        if (nonce == null || nonce.isBlank() || !nonce.equals(transaction.getNonce()) || !nonce.equals(claims.getStringClaim(NONCE))) {
            throw new OpenId4VpException("the SD-JWT VC holder proof nonce does not match the saved request");
        }
        // RFC 9901 requires aud to be a string, not a JWT audience array.
        // Nimbus normalizes aud to a list in JWTClaimsSet, losing its original JSON type.
        val audience = JSONObjectUtils.parse(proof.getPayload().toString()).get("aud");
        final boolean matches;
        if (transaction.getResponseMode().isOverDcApi()) {
            val origins = JSONObjectUtils.getStringList(request, EXPECTED_ORIGINS);
            matches = origins != null && origins.stream().anyMatch(origin -> ("origin:" + origin).equals(audience));
        } else {
            val clientId = JSONObjectUtils.getString(request, CLIENT_ID);
            matches = clientId != null && !clientId.isBlank() && clientId.equals(audience);
        }
        if (!matches) {
            throw new OpenId4VpException("the SD-JWT VC holder proof audience does not match the saved request");
        }
        val issuedAt = claims.getIssueTime().toInstant();
        if (issuedAt.isBefore(transaction.getCreatedAt().minusSeconds(clockSkewSeconds))
            || issuedAt.isAfter(Instant.now().plusSeconds(clockSkewSeconds))) {
            throw new OpenId4VpException("the SD-JWT VC holder proof was issued outside the transaction time window");
        }
    }
}
