package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.keystore.loading.KeyStoreUtils;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Validates an IETF SD-JWT verifiable credential.
 *
 * <p>Uses the optional EUDI SD-JWT library for disclosure and key-binding verification. Applications must
 * configure trusted issuer keys or a certificate truststore; no issuer is trusted by default. A credential
 * with a status claim requires a status checker. No remote metadata, keys or status lists are fetched by this verifier.
 * Certificate revocation checks are enabled by default and use the Java PKIX provider.</p>
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
     * Truststore for x5c credentials without an iss claim. Only trusted certificate entries are used, optionally
     * restricted to keyStoreAlias. No private key password or keystore generator is used. Null disables this method.
     */
    private KeystoreProperties trustStore;

    /** Check certificate revocation using supplied CRLs and the Java provider's configured mechanisms. */
    private boolean certificateRevocationEnabled = true;

    /** Optional local CRLs; their signatures and validity are checked by the PKIX provider. */
    private List<X509CRL> certificateRevocationLists = List.of();

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
            val issuer = resolveIssuer(jwt, issuerName);
            val processor = new DefaultJWTProcessor<SecurityContext>();
            processor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("dc+sd-jwt")));
            processor.setJWSKeySelector(issuer.keySelector());
            val expectedClaims = new JWTClaimsSet.Builder();
            if (issuerName != null) {
                expectedClaims.issuer(issuerName);
            }
            processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                expectedClaims.build(), issuerName == null ? Set.of("vct") : Set.of("iss", "vct")));

            val verified = EudiSdJwtAdapter.verify(rawCredential, processor);
            val claims = verified.getClaims();
            if (!(claims.get("vct") instanceof String type) || type.isBlank()
                || !Objects.equals(issuerName, claims.get("iss"))) {
                throw new OpenId4VpException("the SD-JWT VC must contain a credential type and must not disclose or change its issuer");
            }
            if (verified.getHolderProof() != null) {
                checkHolderProof(verified.getHolderProof(), transaction);
            }
            val result = new VerifiedCredential().setFormat(getFormat()).setType(type).setIssuer(issuer.name())
                .setClaims(new LinkedHashMap<>(claims))
                .setCryptographicHolderBinding(verified.getHolderProof() != null);
            val authorities = new LinkedHashMap<String, List<String>>();
            issuer.trustedAuthorities().forEach((name, values) -> authorities.put(name, List.copyOf(values)));
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
            LOGGER.debug("SD-JWT VC verification rejected", e);
            throw new OpenId4VpException("SD-JWT VC verification failed", e);
        }
    }

    private ResolvedIssuer resolveIssuer(final SignedJWT jwt, final String issuerName) throws GeneralSecurityException, IOException {
        if (issuerName == null) {
            return resolveCertificateIssuer(jwt);
        }
        if (issuerName.isBlank()) {
            throw new OpenId4VpException("the SD-JWT VC iss claim must not be blank");
        }
        val issuer = trustedIssuers == null ? null : trustedIssuers.get(issuerName);
        if (issuer == null || issuer.getKeys() == null || issuer.getKeys().getKeys().isEmpty()) {
            throw new OpenId4VpException("the SD-JWT VC issuer has no configured trusted keys");
        }
        return new ResolvedIssuer(issuerName,
            new JWSVerificationKeySelector<>(issuerAlgorithms, new ImmutableJWKSet<>(issuer.getKeys())), issuer.getTrustedAuthorities());
    }

    private ResolvedIssuer resolveCertificateIssuer(final SignedJWT jwt) throws GeneralSecurityException, IOException {
        val encodedChain = jwt.getHeader().getX509CertChain();
        if (encodedChain == null || encodedChain.isEmpty()) {
            throw new OpenId4VpException("the SD-JWT VC must contain an iss claim or an x5c certificate chain");
        }
        val parameters = buildCertificateValidationParameters();
        val now = new Date();
        parameters.setDate(now);
        val chain = new ArrayList<X509Certificate>();
        for (val encoded : encodedChain) {
            val certificate = X509CertUtils.parse(encoded.decode());
            if (certificate == null) {
                throw new OpenId4VpException("invalid certificate in the SD-JWT VC x5c header");
            }
            certificate.checkValidity(now);
            chain.add(certificate);
        }
        val leaf = chain.get(0);
        val keyUsage = leaf.getKeyUsage();
        if (keyUsage != null && !keyUsage[0]) {
            throw new OpenId4VpException("the SD-JWT VC leaf certificate does not permit digital signatures");
        }
        // A supplied root is removed only when it is already an explicitly configured trust anchor.
        val last = chain.get(chain.size() - 1);
        if (chain.size() > 1 && parameters.getTrustAnchors().stream().anyMatch(anchor -> last.equals(anchor.getTrustedCert()))) {
            chain.remove(chain.size() - 1);
        }
        val path = CertificateFactory.getInstance("X.509").generateCertPath(chain);
        CertPathValidator.getInstance("PKIX").validate(path, parameters);
        val subject = leaf.getSubjectX500Principal().getName();
        if (subject.isBlank()) {
            throw new OpenId4VpException("the SD-JWT VC issuer certificate must have a subject");
        }
        // The validated leaf key is authoritative; a kid does not select a different certificate.
        JWSKeySelector<SecurityContext> selector = (header, context) -> issuerAlgorithms.contains(header.getAlgorithm())
            ? List.of(leaf.getPublicKey()) : List.of();
        return new ResolvedIssuer(subject, selector, Map.of());
    }

    private PKIXParameters buildCertificateValidationParameters() throws GeneralSecurityException, IOException {
        if (trustStore == null || trustStore.getKeystoreResource() == null) {
            throw new OpenId4VpException("the SD-JWT VC uses x5c without iss: configure a trustStore with trusted certificates");
        }
        final PKIXParameters parameters;
        try (val input = trustStore.getKeystoreResource().getInputStream()) {
            val type = Objects.requireNonNullElse(trustStore.getKeyStoreType(), KeyStoreUtils.DEFAULT_KEYSTORE_TYPE);
            val keyStore = KeyStoreUtils.loadKeyStore(input, trustStore.getKeystorePassword(), type);
            val alias = trustStore.getKeyStoreAlias();
            if (alias == null) {
                parameters = new PKIXParameters(keyStore);
            } else {
                if (!keyStore.isCertificateEntry(alias) || !(keyStore.getCertificate(alias) instanceof X509Certificate certificate)) {
                    throw new OpenId4VpException("the trustStore alias must identify a trusted X.509 certificate entry");
                }
                parameters = new PKIXParameters(Set.of(new TrustAnchor(certificate, null)));
            }
        }
        parameters.setRevocationEnabled(certificateRevocationEnabled);
        if (certificateRevocationLists != null && !certificateRevocationLists.isEmpty()) {
            parameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certificateRevocationLists)));
        }
        return parameters;
    }

    private record ResolvedIssuer(String name, JWSKeySelector<SecurityContext> keySelector,
                                  Map<String, List<String>> trustedAuthorities) { }

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
