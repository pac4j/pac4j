package org.pac4j.openid4vp.verifier;

import lombok.val;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import COSE.AlgorithmID;
import COSE.OneKey;
import id.walt.mdoc.COSECryptoProviderKeyInfo;
import id.walt.mdoc.SimpleCOSECryptoProvider;
import id.walt.mdoc.dataelement.*;
import id.walt.mdoc.dataretrieval.DeviceResponse;
import id.walt.mdoc.doc.*;
import id.walt.mdoc.docrequest.MDocRequestBuilder;
import id.walt.mdoc.mdocauth.DeviceAuthentication;
import id.walt.mdoc.mso.DeviceKeyInfo;
import id.walt.mdoc.mso.ValidityInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import id.walt.mdoc.cose.COSESign1;
import id.walt.mdoc.issuersigned.IssuerSigned;
import id.walt.mdoc.devicesigned.DeviceSigned;
import id.walt.mdoc.devicesigned.DeviceAuth;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Synthetic cryptographic fixtures: compatibility test, not OpenID4VP conformance certification. */
@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
    justification = "Generated fixtures always include an MSO, issuer authentication and a device signature")
class MdocVerifierTests {
    private static final String DOCTYPE = "org.iso.18013.5.1.mDL";
    private static final String NS = "org.iso.18013.5.1";
    private KeyPair issuer, holder, rootKeys;
    private X509Certificate root, leaf;
    private SimpleCOSECryptoProvider signer;
    private MdocVerifier verifier;
    private VpTransaction transaction;
    @TempDir
    Path directory;
    private DeviceKeyInfo deviceKey;

    @BeforeEach
    void setUp() throws Exception {
        rootKeys = keyPair();
        issuer = keyPair();
        holder = keyPair();
        root = certificate(rootKeys.getPublic(), rootKeys.getPrivate(), "CN=Compatibility Root", "CN=Compatibility Root", true);
        leaf = certificate(issuer.getPublic(), rootKeys.getPrivate(), "CN=Compatibility Issuer", "CN=Compatibility Root", false);
        signer = new SimpleCOSECryptoProvider(List.of(
            new COSECryptoProviderKeyInfo("issuer", AlgorithmID.ECDSA_256, issuer.getPublic(), issuer.getPrivate(),
                List.of(leaf), List.of(root)),
            new COSECryptoProviderKeyInfo("device", AlgorithmID.ECDSA_256, holder.getPublic(), holder.getPrivate(), List.of(), List.of())));
        verifier = new MdocVerifier().setTrustStore(trustStore(root)).setCertificateRevocationEnabled(false);
        transaction = new VpTransaction().setId("mdoc-test").setNonce("nonce").setResponseMode(ResponseMode.DIRECT_POST)
            .setCreatedAt(Instant.now().minusSeconds(5)).setExpiresAt(Instant.now().plusSeconds(300));
        saveRequest("x509_san_dns:verifier.example", "nonce", "https://verifier.example/response");
        MapElement publicKey = DataElement.Companion.fromCBOR(new OneKey(holder.getPublic(), null).AsCBOR().EncodeToBytes());
        deviceKey = new DeviceKeyInfo(publicKey, null, null);
    }

    private KeystoreProperties trustStore(X509Certificate certificate) throws Exception {
        val store = KeyStore.getInstance("PKCS12");
        store.load(null, null);
        store.setCertificateEntry("root", certificate);
        val path = Files.createTempFile(directory, "mdoc-trust-", ".p12");
        try (val output = Files.newOutputStream(path)) {
            store.store(output, "changeit".toCharArray());
        }
        return new KeystoreProperties().setKeystorePath(path.toString()).setKeyStoreType("PKCS12").setKeystorePassword("changeit");
    }

    private void saveRequest(String clientId, String nonce, String responseUri) {
        transaction.setRequestParameters(JSONObjectUtils.toJSONString(Map.of(
            "client_id", clientId, "nonce", nonce, "response_uri", responseUri)));
    }

    private static KeyPair keyPair() throws Exception {
        val generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        return generator.generateKeyPair();
    }

