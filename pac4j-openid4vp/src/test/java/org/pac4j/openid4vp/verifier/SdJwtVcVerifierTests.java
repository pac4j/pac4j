package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.val;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.config.properties.ResourceProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.dcql.CredentialQuery;
import org.pac4j.openid4vp.dcql.DcqlValidator;
import org.pac4j.openid4vp.dcql.TrustedAuthority;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.trust.CertificateTrustedIssuer;
import org.pac4j.openid4vp.verifier.trust.KeysTrustedIssuer;
import org.pac4j.openid4vp.verifier.trust.TrustedIssuer;
import org.pac4j.openid4vp.verifier.trust.TrustedIssuers;
import org.springframework.core.io.FileSystemResource;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real issuer and holder signatures, with independently constructed disclosures and presentation hashes.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class SdJwtVcVerifierTests {

    private static final String ISSUER = "https://issuer.example";
    private static final String AUDIENCE = "x509_hash:verifier";
    private ECKey issuerKey;
    private ECKey holderKey;
    private SdJwtVcVerifier verifier;
    private CertificateTrustedIssuer certificateIssuers;
    private VpTransaction transaction;
    private Map<String, Object> issuerClaims;
    private List<String> disclosures;
    private Map<String, Object> proofClaims;
    private List<Base64> issuerCertificateChain;

    @TempDir
    private Path directory;

    @BeforeEach
    void setUp() throws Exception {
        issuerKey = new ECKeyGenerator(Curve.P_256).keyID("issuer-key").generate();
        holderKey = new ECKeyGenerator(Curve.P_256).generate();
        verifier = new SdJwtVcVerifier().setTrustedIssuers(new TrustedIssuers(
            new KeysTrustedIssuer(ISSUER, new JWKSet(issuerKey.toPublicJWK()))
                .setTrustedAuthorities(Map.of("etsi_tl", List.of("https://trusted-list.example")))));
        transaction = new VpTransaction().setId("transaction").setNonce("nonce")
            .setCreatedAt(Instant.now().minusSeconds(5)).setExpiresAt(Instant.now().plusSeconds(300))
            .setResponseMode(ResponseMode.DIRECT_POST_JWT)
            .setRequestParameters(JSONObjectUtils.toJSONString(Map.of("client_id", AUDIENCE, "nonce", "nonce")));
        issuerClaims = new LinkedHashMap<>(Map.of("iss", ISSUER, "vct", "urn:example:pid", "sub", "user",
            "iat", Instant.now().minusSeconds(60).getEpochSecond(), "exp", Instant.now().plusSeconds(600).getEpochSecond(),
            "cnf", Map.of("jwk", holderKey.toPublicJWK().toJSONObject())));
        disclosures = new ArrayList<>();
        val name = disclose("salt-name", "given_name", "Alice");
        disclosures.add(name);
        issuerClaims.put("_sd", List.of(hash(name)));
        proofClaims = new LinkedHashMap<>(Map.of("nonce", "nonce", "aud", AUDIENCE, "iat", Instant.now().getEpochSecond()));
    }

    private String disclose(final String... values) {
        return Base64URL.encode(com.nimbusds.jose.util.JSONArrayUtils.toJSONString(List.of(values))).toString();
    }

    private String hash(final String value) throws Exception {
        return Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.US_ASCII))).toString();
    }

    private String sign(final Map<String, Object> claims, final ECKey key, final String type) throws Exception {
        val header = new JWSHeader.Builder(JWSAlgorithm.ES256).type(new JOSEObjectType(type)).keyID(key.getKeyID());
        if ("dc+sd-jwt".equals(type)) {
            header.x509CertChain(issuerCertificateChain);
        }
        val jwt = new JWSObject(header.build(),
            new Payload(JSONObjectUtils.toJSONString(claims)));
        jwt.sign(new ECDSASigner(key));
        return jwt.serialize();
    }

    private String issuance() throws Exception {
        return sign(issuerClaims, issuerKey, "dc+sd-jwt") + "~"
            + (disclosures.isEmpty() ? "" : String.join("~", disclosures) + "~");
    }

    private String presentation() throws Exception {
        return bind(issuance());
    }

    private String bind(final String issuance) throws Exception {
        proofClaims.put("sd_hash", hash(issuance));
        return issuance + sign(proofClaims, holderKey, "kb+jwt");
    }

    private VerifiedCredential verify(final String raw) {
        return verifier.verify(raw, transaction, new OpenId4VpConfiguration());
    }

    @Test
    void verifiesPresentationAndTrustEvidence() throws Exception {
        val result = verify(presentation());
        assertEquals(ISSUER, result.getIssuer());
        assertEquals("urn:example:pid", result.getType());
        assertEquals("Alice", result.getClaims().get("given_name"));
        assertEquals("user", result.getClaims().get("sub"));
        assertFalse(result.getClaims().containsKey("_sd"));
        assertTrue(result.isCryptographicHolderBinding());
        assertEquals(List.of("https://trusted-list.example"), result.getTrustedAuthorities().get("etsi_tl"));
    }

    @Test
    void readsTheOtherTypesTheIssuerSigned() throws Exception {
        assertTrue(verify(presentation()).getAdditionalTypes().isEmpty());
        issuerClaims.put("aka_vcts", List.of("urn:example:pid:general"));
        assertEquals(List.of("urn:example:pid:general"), verify(presentation()).getAdditionalTypes());
    }

    @Test
    void rejectsInvalidOrDisclosedOtherTypes() throws Exception {
        // "MUST be a non-empty array of case-sensitive strings" which "MUST NOT contain the value of the vct claim"
        for (val invalid : List.<Object>of(List.of(), "urn:example:pid:general", List.of(42), List.of("urn:example:pid"))) {
            issuerClaims.put("aka_vcts", invalid);
            val raw = presentation();
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
        // and it "MUST NOT be selectively disclosed": a holder could otherwise add a type the issuer never signed
        issuerClaims.remove("aka_vcts");
        val typesDisclosure = Base64URL.encode(com.nimbusds.jose.util.JSONArrayUtils.toJSONString(
            List.of("salt-types", "aka_vcts", List.of("urn:example:pid:general")))).toString();
        disclosures.add(typesDisclosure);
        issuerClaims.put("_sd", List.of(hash(disclosures.get(0)), hash(typesDisclosure)));
        val raw = presentation();
        assertEquals("the SD-JWT VC aka_vcts claim must not be selectively disclosed",
            assertThrows(OpenId4VpException.class, () -> verify(raw)).getMessage());
    }

    @Test
    void reconstructsNestedAndArrayDisclosuresAndOmitsUndisclosedClaims() throws Exception {
        val street = disclose("salt-street", "street", "Main Street");
        val role = disclose("salt-role", "admin");
        issuerClaims.put("address", Map.of("_sd", List.of(hash(street))));
        issuerClaims.put("roles", List.of(Map.of("...", hash(role)), Map.of("...", hash("undisclosed"))));
        disclosures.clear();
        disclosures.addAll(List.of(street, role));
        val result = verify(presentation());
        assertEquals(Map.of("street", "Main Street"), result.getClaims().get("address"));
        assertEquals(List.of("admin"), result.getClaims().get("roles"));
        assertFalse(result.getClaims().containsKey("given_name"));
    }

    @Test
    void rejectsMissingIssuerBeforeTrustedKeyLookup() throws Exception {
        issuerClaims.remove("iss");
        verifier = new SdJwtVcVerifier();
        val raw = presentation();
        val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertEquals("the SD-JWT VC must contain an iss claim or an x5c certificate chain", error.getMessage());
    }

    @Test
    void rejectsBlankIssuerBeforeTrustedKeyLookup() throws Exception {
        for (val issuer : List.of("", " ")) {
            issuerClaims.put("iss", issuer);
            val raw = presentation();
            val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
            assertEquals("the SD-JWT VC iss claim must not be blank", error.getMessage());
        }
    }

    @Test
    void verifiesCertificateIssuerWithoutIssWithOrWithoutRootInHeader() throws Exception {
        val certificates = configureCertificateIssuer();
        for (val includeRoot : List.of(false, true)) {
            issuerCertificateChain = includeRoot
                ? List.of(Base64.encode(certificates.leaf().getEncoded()), Base64.encode(certificates.root().getEncoded()))
                : List.of(Base64.encode(certificates.leaf().getEncoded()));
            val result = verify(presentation());
            assertEquals("CN=Credential Issuer", result.getIssuer());
            assertEquals("Alice", result.getClaims().get("given_name"));
            assertFalse(result.getClaims().containsKey("iss"));
            assertTrue(result.isCryptographicHolderBinding());
            // a supplied root is a trust anchor: its identifier, the one of its own key, is the one the leaf names
            assertEquals(Map.of(TrustedAuthority.AKI, List.of(authorityKeyIdentifier(certificates.rootKey()))),
                result.getTrustedAuthorities());
        }
    }

    @Test
    void loadsJksAndPkcs12TrustStoresWithoutPrivateKeySettings() throws Exception {
        val certificates = configureCertificateIssuer();
        val raw = presentation();
        for (val type : List.of("JKS", "PKCS12")) {
            val properties = trustStore(type, certificates.root());
            if ("JKS".equals(type)) {
                properties.setKeyStoreType(null);
            }
            certificateIssuers.setTrustStore(properties);
            assertEquals("CN=Credential Issuer", verify(raw).getIssuer());
        }
    }

    @Test
    void restrictsTrustToConfiguredCertificateAlias() throws Exception {
        val certificates = configureCertificateIssuer();
        val raw = presentation();
        certificateIssuers.getTrustStore().setKeyStoreAlias("root");
        assertTrue(verify(raw).isCryptographicHolderBinding());
        certificateIssuers.getTrustStore().setKeyStoreAlias("missing");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        val keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("private-key", certificates.rootKey().toPrivateKey(), "secret".toCharArray(),
            new X509Certificate[] {certificates.root()});
        certificateIssuers.setTrustStore(saveTrustStore(keyStore));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        certificateIssuers.getTrustStore().setKeyStoreAlias("private-key");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void readsTheTrustStoreOnceUntilItsSettingsChange() throws Exception {
        configureCertificateIssuer();
        val raw = presentation();
        val opened = new AtomicInteger();
        val properties = certificateIssuers.getTrustStore();
        properties.setResource(new FileSystemResource(properties.getResource().getFile()) {
            @Override
            public InputStream getInputStream() throws IOException {
                opened.incrementAndGet();
                return super.getInputStream();
            }
        });
        certificateIssuers.setTrustStore(properties);

        verify(raw);
        verify(raw);
        assertEquals(1, opened.get());
        // a changed setting reads it again
        properties.setKeyStoreAlias("root");
        verify(raw);
        assertEquals(2, opened.get());
        // and so does setting it again, to pick up a file replaced on disk
        certificateIssuers.setTrustStore(properties);
        verify(raw);
        assertEquals(3, opened.get());
    }

    @Test
    void readsTheCertificateRevocationListsOnceUntilTheyAreSetAgain() throws Exception {
        val certificates = configureCertificateIssuer();
        val builder = new JcaX509v2CRLBuilder(certificates.root(), Date.from(Instant.now().minusSeconds(60)));
        builder.setNextUpdate(Date.from(Instant.now().plusSeconds(3600)));
        val crl = crlResource(new JcaX509CRLConverter().getCRL(builder.build(
            new JcaContentSignerBuilder("SHA256withECDSA").build(certificates.rootKey().toPrivateKey()))));
        val opened = new AtomicInteger();
        crl.setResource(new FileSystemResource(crl.getResource().getFile()) {
            @Override
            public InputStream getInputStream() throws IOException {
                opened.incrementAndGet();
                return super.getInputStream();
            }
        });
        certificateIssuers.setCertificateRevocationEnabled(true).setCertificateRevocationLists(List.of(crl));
        val raw = presentation();

        verify(raw);
        verify(raw);
        assertEquals(1, opened.get());
        certificateIssuers.setCertificateRevocationLists(List.of(crl));
        verify(raw);
        assertEquals(2, opened.get());

        certificateIssuers.setCertificateRevocationLists(List.of(new ResourceProperties()));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsEmptyTrustStoreAndIncorrectPassword() throws Exception {
        configureCertificateIssuer();
        val raw = presentation();
        certificateIssuers.getTrustStore().setKeystorePassword("incorrect");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        val empty = KeyStore.getInstance("PKCS12");
        empty.load(null, null);
        certificateIssuers.setTrustStore(saveTrustStore(empty));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsMalformedCertificate() throws Exception {
        configureCertificateIssuer();
        issuerCertificateChain = List.of(Base64.encode("not a certificate"));
        val raw = presentation();
        assertTrue(assertThrows(OpenId4VpException.class, () -> verify(raw)).getMessage().contains("invalid certificate"));
    }

    @Test
    void verifiesCertificateIssuerWithAnIssNotAmongTheTrustedIssuers() throws Exception {
        configureCertificateIssuer();
        issuerClaims.put("iss", ISSUER);
        val result = verify(presentation());
        // "the Issuer of the Verifiable Digital Credential is the subject of the end-entity certificate"
        assertEquals("CN=Credential Issuer", result.getIssuer());
        assertEquals(ISSUER, result.getClaims().get("iss"));
        assertTrue(result.isCryptographicHolderBinding());
    }

    @Test
    void usesConfiguredIssuerKeysWhenIssAndX5cAreBothPresent() throws Exception {
        configureCertificateIssuer();
        issuerClaims.put("iss", ISSUER);
        val raw = presentation();
        // added after the certificates, consulted before them
        verifier.getTrustedIssuers().add(new KeysTrustedIssuer(ISSUER, new JWKSet(issuerKey.toPublicJWK())));
        assertEquals(ISSUER, verify(raw).getIssuer());
    }

    @Test
    void neverFallsBackToX5cForAnIssWithConfiguredKeys() throws Exception {
        // a valid chain in the header cannot switch a configured iss to another verification process
        configureCertificateIssuer();
        issuerClaims.put("iss", ISSUER);
        val otherKey = new ECKeyGenerator(Curve.P_256).keyID("issuer-key").generate();
        verifier.getTrustedIssuers().add(new KeysTrustedIssuer(ISSUER, new JWKSet(otherKey.toPublicJWK())));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsAnIssWithoutConfiguredKeysNorX5c() throws Exception {
        issuerClaims.put("iss", "https://untrusted.example");
        val raw = presentation();
        val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertEquals("the SD-JWT VC issuer has no configured trusted keys and no x5c certificate chain", error.getMessage());
    }

    @Test
    void rejectsCertificateIssuerWithoutConfiguredTrust() throws Exception {
        configureCertificateIssuer();
        verifier = new SdJwtVcVerifier();
        val raw = presentation();
        val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertTrue(error.getMessage().contains("not trusted"));
    }

    @Test
    void rejectsUntrustedRootEvenWhenIncludedInHeader() throws Exception {
        val certificates = configureCertificateIssuer();
        val otherKey = new ECKeyGenerator(Curve.P_256).generate();
        val otherRoot = certificate("CN=Other Root", otherKey, "CN=Other Root", otherKey, true,
            KeyUsage.keyCertSign, -60, 3600);
        certificateIssuers.setTrustStore(trustStore("PKCS12", otherRoot));
        issuerCertificateChain = List.of(Base64.encode(certificates.leaf().getEncoded()), Base64.encode(certificates.root().getEncoded()));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsInvalidCertificateSignature() throws Exception {
        configureCertificateIssuer();
        val otherKey = new ECKeyGenerator(Curve.P_256).generate();
        val forged = certificate("CN=Credential Issuer", issuerKey, "CN=Root", otherKey, false,
            KeyUsage.digitalSignature, -60, 3600);
        issuerCertificateChain = List.of(Base64.encode(forged.getEncoded()));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsExpiredAndNotYetValidCertificates() throws Exception {
        val certificates = configureCertificateIssuer();
        for (val start : List.of(-3600, 3600)) {
            val invalid = certificate("CN=Credential Issuer", issuerKey, "CN=Root", certificates.rootKey(), false,
                KeyUsage.digitalSignature, start, start + 60);
            issuerCertificateChain = List.of(Base64.encode(invalid.getEncoded()));
            val raw = presentation();
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
    }

    @Test
    void rejectsCertificateWithoutDigitalSignatureUsage() throws Exception {
        val certificates = configureCertificateIssuer();
        val invalid = certificate("CN=Credential Issuer", issuerKey, "CN=Root", certificates.rootKey(), false,
            KeyUsage.keyAgreement, -60, 3600);
        issuerCertificateChain = List.of(Base64.encode(invalid.getEncoded()));
        val raw = presentation();
        assertTrue(assertThrows(OpenId4VpException.class, () -> verify(raw)).getMessage().contains("digital signatures"));
    }

    @Test
    void validatesIntermediateCertificateConstraints() throws Exception {
        val certificates = configureCertificateIssuer();
        val intermediateKey = new ECKeyGenerator(Curve.P_256).generate();
        val leaf = certificate("CN=Credential Issuer", issuerKey, "CN=Intermediate", intermediateKey, false,
            KeyUsage.digitalSignature, -60, 3600);
        for (val ca : List.of(true, false)) {
            val intermediate = certificate("CN=Intermediate", intermediateKey, "CN=Root", certificates.rootKey(), ca,
                KeyUsage.keyCertSign, -60, 3600);
            issuerCertificateChain = List.of(Base64.encode(leaf.getEncoded()), Base64.encode(intermediate.getEncoded()));
            val raw = presentation();
            if (ca) {
                assertTrue(verify(raw).isCryptographicHolderBinding());
            } else {
                assertThrows(OpenId4VpException.class, () -> verify(raw));
            }
        }
    }

    @Test
    void rejectsInvalidIssuerSignatureWithTrustedCertificate() throws Exception {
        configureCertificateIssuer();
        issuerKey = new ECKeyGenerator(Curve.P_256).generate();
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void enforcesIssuerAlgorithmAndHolderProofWithCertificateIssuer() throws Exception {
        configureCertificateIssuer();
        val raw = presentation();
        verifier.setIssuerAlgorithms(Set.of(JWSAlgorithm.RS256));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        verifier.setIssuerAlgorithms(Set.of(JWSAlgorithm.ES256));
        proofClaims.put("nonce", "wrong");
        val wrongNonce = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(wrongNonce));
    }

    @Test
    void rejectsDisclosureOfIssuerWithCertificateIssuer() throws Exception {
        configureCertificateIssuer();
        val issuerDisclosure = disclose("salt-issuer", "iss", ISSUER);
        disclosures.add(issuerDisclosure);
        issuerClaims.put("_sd", List.of(hash(disclosures.get(0)), hash(issuerDisclosure)));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void honorsCertificateRevocationPolicy() throws Exception {
        val certificates = configureCertificateIssuer();
        certificateIssuers.setCertificateRevocationEnabled(true);
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        for (val revoked : List.of(false, true)) {
            val builder = new JcaX509v2CRLBuilder(certificates.root(), Date.from(Instant.now().minusSeconds(60)));
            builder.setNextUpdate(Date.from(Instant.now().plusSeconds(3600)));
            if (revoked) {
                builder.addCRLEntry(certificates.leaf().getSerialNumber(), Date.from(Instant.now().minusSeconds(30)), 1);
            }
            val crl = new JcaX509CRLConverter().getCRL(builder.build(
                new JcaContentSignerBuilder("SHA256withECDSA").build(certificates.rootKey().toPrivateKey())));
            certificateIssuers.setCertificateRevocationLists(List.of(crlResource(crl)));
            if (revoked) {
                assertThrows(OpenId4VpException.class, () -> verify(raw));
            } else {
                assertTrue(verify(raw).isCryptographicHolderBinding());
            }
        }
    }

    private TestCertificates configureCertificateIssuer() throws Exception {
        issuerClaims.remove("iss");
        verifier = new SdJwtVcVerifier();
        val rootKey = new ECKeyGenerator(Curve.P_256).generate();
        val root = certificate("CN=Root", rootKey, "CN=Root", rootKey, true, KeyUsage.keyCertSign | KeyUsage.cRLSign, -60, 3600);
        val leaf = certificate("CN=Credential Issuer", issuerKey, "CN=Root", rootKey, false, KeyUsage.digitalSignature, -60, 3600);
        // These generated fixtures have no revocation information; the revocation test supplies a local CRL explicitly.
        certificateIssuers = new CertificateTrustedIssuer(trustStore("PKCS12", root)).setCertificateRevocationEnabled(false);
        verifier.getTrustedIssuers().add(certificateIssuers);
        issuerCertificateChain = List.of(Base64.encode(leaf.getEncoded()));
        return new TestCertificates(rootKey, root, leaf);
    }

    private ResourceProperties crlResource(final X509CRL crl) throws Exception {
        val path = Files.createTempFile(directory, "crl-", ".crl");
        Files.write(path, crl.getEncoded());
        return new ResourceProperties().setPath(path.toString());
    }

    private KeystoreProperties trustStore(final String type, final X509Certificate root) throws Exception {
        val keyStore = KeyStore.getInstance(type);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("root", root);
        return saveTrustStore(keyStore);
    }

    private KeystoreProperties saveTrustStore(final KeyStore keyStore) throws Exception {
        val path = Files.createTempFile(directory, "truststore-", ".store");
        try (val output = Files.newOutputStream(path)) {
            keyStore.store(output, "changeit".toCharArray());
        }
        return new KeystoreProperties(path.toString()).setKeyStoreType(keyStore.getType())
            .setKeystorePassword("changeit");
    }

    private X509Certificate certificate(final String subject, final ECKey subjectKey, final String issuer, final ECKey signingKey,
                                        final boolean ca, final int keyUsage, final long notBefore, final long notAfter) throws Exception {
        val now = Instant.now();
        val builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), BigInteger.valueOf(ca ? 1 : 2),
            Date.from(now.plusSeconds(notBefore)), Date.from(now.plusSeconds(notAfter)), new X500Name(subject), subjectKey.toPublicKey());
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(ca));
        builder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsage));
        builder.addExtension(Extension.authorityKeyIdentifier, false,
            new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(signingKey.toPublicKey()));
        return new JcaX509CertificateConverter().getCertificate(builder.build(
            new JcaContentSignerBuilder("SHA256withECDSA").build(signingKey.toPrivateKey())));
    }

    private String authorityKeyIdentifier(final ECKey authorityKey) throws Exception {
        return Base64URL.encode(new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(authorityKey.toPublicKey())
            .getKeyIdentifierOctets()).toString();
    }

    @Test
    void reportsTheAuthorityKeyIdentifiersOfTheValidatedChain() throws Exception {
        val certificates = configureCertificateIssuer();
        val result = verify(presentation());
        // the leaf names the root key: this is what a DCQL query asks for to accept the issuers of a given CA
        val rootIdentifier = authorityKeyIdentifier(certificates.rootKey());
        assertEquals(Map.of(TrustedAuthority.AKI, List.of(rootIdentifier)), result.getTrustedAuthorities());

        val query = new CredentialQuery("pid", CredentialFormat.SD_JWT_VC).setVctValues("urn:example:pid")
            .addTrustedAuthority(new TrustedAuthority(TrustedAuthority.AKI, rootIdentifier));
        assertDoesNotThrow(() -> new DcqlValidator().validateCredential(query, result));
        val otherQuery = new CredentialQuery("pid", CredentialFormat.SD_JWT_VC).setVctValues("urn:example:pid")
            .addTrustedAuthority(new TrustedAuthority(TrustedAuthority.AKI, authorityKeyIdentifier(issuerKey)));
        assertThrows(OpenId4VpException.class, () -> new DcqlValidator().validateCredential(otherQuery, result));
    }

    @Test
    void reportsTheAuthorityKeyIdentifiersOfAnIntermediateChain() throws Exception {
        val certificates = configureCertificateIssuer();
        val intermediateKey = new ECKeyGenerator(Curve.P_256).generate();
        val intermediate = certificate("CN=Intermediate", intermediateKey, "CN=Root", certificates.rootKey(), true,
            KeyUsage.keyCertSign, -60, 3600);
        val leaf = certificate("CN=Credential Issuer", issuerKey, "CN=Intermediate", intermediateKey, false,
            KeyUsage.digitalSignature, -60, 3600);
        issuerCertificateChain = List.of(Base64.encode(leaf.getEncoded()), Base64.encode(intermediate.getEncoded()));
        val result = verify(presentation());
        assertEquals(List.of(authorityKeyIdentifier(intermediateKey), authorityKeyIdentifier(certificates.rootKey())),
            result.getTrustedAuthorities().get(TrustedAuthority.AKI));
    }

    @Test
    void consultsTheNextTruststoreForAChainTheFirstDoesNotKnow() throws Exception {
        configureCertificateIssuer();
        val otherKey = new ECKeyGenerator(Curve.P_256).generate();
        val otherRoot = certificate("CN=Other Root", otherKey, "CN=Other Root", otherKey, true,
            KeyUsage.keyCertSign, -60, 3600);
        verifier = new SdJwtVcVerifier().setTrustedIssuers(new TrustedIssuers(
            new CertificateTrustedIssuer(trustStore("PKCS12", otherRoot)).setCertificateRevocationEnabled(false),
            certificateIssuers));
        assertEquals("CN=Credential Issuer", verify(presentation()).getIssuer());
    }

    @Test
    void neverConsultsTheNextTruststoreOnceAChainIsRejected() throws Exception {
        val certificates = configureCertificateIssuer();
        val builder = new JcaX509v2CRLBuilder(certificates.root(), Date.from(Instant.now().minusSeconds(60)));
        builder.setNextUpdate(Date.from(Instant.now().plusSeconds(3600)));
        builder.addCRLEntry(certificates.leaf().getSerialNumber(), Date.from(Instant.now().minusSeconds(30)), 1);
        val crl = new JcaX509CRLConverter().getCRL(builder.build(
            new JcaContentSignerBuilder("SHA256withECDSA").build(certificates.rootKey().toPrivateKey())));
        // the same root, revoking the leaf: the first truststore knows the chain and rejects it
        verifier = new SdJwtVcVerifier().setTrustedIssuers(new TrustedIssuers(
            new CertificateTrustedIssuer(trustStore("PKCS12", certificates.root()))
                .setCertificateRevocationLists(List.of(crlResource(crl))),
            certificateIssuers));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void addsTrustedIssuersOneByOne() throws Exception {
        val shared = new TrustedIssuers();
        verifier = new SdJwtVcVerifier().setTrustedIssuers(shared)
            .addTrustedIssuer(new KeysTrustedIssuer(ISSUER, new JWKSet(issuerKey.toPublicJWK())));
        // added to the shared trusted issuers, and the setters of the format still chain
        assertEquals(1, shared.getIssuers().size());
        assertEquals(10, new SdJwtVcVerifier().addTrustedIssuer(shared.getIssuers().get(0)).setClockSkewSeconds(10)
            .getClockSkewSeconds());
        assertEquals(ISSUER, verify(presentation()).getIssuer());
    }

    @Test
    void rejectsAKidMatchingNoConfiguredKey() throws Exception {
        verifier = new SdJwtVcVerifier().setTrustedIssuers(new TrustedIssuers(new KeysTrustedIssuer(ISSUER,
            new JWKSet(new ECKey.Builder(issuerKey.toPublicJWK()).keyID("other-key").build()))));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void loadsTheTrustedIssuerKeysFromAJwksResource() throws Exception {
        val otherKey = new ECKeyGenerator(Curve.P_256).keyID("other-key").generate();
        val path = Files.createTempFile(directory, "issuer-", ".jwks");
        // a JWKS holding private parts: only the public ones are kept
        Files.writeString(path, new JWKSet(List.of(otherKey, issuerKey)).toString(false));
        val raw = presentation();

        verifier = new SdJwtVcVerifier()
            .setTrustedIssuers(new TrustedIssuers(new KeysTrustedIssuer(ISSUER, new JwksProperties(path.toString()))));
        assertEquals(ISSUER, verify(raw).getIssuer());
        assertTrue(((KeysTrustedIssuer) verifier.getTrustedIssuers().getIssuers().get(0)).getKeys().getKeys().stream()
            .noneMatch(JWK::isPrivate));

        // a kid keeps only that key
        verifier = new SdJwtVcVerifier().setTrustedIssuers(new TrustedIssuers(
            new KeysTrustedIssuer(ISSUER, new JwksProperties(path.toString()).setKid("other-key"))));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(ISSUER,
            new JwksProperties(path.toString()).setKid("missing")));
    }

    @Test
    void rejectsAMissingOrUndefinedJwksResource() {
        val missing = directory.resolve("missing.jwks").toString();
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(ISSUER, new JwksProperties(missing)));
        assertFalse(Files.exists(directory.resolve("missing.jwks")));
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(ISSUER, new JwksProperties()));
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(ISSUER, (JwksProperties) null));
    }

    @Test
    void validatesTheTrustedIssuersConfiguration() {
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(" ", new JWKSet(issuerKey.toPublicJWK())));
        assertThrows(TechnicalException.class, () -> new KeysTrustedIssuer(ISSUER, new JWKSet()));
        assertThrows(TechnicalException.class, () -> new CertificateTrustedIssuer(null));
        assertThrows(TechnicalException.class, () -> new TrustedIssuers().add(null));
        assertThrows(TechnicalException.class, () -> new TrustedIssuers((TrustedIssuer) null));
        assertThrows(TechnicalException.class, () -> new SdJwtVcVerifier().addTrustedIssuer(null));
        assertThrows(TechnicalException.class, () -> new SdJwtVcVerifier().setTrustedIssuers(null).addTrustedIssuer(
            new KeysTrustedIssuer(ISSUER, new JWKSet(issuerKey.toPublicJWK()))));
        // only the public part of the configured keys is kept
        assertFalse(new KeysTrustedIssuer(ISSUER, new JWKSet(issuerKey)).getKeys().getKeys().get(0).isPrivate());
    }

    private record TestCertificates(ECKey rootKey, X509Certificate root, X509Certificate leaf) { }

    @Test
    void rejectsUnknownIssuer() throws Exception {
        issuerClaims.put("iss", "https://untrusted.example");
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsUnconfiguredTrust() throws Exception {
        verifier = new SdJwtVcVerifier();
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsForgedIssuerSignature() throws Exception {
        issuerKey = new ECKeyGenerator(Curve.P_256).keyID("issuer-key").generate();
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsForgedHolderSignature() throws Exception {
        holderKey = new ECKeyGenerator(Curve.P_256).generate();
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsModifiedDisclosureAndPreservesCause() throws Exception {
        disclosures.set(0, disclose("salt-name", "given_name", "SECRET-ATTACKER-VALUE"));
        val raw = presentation();
        val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertFalse(error.toString().contains("SECRET"));
        assertNotNull(error.getCause());
        assertTrue(error.getCause().getStackTrace().length > 0);
    }

    @Test
    void rejectsDuplicateDisclosure() throws Exception {
        disclosures.add(disclosures.get(0));
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsWrongPresentationHash() throws Exception {
        val issuance = issuance();
        proofClaims.put("sd_hash", hash("another-presentation"));
        val raw = issuance + sign(proofClaims, holderKey, "kb+jwt");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsWrongNonce() throws Exception {
        proofClaims.put("nonce", "other");
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsWrongAudienceAndAudienceArray() throws Exception {
        for (val audience : List.of("other", List.of(AUDIENCE))) {
            proofClaims.put("aud", audience);
            val raw = presentation();
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
    }

    @Test
    void bindsDcApiToSavedOrigin() throws Exception {
        transaction.setResponseMode(ResponseMode.DC_API_JWT).setRequestParameters(JSONObjectUtils.toJSONString(
            Map.of("client_id", AUDIENCE, "nonce", "nonce", "expected_origins", List.of("https://verifier.example"))));
        val wrong = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(wrong));
        proofClaims.put("aud", "origin:https://verifier.example");
        assertTrue(verify(presentation()).isCryptographicHolderBinding());
        proofClaims.put("aud", "origin:https://attacker.example");
        val attacker = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(attacker));
    }

    @Test
    void rejectsStaleAndFutureProofs() throws Exception {
        for (val delta : List.of(-120, 120)) {
            proofClaims.put("iat", Instant.now().plusSeconds(delta).getEpochSecond());
            val raw = presentation();
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
    }

    @Test
    void rejectsExpiredCredentialAndFutureNotBefore() throws Exception {
        issuerClaims.put("exp", Instant.now().minusSeconds(120).getEpochSecond());
        val expired = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(expired));
        issuerClaims.put("exp", Instant.now().plusSeconds(600).getEpochSecond());
        issuerClaims.put("nbf", Instant.now().plusSeconds(120).getEpochSecond());
        val future = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(future));
    }

    @Test
    void rejectsWrongCredentialTypeHeaderAndMissingVct() throws Exception {
        val wrongType = bind(sign(issuerClaims, issuerKey, "JWT") + "~" + disclosures.get(0) + "~");
        assertThrows(OpenId4VpException.class, () -> verify(wrongType));
        issuerClaims.remove("vct");
        val missingVct = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(missingVct));
    }

    @Test
    void rejectsDisallowedAlgorithms() throws Exception {
        val raw = presentation();
        verifier.setIssuerAlgorithms(Set.of(JWSAlgorithm.RS256));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        verifier.setIssuerAlgorithms(Set.of(JWSAlgorithm.ES256)).setHolderAlgorithms(Set.of(JWSAlgorithm.RS256));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        verifier.setIssuerAlgorithms(Set.of(JWSAlgorithm.HS256));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void reportsMissingHolderBindingForDcqlToEnforce() throws Exception {
        val result = verify(issuance());
        assertFalse(result.isCryptographicHolderBinding());
        val query = new CredentialQuery("pid", CredentialFormat.SD_JWT_VC).setVctValues("urn:example:pid");
        assertThrows(OpenId4VpException.class, () -> new DcqlValidator().validateCredential(query, result));
        query.setRequireCryptographicHolderBinding(false);
        assertDoesNotThrow(() -> new DcqlValidator().validateCredential(query, result));
    }

    @Test
    void rejectsStatusWithoutCheckerAndAllowsExplicitChecker() throws Exception {
        issuerClaims.put("status", Map.of("status_list", Map.of("idx", 1, "uri", "https://issuer.example/status")));
        val raw = presentation();
        assertTrue(assertThrows(OpenId4VpException.class, () -> verify(raw)).getMessage().contains("statusChecker"));
        val checked = new AtomicBoolean();
        verifier.setStatusChecker(credential -> {
            assertEquals(ISSUER, credential.getIssuer());
            assertTrue(credential.isCryptographicHolderBinding());
            checked.set(true);
        });
        verify(raw);
        assertTrue(checked.get());
        verifier.setStatusChecker(credential -> { throw new IllegalArgumentException("revoked"); });
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void neverCallsStatusCheckerBeforeCryptographicVerification() throws Exception {
        issuerClaims.put("status", Map.of("status_list", Map.of("idx", 1, "uri", "https://issuer.example/status")));
        val checked = new AtomicBoolean();
        verifier.setStatusChecker(credential -> checked.set(true));
        proofClaims.put("nonce", "attacker");
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertFalse(checked.get());
    }

    @Test
    void rejectsExpiredTransaction() throws Exception {
        val raw = presentation();
        transaction.setExpiresAt(Instant.now().minusSeconds(1));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }
}
