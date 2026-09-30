package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.keystore.loading.KeyStoreUtils;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.dcql.TrustedAuthority;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
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
 * <p>The issuer key is resolved in this order: the keys configured for the {@code iss} value in
 * {@link #trustedIssuers}, then the {@code x5c} chain validated against {@link #trustStore}, with or without
 * {@code iss}. In the latter case, "the Issuer of the Verifiable Digital Credential is the subject of the
 * end-entity certificate": the {@code iss} claim is kept as a claim but does not name the issuer.</p>
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

    private static final String AKA_VCTS = "aka_vcts";

    /** Trusted issuer identifiers mapped to keys and verified authority evidence. */
    private Map<String, SdJwtVcTrustedIssuer> trustedIssuers = Map.of();

    /**
     * Truststore for x5c credentials whose iss claim, if any, is not among the {@link #trustedIssuers}. Only trusted
     * certificate entries are used, optionally restricted to keyStoreAlias. No private key password or keystore
     * generator is used. Null disables this method.
     *
     * <p>Its trust anchors are read at the first certificate validation and kept: they are read again only when its
     * resource, password, type or alias change, or when {@link #setTrustStore(KeystoreProperties)} is called. A
     * truststore file replaced on disk under the same settings is thus not picked up until then.</p>
     */
    private KeystoreProperties trustStore;

    /** The trust anchors read from {@link #trustStore}, with the settings they were read with. */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private volatile LoadedTrustAnchors loadedTrustAnchors;

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
            // "The Issuer MUST include the typ header parameter in the SD-JWT. The typ value MUST use dc+sd-jwt"
            // https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.1
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
            // vct: "Its value MUST be a case-sensitive string"; iss is among the claims which "MUST NOT be included in
            // Disclosures and therefore MUST NOT be selectively disclosed", so a disclosure cannot add or change it
            // https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.1
            // https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.3
            if (!(claims.get("vct") instanceof String type) || type.isBlank()
                || !Objects.equals(issuerName, claims.get("iss"))) {
                throw new OpenId4VpException("the SD-JWT VC must contain a credential type and must not disclose or change its issuer");
            }
            val additionalTypes = readAdditionalTypes(jwt.getJWTClaimsSet().getClaim(AKA_VCTS), claims.get(AKA_VCTS), type);
            if (verified.getHolderProof() != null) {
                checkHolderProof(verified.getHolderProof(), transaction);
            }
            val result = new VerifiedCredential().setFormat(getFormat()).setType(type).setAdditionalTypes(additionalTypes)
                .setIssuer(issuer.name())
                .setClaims(new LinkedHashMap<>(claims))
                .setCryptographicHolderBinding(verified.getHolderProof() != null);
            val authorities = new LinkedHashMap<String, List<String>>();
            issuer.trustedAuthorities().forEach((name, values) -> authorities.put(name, List.copyOf(values)));
            result.setTrustedAuthorities(authorities);
            // status: "The information on how to read the status of the Verifiable Credential", which only the application
            // can read: without a checker, a credential which may have been revoked is refused
            // https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.3
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

    /**
     * Resolve the issuer key: the configured keys of the {@code iss} value, or else the {@code x5c} chain.
     *
     * <p>The configured keys of an {@code iss} value always win, whatever the header carries: "A Verifier MUST
     * ensure that for any given iss value, an attacker cannot influence the type of verification process used".
     * An {@code iss} value without configured keys is only ever verified through its certificate chain.</p>
     *
     * @param jwt the issuer-signed JWT, not verified yet
     * @param issuerName its {@code iss} claim, or null
     * @return the issuer and how to select its key
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.5">
     *     SD-JWT VC draft 19, issuer verification key discovery and validation</a>
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-7.3">
     *     SD-JWT VC draft 19, ecosystem-specific public key verification methods</a>
     */
    /**
     * Read the other types the issuer signed the credential as. Its value "MUST be a non-empty array of
     * case-sensitive strings" and "MUST NOT contain the value of the vct claim"; like vct, it is among the claims
     * which "MUST NOT be selectively disclosed", so it must be the one of the issuer-signed payload.
     *
     * @param signed the aka_vcts of the issuer-signed payload, or null
     * @param disclosed the aka_vcts of the claims rebuilt from the disclosures, or null
     * @param type the vct of the credential
     * @return the additional types, empty when there are none
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.2">
     *     SD-JWT VC draft 19, other credential types</a>
     * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.2.2.3">
     *     SD-JWT VC draft 19, registered JWT claims</a>
     */
    private List<String> readAdditionalTypes(final Object signed, final Object disclosed, final String type) {
        if (!Objects.equals(signed, disclosed)) {
            throw new OpenId4VpException("the SD-JWT VC aka_vcts claim must not be selectively disclosed");
        }
        if (signed == null) {
            return List.of();
        }
        if (!(signed instanceof List<?> values) || values.isEmpty()
            || values.stream().anyMatch(value -> !(value instanceof String text) || text.isBlank() || text.equals(type))) {
            throw new OpenId4VpException("the SD-JWT VC aka_vcts claim must be a non-empty array of other types than its vct");
        }
        return values.stream().map(String.class::cast).toList();
    }

    private ResolvedIssuer resolveIssuer(final SignedJWT jwt, final String issuerName) throws GeneralSecurityException, IOException {
        if (issuerName != null && issuerName.isBlank()) {
            throw new OpenId4VpException("the SD-JWT VC iss claim must not be blank");
        }
        val issuer = issuerName == null || trustedIssuers == null ? null : trustedIssuers.get(issuerName);
        if (issuer != null) {
            if (issuer.getKeys() == null || issuer.getKeys().getKeys().isEmpty()) {
                throw new OpenId4VpException("the SD-JWT VC issuer has no configured trusted keys");
            }
            return new ResolvedIssuer(issuerName, new JWSVerificationKeySelector<>(issuerAlgorithms,
                new ImmutableJWKSet<>(issuer.getKeys())), issuer.getTrustedAuthorities());
        }
        val encodedChain = jwt.getHeader().getX509CertChain();
        if (encodedChain == null || encodedChain.isEmpty()) {
            throw new OpenId4VpException(issuerName == null
                ? "the SD-JWT VC must contain an iss claim or an x5c certificate chain"
                : "the SD-JWT VC issuer has no configured trusted keys and no x5c certificate chain");
        }
        return resolveCertificateIssuer(encodedChain);
    }

    private ResolvedIssuer resolveCertificateIssuer(final List<Base64> encodedChain)
        throws GeneralSecurityException, IOException {
        val parameters = buildCertificateValidationParameters();
        val chain = new ArrayList<X509Certificate>();
        for (val encoded : encodedChain) {
            val certificate = X509CertUtils.parse(encoded.decode());
            if (certificate == null) {
                throw new OpenId4VpException("invalid certificate in the SD-JWT VC x5c header");
            }
            chain.add(certificate);
        }
        CertificateChainValidator.validate(chain, parameters);
        val leaf = chain.get(0);
        val subject = leaf.getSubjectX500Principal().getName();
        if (subject.isBlank()) {
            throw new OpenId4VpException("the SD-JWT VC issuer certificate must have a subject");
        }
        // The validated leaf key is authoritative; a kid does not select a different certificate.
        JWSKeySelector<SecurityContext> selector = (header, context) -> issuerAlgorithms.contains(header.getAlgorithm())
            ? List.of(leaf.getPublicKey()) : List.of();
        val authorityKeyIdentifiers = CertificateChainValidator.authorityKeyIdentifiers(chain);
        return new ResolvedIssuer(subject, selector,
            authorityKeyIdentifiers.isEmpty() ? Map.of() : Map.of(TrustedAuthority.AKI, authorityKeyIdentifiers));
    }

    /**
     * Set the truststore, whose trust anchors are then read again at the next certificate validation.
     *
     * @param trustStore the truststore, or null to disable the certificate validation
     * @return this verifier
     */
    public SdJwtVcVerifier setTrustStore(final KeystoreProperties trustStore) {
        this.trustStore = trustStore;
        this.loadedTrustAnchors = null;
        return this;
    }

    private PKIXParameters buildCertificateValidationParameters() throws GeneralSecurityException, IOException {
        if (trustStore == null || trustStore.getKeystoreResource() == null) {
            throw new OpenId4VpException("the SD-JWT VC issuer is not among the trustedIssuers and uses x5c: "
                + "configure a trustStore with trusted certificates");
        }
        // a new instance for each validation: the date and the certificate stores are set on it
        val parameters = new PKIXParameters(loadTrustAnchors());
        parameters.setRevocationEnabled(certificateRevocationEnabled);
        if (certificateRevocationLists != null && !certificateRevocationLists.isEmpty()) {
            parameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certificateRevocationLists)));
        }
        return parameters;
    }

    private Set<TrustAnchor> loadTrustAnchors() throws GeneralSecurityException, IOException {
        val settings = new TrustStoreSettings(trustStore.getKeystoreResource(), trustStore.getKeystorePassword(),
            Objects.requireNonNullElse(trustStore.getKeyStoreType(), KeyStoreUtils.DEFAULT_KEYSTORE_TYPE),
            trustStore.getKeyStoreAlias());
        val loaded = loadedTrustAnchors;
        if (loaded != null && loaded.settings().equals(settings)) {
            return loaded.anchors();
        }
        val anchors = CertificateChainValidator.loadTrustAnchors(trustStore);
        loadedTrustAnchors = new LoadedTrustAnchors(settings, anchors);
        LOGGER.debug("SD-JWT VC truststore loaded: {} trust anchors", anchors.size());
        return anchors;
    }

    /** What the trust anchors are read from: they are read again when any of it changes. */
    private record TrustStoreSettings(Resource resource, String password, String type, String alias) {

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "TrustStoreSettings(resource=" + resource + ", type=" + type + ", alias=" + alias + ")";
        }
    }

    private record LoadedTrustAnchors(TrustStoreSettings settings, Set<TrustAnchor> anchors) { }

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

    /**
     * Check the key binding JWT against the saved request, its signature, typ and sd_hash being verified by EUDI:
     * "the nonce claim MUST be the value of nonce from the Authorization Request; the aud claim MUST be the value of the
     * Client Identifier, except for requests over the DC API where it MUST be the Origin prefixed with origin:". Its
     * iat must be "within an acceptable window": here, between the creation of the transaction and now.
     *
     * @param proof the key binding JWT
     * @param transaction the answered transaction
     * @throws Exception if the proof cannot be read
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#appendix-B.3.6">
     *     OpenID4VP 1.0, SD-JWT VC presentation response</a>
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#appendix-A.4">
     *     OpenID4VP 1.0, DC API response</a>
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9901.html#section-4.3">
     *     RFC 9901, key binding JWT</a>
     */
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
        // aud: "The value MUST be a single string", not a JWT audience array https://www.rfc-editor.org/rfc/rfc9901.html#section-4.3
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
