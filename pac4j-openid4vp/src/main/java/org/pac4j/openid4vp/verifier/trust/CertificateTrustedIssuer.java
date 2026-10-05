package org.pac4j.openid4vp.verifier.trust;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.config.properties.ResourceProperties;
import org.pac4j.core.keystore.loading.KeyStoreUtils;
import org.pac4j.openid4vp.dcql.TrustedAuthority;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.springframework.core.io.Resource;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * The issuers whose certificate chain validates, with Java PKIX, against the trusted certificates of a truststore:
 * the IACA roots of mobile documents, or the CAs of the SD-JWT VC issuers signing with a {@code x5c} chain, as the
 * high assurance profile mandates.
 *
 * <p>Only explicitly configured certificates are trusted, never the system roots nor those supplied by the wallet.
 * The resolved issuer is the subject of the leaf certificate (RFC 2253): "the Issuer of the Verifiable Digital
 * Credential is the subject of the end-entity certificate". The authority key identifiers of the validated chain are
 * reported as DCQL {@code aki} evidence.</p>
 *
 * <p>A chain which does not lead to one of these trust anchors is not known by this source, so that another source
 * may know it; a chain which does, but fails the validation (validity, signatures, revocation, key usage), is
 * rejected.</p>
 *
 * @see <a href="https://www.ietf.org/archive/id/draft-ietf-oauth-sd-jwt-vc-19.html#section-2.5">
 *     SD-JWT VC draft 19, issuer verification key discovery and validation</a>
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-6.1.1.1">
 *     OpenID4VP 1.0, authority key identifier</a>
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString(exclude = {"trustStore", "loadedTrustAnchors", "loadedCertificateRevocationLists"})
@Accessors(chain = true)
@Slf4j
public class CertificateTrustedIssuer implements TrustedIssuer {

    /**
     * Only its trusted certificate entries are used, optionally restricted to keyStoreAlias. No private key password
     * or keystore generator is used.
     *
     * <p>Its trust anchors are read at the first validation and kept: they are read again only when its resource,
     * password, type or alias change, or when {@link #setTrustStore(KeystoreProperties)} is called. A truststore
     * file replaced on disk under the same settings is thus not picked up until then.</p>
     */
    private KeystoreProperties trustStore;

    /** Check certificate revocation using the supplied CRLs and the Java provider's configured mechanisms. */
    private boolean certificateRevocationEnabled = true;

    /**
     * Optional local CRLs, their signatures and validity being checked by the PKIX provider. They are read at the first
     * validation and kept: they are read again only when {@link #setCertificateRevocationLists(List)} is called, which
     * picks up CRL files replaced on disk.
     */
    private List<ResourceProperties> certificateRevocationLists = List.of();

    /** The CRLs read from {@link #certificateRevocationLists}. */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private volatile List<X509CRL> loadedCertificateRevocationLists;

    /** The trust anchors read from {@link #trustStore}, with the settings they were read with. */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private volatile LoadedTrustAnchors loadedTrustAnchors;

    /**
     * <p>Trust the issuers whose chain validates against a truststore.</p>
     *
     * @param trustStore the truststore
     */
    public CertificateTrustedIssuer(final KeystoreProperties trustStore) {
        setTrustStore(trustStore);
    }

    /**
     * <p>Set the truststore, whose trust anchors are then read again at the next validation.</p>
     *
     * @param trustStore the truststore
     * @return this trusted issuer definition
     */
    public CertificateTrustedIssuer setTrustStore(final KeystoreProperties trustStore) {
        assertNotNull("trustStore", trustStore);
        this.trustStore = trustStore;
        this.loadedTrustAnchors = null;
        return this;
    }

