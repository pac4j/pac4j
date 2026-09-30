package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.util.Base64URL;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.keystore.loading.KeyStoreUtils;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertPathValidator;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Shared X.509 validation for credential formats; only explicitly configured certificates are trusted.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
class CertificateChainValidator {

    Set<TrustAnchor> loadTrustAnchors(final KeystoreProperties settings) throws GeneralSecurityException, IOException {
        if (settings == null || settings.getKeystoreResource() == null) {
            throw new OpenId4VpException("configure a trustStore with trusted certificates");
        }
        try (val input = settings.getKeystoreResource().getInputStream()) {
            val store = KeyStoreUtils.loadKeyStore(input, settings.getKeystorePassword(),
                Objects.requireNonNullElse(settings.getKeyStoreType(), KeyStoreUtils.DEFAULT_KEYSTORE_TYPE));
            val alias = settings.getKeyStoreAlias();
            if (alias == null) {
                return Set.copyOf(new PKIXParameters(store).getTrustAnchors());
            }
            if (!store.isCertificateEntry(alias) || !(store.getCertificate(alias) instanceof X509Certificate certificate)) {
                throw new OpenId4VpException("the trustStore alias must identify a trusted X.509 certificate entry");
            }
            return Set.of(new TrustAnchor(certificate, null));
        }
    }

    void validate(final List<X509Certificate> certificates, final PKIXParameters parameters) throws GeneralSecurityException {
        if (certificates.isEmpty()) {
            throw new OpenId4VpException("the credential has no issuer certificate chain");
        }
        val now = new Date();
        parameters.setDate(now);
        for (val certificate : certificates) {
            certificate.checkValidity(now);
        }
        val leaf = certificates.get(0);
        val keyUsage = leaf.getKeyUsage();
        if (keyUsage != null && !keyUsage[0]) {
            throw new OpenId4VpException("the credential leaf certificate does not permit digital signatures");
        }
        val chain = new ArrayList<>(certificates);
        val last = chain.get(chain.size() - 1);
        if (chain.size() > 1 && parameters.getTrustAnchors().stream().anyMatch(anchor -> last.equals(anchor.getTrustedCert()))) {
            chain.remove(chain.size() - 1);
        }
        CertPathValidator.getInstance("PKIX").validate(CertificateFactory.getInstance("X.509").generateCertPath(chain), parameters);
    }

    List<String> authorityKeyIdentifiers(final List<X509Certificate> chain) throws GeneralSecurityException {
        val identifiers = new LinkedHashSet<String>();
        for (val certificate : chain) {
            val authority = AuthorityKeyIdentifier.fromExtensions(new JcaX509CertificateHolder(certificate).getExtensions());
            if (authority != null && authority.getKeyIdentifierOctets() != null) {
                identifiers.add(Base64URL.encode(authority.getKeyIdentifierOctets()).toString());
            }
        }
        return List.copyOf(identifiers);
    }
}
