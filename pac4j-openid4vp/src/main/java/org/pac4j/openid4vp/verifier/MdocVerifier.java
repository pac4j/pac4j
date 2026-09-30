package org.pac4j.openid4vp.verifier;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Verifies an ISO mdoc presentation using the optional walt.id library.
 *
 * <p>Supports ES256 issuer and device signatures (COSE algorithm -7, P-256), including the OpenID4VP 1.0
 * redirect and DC API handovers. DeviceMAC, device-signed claims and transaction data are not supported.
 * Each presentation must contain exactly one document. Only disclosed issuer-signed attributes are returned.</p>
 *
 * <p>No issuer is trusted by default. Certificate validation uses Java PKIX and explicit truststore anchors,
 * not the system roots or walt.id's certificate policy. Revocation checking is enabled by default.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class MdocVerifier implements CredentialVerifier {

    /** Trusted certificate entries only; read for each verification, optionally restricted to keyStoreAlias. */
    private KeystoreProperties trustStore;

    /** Check certificate revocation using the Java provider and optional local CRLs. */
    private boolean certificateRevocationEnabled = true;

    private List<X509CRL> certificateRevocationLists = List.of();

    /**
     * Checks the authenticated MSO status object, when present. Must throw if invalid or unavailable.
     * Without a checker, a document containing status is rejected. No status lists are downloaded automatically.
     */
    private BiConsumer<VerifiedCredential, Map<String, Object>> statusChecker;

    /** Maximum decoded presentation size, including images, in bytes. */
    private int maxPresentationSize = 2 * 1024 * 1024;

    /** {@inheritDoc} */
    @Override
    public CredentialFormat getFormat() {
        return CredentialFormat.MSO_MDOC;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getFormatMetadata() {
        return Map.of("issuerauth_alg_values", List.of(-7), "deviceauth_alg_values", List.of(-7));
    }

    /** {@inheritDoc} */
    @Override
    public VerifiedCredential verify(final String rawCredential, final VpTransaction transaction,
                                     final OpenId4VpConfiguration configuration) {
        try {
            Class.forName("id.walt.mdoc.doc.MDoc", false, MdocVerifier.class.getClassLoader());
            val now = Instant.now();
            if (transaction == null || transaction.getRequestParameters() == null || transaction.getResponseMode() == null
                || transaction.getCreatedAt() == null || transaction.getExpiresAt() == null
                || !now.isBefore(transaction.getExpiresAt())
                || !transaction.getCreatedAt().isBefore(transaction.getExpiresAt())) {
                throw new OpenId4VpException("mdoc verification requires a valid saved transaction");
            }
            if (maxPresentationSize <= 0 || rawCredential == null || rawCredential.isBlank()
                || rawCredential.length() > 4L * ((maxPresentationSize + 2L) / 3)) {
                throw new OpenId4VpException("invalid or oversized mdoc presentation");
            }
            LOGGER.debug("verifying mdoc for transaction {}", transaction.getId());
            val result = WaltMdocAdapter.verify(rawCredential, transaction, this);
            LOGGER.debug("mdoc verified for transaction {}", transaction.getId());
            return result;
        } catch (final ClassNotFoundException | LinkageError e) {
            throw new OpenId4VpException("mdoc verification requires the optional dependency "
                + "id.walt.mdoc-credentials:waltid-mdoc-credentials-jvm:0.11.0 from https://maven.waltid.dev/releases "
                + "and compatible transitive dependencies (Kotlin 2.2.21 when used with EUDI SD-JWT)", e);
        } catch (final OpenId4VpException e) {
            throw e;
        } catch (final Exception e) {
            throw new OpenId4VpException("mdoc verification failed", e);
        }
    }

    void validateIssuer(final List<X509Certificate> chain) throws GeneralSecurityException, IOException {
        val parameters = new PKIXParameters(CertificateChainValidator.loadTrustAnchors(trustStore));
        parameters.setRevocationEnabled(certificateRevocationEnabled);
        if (certificateRevocationLists != null && !certificateRevocationLists.isEmpty()) {
            parameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certificateRevocationLists)));
        }
        CertificateChainValidator.validate(chain, parameters);
    }
}