    /**
     * <p>Set the local CRLs, which are then read again at the next validation.</p>
     *
     * @param certificateRevocationLists where the CRLs are
     * @return this trusted issuer definition
     */
    public CertificateTrustedIssuer setCertificateRevocationLists(final List<ResourceProperties> certificateRevocationLists) {
        assertNotNull("certificateRevocationLists", certificateRevocationLists);
        this.certificateRevocationLists = List.copyOf(certificateRevocationLists);
        this.loadedCertificateRevocationLists = null;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ResolvedIssuer> resolve(final IssuerEvidence evidence) {
        val certificates = evidence.certificateChain();
        if (certificates.isEmpty()) {
            return Optional.empty();
        }
        try {
            val anchors = loadTrustAnchors();
            val path = new ArrayList<>(certificates);
            val last = path.get(path.size() - 1);
            // a supplied trust anchor ends the path rather than being part of it
            if (path.size() > 1 && anchors.stream().anyMatch(anchor -> last.equals(anchor.getTrustedCert()))) {
                path.remove(path.size() - 1);
            }
            val top = path.get(path.size() - 1);
            if (anchors.stream().noneMatch(anchor -> anchor.getTrustedCert() != null
                && anchor.getTrustedCert().getSubjectX500Principal().equals(top.getIssuerX500Principal()))) {
                LOGGER.debug("issuer certificate chain not issued by a trust anchor of this truststore");
                return Optional.empty();
            }
            validate(certificates, path, anchors);
            val leaf = certificates.get(0);
            val subject = leaf.getSubjectX500Principal().getName();
            if (subject.isBlank()) {
                throw new OpenId4VpException("the issuer certificate must have a subject");
            }
            val authorityKeyIdentifiers = authorityKeyIdentifiers(certificates);
            return Optional.of(new ResolvedIssuer(subject, List.of(JWK.parse(leaf).toPublicJWK()),
                authorityKeyIdentifiers.isEmpty() ? Map.of() : Map.of(TrustedAuthority.AKI, authorityKeyIdentifiers)));
        } catch (final OpenId4VpException e) {
            throw e;
        } catch (final Exception e) {
            throw new OpenId4VpException("the issuer certificate chain is not valid", e);
        }
    }

    private void validate(final List<X509Certificate> certificates, final List<X509Certificate> path,
                          final Set<TrustAnchor> anchors) throws GeneralSecurityException {
        val now = new Date();
        for (val certificate : certificates) {
            certificate.checkValidity(now);
        }
        val keyUsage = certificates.get(0).getKeyUsage();
        if (keyUsage != null && !keyUsage[0]) {
            throw new OpenId4VpException("the issuer leaf certificate does not permit digital signatures");
        }
        // a new instance for each validation: the date and the certificate stores are set on it
        val parameters = new PKIXParameters(anchors);
        parameters.setDate(now);
        parameters.setRevocationEnabled(certificateRevocationEnabled);
        val crls = loadCertificateRevocationLists();
        if (!crls.isEmpty()) {
            parameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls)));
        }
        CertPathValidator.getInstance("PKIX").validate(CertificateFactory.getInstance("X.509").generateCertPath(path), parameters);
    }

    private static List<String> authorityKeyIdentifiers(final List<X509Certificate> chain) throws GeneralSecurityException {
        val identifiers = new LinkedHashSet<String>();
        for (val certificate : chain) {
            val authority = AuthorityKeyIdentifier.fromExtensions(new JcaX509CertificateHolder(certificate).getExtensions());
            if (authority != null && authority.getKeyIdentifierOctets() != null) {
                identifiers.add(Base64URL.encode(authority.getKeyIdentifierOctets()).toString());
            }
        }
        return List.copyOf(identifiers);
    }

    private List<X509CRL> loadCertificateRevocationLists() {
        val loaded = loadedCertificateRevocationLists;
        if (loaded != null) {
            return loaded;
        }
        val crls = new ArrayList<X509CRL>();
        for (val properties : certificateRevocationLists) {
            if (properties == null || !properties.isDefined()) {
                throw new OpenId4VpException("configure the resource of each certificate revocation list");
            }
            try (val input = properties.getResource().getInputStream()) {
                crls.add((X509CRL) CertificateFactory.getInstance("X.509").generateCRL(input));
            } catch (final IOException | GeneralSecurityException e) {
                throw new OpenId4VpException("cannot read the certificate revocation list: " + properties.getResource(), e);
            }
        }
        loadedCertificateRevocationLists = List.copyOf(crls);
        LOGGER.debug("certificate revocation lists loaded: {}", crls.size());
        return loadedCertificateRevocationLists;
    }

    private Set<TrustAnchor> loadTrustAnchors() throws GeneralSecurityException, IOException {
        if (trustStore.getResource() == null) {
            throw new OpenId4VpException("configure the trustStore resource of the trusted issuer certificates");
        }
        val settings = new TrustStoreSettings(trustStore.getResource(), trustStore.getKeystorePassword(),
            Objects.requireNonNullElse(trustStore.getKeyStoreType(), KeyStoreUtils.DEFAULT_KEYSTORE_TYPE),
            trustStore.getKeyStoreAlias());
        val loaded = loadedTrustAnchors;
        if (loaded != null && loaded.settings().equals(settings)) {
            return loaded.anchors();
        }
        final Set<TrustAnchor> anchors;
        try (val input = settings.resource().getInputStream()) {
            val store = KeyStoreUtils.loadKeyStore(input, settings.password(), settings.type());
            if (settings.alias() == null) {
                anchors = Set.copyOf(new PKIXParameters(store).getTrustAnchors());
            } else if (store.isCertificateEntry(settings.alias())
                && store.getCertificate(settings.alias()) instanceof X509Certificate certificate) {
                anchors = Set.of(new TrustAnchor(certificate, null));
            } else {
                throw new OpenId4VpException("the trustStore alias must identify a trusted X.509 certificate entry");
            }
        }
        loadedTrustAnchors = new LoadedTrustAnchors(settings, anchors);
        LOGGER.debug("issuer truststore loaded: {} trust anchors", anchors.size());
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
}