    private X509Certificate certificate(PublicKey key, PrivateKey signingKey, String subject, String issuer, boolean ca)
        throws Exception {
        val now = Instant.now();
        val builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), new BigInteger(120, new SecureRandom()),
            Date.from(now.minusSeconds(600)), Date.from(now.plusSeconds(3600)), new X500Name(subject), key);
        builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(ca));
        builder.addExtension(Extension.authorityKeyIdentifier, false,
            new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(ca ? key : rootKeys.getPublic()));
        builder.addExtension(Extension.keyUsage, true,
            new KeyUsage(ca ? KeyUsage.keyCertSign | KeyUsage.cRLSign : KeyUsage.digitalSignature));
        return new JcaX509CertificateConverter().getCertificate(
            builder.build(new JcaContentSignerBuilder("SHA256withECDSA").build(signingKey)));
    }

    private DeviceAuthentication authentication(String nonce) throws Exception {
        val request = JSONObjectUtils.parse(transaction.getRequestParameters());
        DataElement thumbprint = transaction.getResponseMode().isEncrypted()
            ? new ByteStringElement(com.nimbusds.jose.jwk.JWK.parse(transaction.getEncryptionKey()).computeThumbprint().decode())
            : new NullElement(null);
        val info = transaction.getResponseMode().isOverDcApi()
            ? new ListElement(List.of(new StringElement("https://verifier.example"), new StringElement(nonce), thumbprint))
            : new ListElement(List.of(new StringElement((String) request.get("client_id")), new StringElement(nonce),
                thumbprint, new StringElement((String) request.get("response_uri"))));
        val label = transaction.getResponseMode().isOverDcApi() ? "OpenID4VPDCAPIHandover" : "OpenID4VPHandover";
        val handover = new ListElement(List.of(new StringElement(label),
            new ByteStringElement(MessageDigest.getInstance("SHA-256").digest(info.toCBOR()))));
        return new DeviceAuthentication(new ListElement(List.of(new NullElement(null), new NullElement(null), handover)),
            DOCTYPE, new EncodedCBORElement(new MapElement(Map.of())));
    }

    private MDoc presentation(boolean expired) throws Exception {
        val now = Instant.now();
        val validity = new ValidityInfo(kotlinx.datetime.Instant.Companion.fromEpochSeconds(now.minusSeconds(600).getEpochSecond(), 0),
            kotlinx.datetime.Instant.Companion.fromEpochSeconds(now.minusSeconds(300).getEpochSecond(), 0),
            kotlinx.datetime.Instant.Companion.fromEpochSeconds(now.plusSeconds(expired ? -60 : 600).getEpochSecond(), 0), null);
        val document = new MDocBuilder(DOCTYPE).addItemToSign(NS, "given_name", new StringElement("Alice"))
            .addItemToSign(NS, "family_name", new StringElement("Doe"))
            .sign(validity, deviceKey, signer, "issuer");
        val request = new MDocRequestBuilder(DOCTYPE).addDataElementRequest(NS, "given_name", false)
            .addDataElementRequest(NS, "family_name", false).build(null);
        val presented = document.presentWithDeviceSignature(request, authentication("nonce"), signer, "device");
        val response = new DeviceResponse(List.of(presented), new StringElement("1.0"), new NumberElement(0), null);
        val parsed = DeviceResponse.Companion.fromCBORBase64URL(response.toCBORBase64URL());
        assertEquals(1, parsed.getDocuments().size());
        return parsed.getDocuments().get(0);
    }

    private String encode(MDoc document) {
        return new DeviceResponse(List.of(document), new StringElement("1.0"), new NumberElement(0), null).toCBORBase64URL();
    }

    private VerifiedCredential verify(String raw) {
        return verifier.verify(raw, transaction, new OpenId4VpConfiguration());
    }

    private void rejected(MDoc document) {
        assertThrows(OpenId4VpException.class, () -> verify(encode(document)));
    }

    @Test
    void verifiesIssuerHolderClaimsAndCoexistsWithEudi() throws Exception {
        val sdJwt = new SdJwtVcVerifierTests();
        sdJwt.setUp();
        sdJwt.verifiesPresentationAndTrustEvidence();
        val result = verify(encode(presentation(false)));
        assertEquals(CredentialFormat.MSO_MDOC, result.getFormat());
        assertEquals(DOCTYPE, result.getType());
        assertEquals("CN=Compatibility Issuer", result.getIssuer());
        assertEquals(Map.of(NS, Map.of("given_name", "Alice", "family_name", "Doe")), result.getClaims());
        assertTrue(result.isCryptographicHolderBinding());
        assertFalse(result.getTrustedAuthorities().get("aki").isEmpty());
        sdJwt.setUp();
        sdJwt.verifiesPresentationAndTrustEvidence();
    }

    @Test
    void verifiesThePublishedOpenId4VpHandoverVectors() throws Exception {
        // OpenID4VP 1.0 B.2.6: independent reference bytes for redirects and the DC API.
        val nonce = new String(HexFormat.of().parseHex(
            "6578633767426b786a7831726463397564527276654b7653734a4971383061766c58654c48684777717441"),
            java.nio.charset.StandardCharsets.UTF_8);
        transaction.setEncryptionKey("{\"kty\":\"EC\",\"crv\":\"P-256\","
            + "\"x\":\"DxiH5Q4Yx3UrukE2lWCErq8N8bqC9CHLLrAwLz5BmE0\","
            + "\"y\":\"XtLM4-3h5o3HUH0MHVJV0kyq0iBlrBwlh8qEDMZ4-Pc\"}");
        val vectors = Map.of(
            ResponseMode.DIRECT_POST_JWT,
            "83f6f682714f70656e494434565048616e646f7665725820048bc053c00442af9b8eed494cefdd9d95240d254b046b11b68013722aad38ac",
            ResponseMode.DC_API_JWT,
            "83f6f682764f70656e4944345650444341504948616e646f7665725820fbece366f4212f9762c74cfdbf83b8c69e371d5d68cea09cb4c48ca6daab761a");
        for (val vector : vectors.entrySet()) {
            transaction.setResponseMode(vector.getKey());
            saveRequest("x509_san_dns:example.com", nonce, "https://example.com/response");
            if (vector.getKey().isOverDcApi()) {
                transaction.setRequestParameters(JSONObjectUtils.toJSONString(Map.of("nonce", nonce,
                    "expected_origins", List.of("https://example.com"))));
            }
            val doc = presentation(false);
            final ListElement transcript = DataElement.Companion.fromCBOR(HexFormat.of().parseHex(vector.getValue()));
            val authentication = new DeviceAuthentication(transcript, DOCTYPE, new EncodedCBORElement(new MapElement(Map.of())));
            val request = new MDocRequestBuilder(DOCTYPE).addDataElementRequest(NS, "given_name", false).build(null);
            val presented = doc.presentWithDeviceSignature(request, authentication, signer, "device");
            assertTrue(verify(encode(presented)).isCryptographicHolderBinding());
        }
    }

    @Test
    void bindsEncryptedResponsesToTheEncryptionKey() throws Exception {
        transaction.setResponseMode(ResponseMode.DIRECT_POST_JWT)
            .setEncryptionKey(new ECKeyGenerator(Curve.P_256).generate().toJSONString());
        val raw = encode(presentation(false));
        assertTrue(verify(raw).isCryptographicHolderBinding());
        transaction.setEncryptionKey(new ECKeyGenerator(Curve.P_256).generate().toJSONString());
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void verifiesBothDcApiModesAndBindsOrigin() throws Exception {
        for (val mode : List.of(ResponseMode.DC_API, ResponseMode.DC_API_JWT)) {
            transaction.setResponseMode(mode).setEncryptionKey(new ECKeyGenerator(Curve.P_256).generate().toJSONString())
                .setRequestParameters(JSONObjectUtils.toJSONString(Map.of("nonce", "nonce",
                    "expected_origins", List.of("https://other.example", "https://verifier.example"))));
            val raw = encode(presentation(false));
            assertTrue(verify(raw).isCryptographicHolderBinding());
            transaction.setRequestParameters(JSONObjectUtils.toJSONString(Map.of("nonce", "nonce",
                "expected_origins", List.of("https://other.example"))));
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
    }

    @Test
    void rejectsChangedNonceClientAndResponseUri() throws Exception {
        val raw = encode(presentation(false));
        saveRequest("x509_san_dns:verifier.example", "other", "https://verifier.example/response");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        saveRequest("x509_san_dns:other.example", "nonce", "https://verifier.example/response");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        saveRequest("x509_san_dns:verifier.example", "nonce", "https://other.example/response");
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsExpiredDocumentAndTransaction() throws Exception {
        rejected(presentation(true));
        val raw = encode(presentation(false));
        transaction.setExpiresAt(Instant.now().minusSeconds(1));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsMissingAndUntrustedAnchors() throws Exception {
        val raw = encode(presentation(false));
        verifier.setTrustStore(null);
        assertThrows(OpenId4VpException.class, () -> verify(raw));
        val other = keyPair();
        verifier.setTrustStore(trustStore(certificate(other.getPublic(), other.getPrivate(), "CN=Other", "CN=Other", true)));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void verifiesWithLocalCrlAndRejectsRevokedIssuer() throws Exception {
        val now = Instant.now();
        val crl = new JcaX509v2CRLBuilder(root.getSubjectX500Principal(), Date.from(now.minusSeconds(60)));
        crl.setNextUpdate(Date.from(now.plusSeconds(600)));
        val raw = encode(presentation(false));
        verifier.setCertificateRevocationEnabled(true).setCertificateRevocationLists(List.of(
            new JcaX509CRLConverter().getCRL(crl.build(new JcaContentSignerBuilder("SHA256withECDSA").build(rootKeys.getPrivate())))));
        assertDoesNotThrow(() -> verify(raw));
        crl.addCRLEntry(leaf.getSerialNumber(), Date.from(now.minusSeconds(30)), 1);
        verifier.setCertificateRevocationLists(List.of(new JcaX509CRLConverter().getCRL(
            crl.build(new JcaContentSignerBuilder("SHA256withECDSA").build(rootKeys.getPrivate())))));
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void verifiesEveryDisclosedItemIncludingTheSecond() throws Exception {
        val original = presentation(false);
        val items = new ArrayList<>(original.getIssuerSigned().getNameSpaces().get(NS));
        MapElement decoded = DataElement.Companion.fromCBOR(items.get(1).getValue());
        val changed = new LinkedHashMap<>(decoded.getValue());
        changed.put(new MapKey("elementValue"), new StringElement("Mallory"));
        items.set(1, new EncodedCBORElement(new MapElement(changed)));
        rejected(new MDoc(original.getDocType(), new IssuerSigned(Map.of(NS, items), original.getIssuerSigned().getIssuerAuth()),
            original.getDeviceSigned(), null));
    }

    @Test
    void rejectsDuplicateDisclosedItems() throws Exception {
        val original = presentation(false);
        val items = new ArrayList<>(original.getIssuerSigned().getNameSpaces().get(NS));
        items.add(items.get(0));
        rejected(new MDoc(original.getDocType(), new IssuerSigned(Map.of(NS, items), original.getIssuerSigned().getIssuerAuth()),
            original.getDeviceSigned(), null));
    }

    @Test
    void rejectsTamperedIssuerAndDeviceSignatures() throws Exception {
        val original = presentation(false);
        val issuerData = new ArrayList<>(original.getIssuerSigned().getIssuerAuth().getData());
        var signature = ((ByteStringElement) issuerData.get(3)).getValue().clone();
        signature[0] ^= 1;
        issuerData.set(3, new ByteStringElement(signature));
        rejected(new MDoc(original.getDocType(), new IssuerSigned(original.getIssuerSigned().getNameSpaces(), new COSESign1(issuerData)),
            original.getDeviceSigned(), null));
        val deviceData = new ArrayList<>(original.getDeviceSigned().getDeviceAuth().getDeviceSignature().getData());
        signature = ((ByteStringElement) deviceData.get(3)).getValue().clone();
        signature[0] ^= 1;
        deviceData.set(3, new ByteStringElement(signature));
        rejected(new MDoc(original.getDocType(), original.getIssuerSigned(),
            new DeviceSigned(original.getDeviceSigned().getNameSpaces(), new DeviceAuth(null, new COSESign1(deviceData))), null));
    }

    @Test
    void rejectsWrongHolderKeyFromTheSignedMso() throws Exception {
        val other = keyPair();
        MapElement publicKey = DataElement.Companion.fromCBOR(new OneKey(other.getPublic(), null).AsCBOR().EncodeToBytes());
        deviceKey = new DeviceKeyInfo(publicKey, null, null);
        rejected(presentation(false));
    }

    @Test
    void rejectsMalformedFailedAndMultipleDocumentResponses() throws Exception {
        for (val raw : List.of("", "not base64!", "AA", "oA")) {
            assertThrows(OpenId4VpException.class, () -> verify(raw));
        }
        val doc = presentation(false);
        for (val response : List.of(
            new DeviceResponse(List.of(doc), new StringElement("1.0"), new NumberElement(10), null),
            new DeviceResponse(List.of(doc, doc), new StringElement("1.0"), new NumberElement(0), null),
            new DeviceResponse(List.of(), new StringElement("1.0"), new NumberElement(0), null))) {
            assertThrows(OpenId4VpException.class, () -> verify(response.toCBORBase64URL()));
        }
        val raw = encode(doc);
        verifier.setMaxPresentationSize(32);
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsDuplicateCborKeys() throws Exception {
        val encoded = new DeviceResponse(List.of(presentation(false)), new StringElement("1.0"), new NumberElement(0), null).toCBOR();
        // Four entries, with a duplicate status pair at the end of an otherwise valid three-entry map.
        assertEquals((byte) 0xa3, encoded[0]);
        val duplicate = Arrays.copyOf(encoded, encoded.length + 8);
        duplicate[0] = (byte) 0xa4;
        System.arraycopy(new byte[] {0x66, 's', 't', 'a', 't', 'u', 's', 0}, 0, duplicate, encoded.length, 8);
        assertThrows(OpenId4VpException.class, () -> verify(Base64.getUrlEncoder().withoutPadding().encodeToString(duplicate)));
    }

    private MDoc withMsoField(MDoc original, String field, DataElement value) {
        val map = new LinkedHashMap<>(original.getMSO().toMapElement().getValue());
        map.put(new MapKey(field), value);
        val auth = signer.sign1(new EncodedCBORElement(new MapElement(map)).toCBOR(), null, null, "issuer");
        return new MDoc(original.getDocType(), new IssuerSigned(original.getIssuerSigned().getNameSpaces(), auth),
            original.getDeviceSigned(), null);
    }

    @Test
    void statusMustBeCheckedAndCheckerSeesAuthenticatedStatus() throws Exception {
        val doc = withMsoField(presentation(false), "status", new MapElement(Map.of(new MapKey("test"), new NumberElement(1))));
        rejected(doc);
        val checked = new AtomicBoolean();
        verifier.setStatusChecker((credential, status) -> {
            assertTrue(credential.isCryptographicHolderBinding());
            assertEquals(1L, ((Number) status.get("test")).longValue());
            checked.set(true);
        });
        verify(encode(doc));
        assertTrue(checked.get());
        verifier.setStatusChecker((credential, status) -> { throw new OpenId4VpException("revoked"); });
        rejected(doc);
    }

    @Test
    void rejectsMissingDeviceProofAndDoctypeMismatch() throws Exception {
        val doc = presentation(false);
        rejected(new MDoc(doc.getDocType(), doc.getIssuerSigned(), null, null));
        rejected(new MDoc(new StringElement("other"), doc.getIssuerSigned(), doc.getDeviceSigned(), null));
    }

    @Test
    void refusesUnsupportedDeviceSignedClaimsAndTransactionData() throws Exception {
        val doc = presentation(false);
        rejected(new MDoc(doc.getDocType(), doc.getIssuerSigned(), new DeviceSigned(
            new EncodedCBORElement(new MapElement(Map.of(new MapKey("namespace"), new MapElement(Map.of())))),
            doc.getDeviceSigned().getDeviceAuth()), null));
        val request = JSONObjectUtils.parse(transaction.getRequestParameters());
        request.put("transaction_data", List.of("data"));
        transaction.setRequestParameters(JSONObjectUtils.toJSONString(request));
        rejected(doc);
    }

    @Test
    void dependencyIsOptionalAndMissingDependencyIsActionable() throws Exception {
        val urls = Arrays.stream(System.getProperty("java.class.path").split(java.io.File.pathSeparator))
            .filter(path -> !path.contains("waltid-mdoc") && !path.contains("cose-java"))
            .map(path -> {
                try {
                    return Path.of(path).toUri().toURL();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }).toArray(URL[]::new);
        try (val loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
            val type = loader.loadClass(MdocVerifier.class.getName());
            val instance = type.getConstructor().newInstance();
            assertEquals("MSO_MDOC", type.getMethod("getFormat").invoke(instance).toString());
            val method = type.getMethod("verify", String.class, loader.loadClass(VpTransaction.class.getName()),
                loader.loadClass(OpenId4VpConfiguration.class.getName()));
            val error = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> method.invoke(instance, "AA", null, null));
            assertTrue(error.getCause().getMessage().contains("https://maven.waltid.dev/releases"));
            assertDoesNotThrow(() -> loader.loadClass(OpenId4VpConfiguration.class.getName()).getConstructor().newInstance());
        }
    }
}
