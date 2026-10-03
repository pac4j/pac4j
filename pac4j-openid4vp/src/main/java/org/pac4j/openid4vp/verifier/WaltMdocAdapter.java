package org.pac4j.openid4vp.verifier;

import cbor.Cbor;
import cbor.CborKt;
import kotlin.Unit;
import id.walt.mdoc.mso.MSO;
import COSE.AlgorithmID;
import COSE.OneKey;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.upokecenter.cbor.CBORObject;
import id.walt.mdoc.COSECryptoProviderKeyInfo;
import id.walt.mdoc.SimpleCOSECryptoProvider;
import id.walt.mdoc.cose.COSESign1;
import id.walt.mdoc.dataelement.*;
import id.walt.mdoc.dataretrieval.DeviceResponse;
import id.walt.mdoc.doc.MDoc;
import id.walt.mdoc.mdocauth.DeviceAuthentication;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.trust.ResolvedIssuer;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Isolates optional walt.id types from the public API. CBOR, COSE signatures and MSO digests are handled by the libraries.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
class WaltMdocAdapter {

    // walt.id's MSO model predates the status extension; retain that authenticated extension separately below.
    private static final Cbor MSO_CBOR = CborKt.Cbor(Cbor.Default, builder -> {
        builder.setIgnoreUnknownKeys(true);
        return Unit.INSTANCE;
    });

    VerifiedCredential verify(final String raw, final VpTransaction transaction, final MdocVerifier verifier) throws Exception {
        val bytes = Base64.getUrlDecoder().decode(raw);
        require(bytes.length <= verifier.getMaxPresentationSize(), "oversized mdoc presentation");
        // The COSE dependency's decoder rejects duplicate map keys and trailing data, unlike Kotlin map reconstruction.
        CBORObject.DecodeFromBytes(bytes);
        val response = DeviceResponse.Companion.fromCBOR(bytes);
        require("1.0".equals(response.getVersion().getValue()) && response.getStatus().getValue().doubleValue() == 0
            && response.getDocumentErrors() == null && response.getDocuments().size() == 1,
            "mdoc requires a successful DeviceResponse containing exactly one document");
        val document = response.getDocuments().get(0);
        require(document.getErrors() == null, "mdoc contains document errors");
        val auth = Objects.requireNonNull(document.getIssuerSigned().getIssuerAuth(), "mdoc has no issuer authentication");
        checkSignature(auth);
        val issuer = verifier.resolveIssuer(readChain(auth));
        require(verifyIssuerSignature(document, issuer), "invalid mdoc issuer signature");
        val payload = Objects.requireNonNull(auth.getPayload(), "mdoc issuer signature must contain the MSO");
        val msoBytes = EncodedCBORElement.Companion.fromEncodedCBORElementData(payload).getValue();
        CBORObject.DecodeFromBytes(msoBytes);
        val mso = MSO_CBOR.decodeFromByteArray(MSO.Companion.serializer(), msoBytes);
        document.set_mso(mso);
        require("1.0".equals(mso.getVersion().getValue()), "unsupported mdoc MSO version");
        require(document.verifyDocType() && document.verifyValidity(), "invalid mdoc doctype or validity");
        val claims = readClaims(document);
        val request = JSONObjectUtils.parse(transaction.getRequestParameters());
        require(!request.containsKey("transaction_data"), "mdoc transaction data is not supported");
        verifyHolder(document, transaction, request);
        val result = new VerifiedCredential().setFormat(CredentialFormat.MSO_MDOC).setType(document.getDocType().getValue())
            .setIssuer(issuer.name()).setClaims(claims).setCryptographicHolderBinding(true)
            .setTrustedAuthorities(new LinkedHashMap<>(issuer.trustedAuthorities()));
        final MapElement signedMso = DataElement.Companion.fromCBOR(msoBytes);
        val status = signedMso.getValue().get(new MapKey("status"));
        if (status != null) {
            require(status instanceof MapElement && verifier.getStatusChecker() != null,
                "the mdoc contains status: configure a statusChecker to validate its MSO status object");
            verifier.getStatusChecker().accept(result, jsonMap((MapElement) status));
        }
        return result;
    }

    private boolean verifyIssuerSignature(final MDoc document, final ResolvedIssuer issuer) throws Exception {
        for (val key : issuer.keys()) {
            if (key instanceof AsymmetricJWK asymmetric && document.verifySignature(provider(asymmetric.toPublicKey()), "key")) {
                return true;
            }
        }
        return false;
    }

    private List<X509Certificate> readChain(final COSESign1 signature) throws Exception {
        val protectedHeaders = signature.decodeProtectedHeader().getValue();
        val unprotectedHeaders = ((MapElement) signature.getData().get(1)).getValue();
        val key = new MapKey(33);
        require(!(protectedHeaders.containsKey(key) && unprotectedHeaders.containsKey(key)), "duplicate mdoc x5chain header");
        val chain = protectedHeaders.containsKey(key) ? protectedHeaders.get(key) : unprotectedHeaders.get(key);
        val entries = chain instanceof ListElement list ? list.getValue() : chain == null ? List.<DataElement>of() : List.of(chain);
        require(!entries.isEmpty(), "mdoc requires an x5chain issuer certificate chain");
        val certificates = new ArrayList<X509Certificate>();
        for (val entry : entries) {
            require(entry instanceof ByteStringElement, "invalid mdoc x5chain certificate");
            try (val input = new ByteArrayInputStream(((ByteStringElement) entry).getValue())) {
                certificates.add((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(input));
                require(input.available() == 0, "trailing data in mdoc certificate");
            }
        }
        return certificates;
    }

    private Map<String, Object> readClaims(final MDoc document) {
        val claims = new LinkedHashMap<String, Object>();
        val namespaces = document.getIssuerSigned().getNameSpaces();
        val mso = Objects.requireNonNull(document.getMSO());
        if (namespaces == null) {
            return claims;
        }
        namespaces.forEach((namespace, items) -> {
            val values = new LinkedHashMap<String, Object>();
            val digestIds = new HashSet<Number>();
            // walt.id 0.11.0 MSO.verifySignedItems returns after its first item: invoke it once per item.
            for (val item : items) {
                CBORObject.DecodeFromBytes(item.getValue());
                require(mso.verifySignedItems(namespace, List.of(item)), "invalid mdoc attribute digest");
            }
            for (val item : document.getIssuerSignedItems(namespace)) {
                val name = item.getElementIdentifier().getValue();
                require(!values.containsKey(name) && digestIds.add(item.getDigestID().getValue()), "duplicate mdoc attribute or digest ID");
                values.put(name, jsonValue(item.getElementValue()));
            }
            claims.put(namespace, values);
        });
        return claims;
    }

    private void verifyHolder(final MDoc document, final VpTransaction transaction, final Map<String, Object> request) throws Exception {
        val signed = Objects.requireNonNull(document.getDeviceSigned(), "mdoc has no device authentication");
        val signature = Objects.requireNonNull(signed.getDeviceAuth().getDeviceSignature(), "mdoc requires a device signature");
        require(signed.getDeviceAuth().getDeviceMac() == null, "mdoc DeviceMAC is not supported");
        checkSignature(signature);
        require(signature.getPayload() == null, "mdoc device signature must have detached payload");
        CBORObject.DecodeFromBytes(signed.getNameSpaces().getValue());
        final MapElement deviceNamespaces = DataElement.Companion.fromCBOR(signed.getNameSpaces().getValue());
        require(deviceNamespaces.getValue().isEmpty(), "mdoc device-signed claims are not supported");
        val deviceKey = Objects.requireNonNull(document.getMSO()).getDeviceKeyInfo().getDeviceKey();
        require(!deviceKey.getValue().containsKey(new MapKey(-4)), "mdoc device key must be public");
        val key = new OneKey(CBORObject.DecodeFromBytes(deviceKey.toCBOR())).AsPublicKey();
        val crypto = provider(key);
        val nonce = string(request, NONCE);
        final DataElement thumbprint;
        if (transaction.getResponseMode().isEncrypted()) {
            require(transaction.getEncryptionKey() != null, "mdoc transaction has no response encryption key");
            thumbprint = new ByteStringElement(JWK.parse(transaction.getEncryptionKey()).toPublicJWK().computeThumbprint().decode());
        } else {
            thumbprint = new NullElement(null);
        }
        final List<ListElement> transcripts = new ArrayList<>();
        if (transaction.getResponseMode().isOverDcApi()) {
            val origins = Objects.requireNonNull(JSONObjectUtils.getStringList(request, EXPECTED_ORIGINS));
            require(!origins.isEmpty() && origins.stream().noneMatch(String::isBlank),
                "mdoc requires saved DC API origins");
            for (val origin : origins) {
                transcripts.add(transcript("OpenID4VPDCAPIHandover",
                    List.of(new StringElement(origin), new StringElement(nonce), thumbprint)));
            }
        } else {
            transcripts.add(transcript("OpenID4VPHandover", List.of(new StringElement(string(request, CLIENT_ID)),
                new StringElement(nonce), thumbprint, new StringElement(string(request, RESPONSE_URI)))));
        }
        for (val transcript : transcripts) {
            val authentication = new DeviceAuthentication(transcript, document.getDocType().getValue(), signed.getNameSpaces());
            if (document.verifyDeviceSignature(authentication, crypto, "key")) {
                return;
            }
        }
        throw new OpenId4VpException("mdoc device signature does not match the saved transaction");
    }

    /** OpenID4VP 1.0 B.2.6: the SHA-256 input is the CBOR array, not a tagged or base64-encoded value. */
    private ListElement transcript(final String label, final List<DataElement> info) throws Exception {
        val hash = MessageDigest.getInstance("SHA-256").digest(new ListElement(info).toCBOR());
        return new ListElement(List.of(new NullElement(null), new NullElement(null),
            new ListElement(List.of(new StringElement(label), new ByteStringElement(hash)))));
    }

    private SimpleCOSECryptoProvider provider(final PublicKey key) {
        require(key instanceof ECPublicKey ec && Curve.P_256.equals(Curve.forECParameterSpec(ec.getParams())),
            "mdoc supports only P-256 signature keys");
        // Only verify1 is used; walt.id's system-root trust and disabled revocation policy are never invoked.
        return new SimpleCOSECryptoProvider(List.of(new COSECryptoProviderKeyInfo("key", AlgorithmID.ECDSA_256,
            key, null, List.of(), List.of())));
    }

    private void checkSignature(final COSESign1 signature) {
        CBORObject.DecodeFromBytes(signature.getProtectedHeader());
        val headers = signature.decodeProtectedHeader().getValue();
        val unprotected = ((MapElement) signature.getData().get(1)).getValue();
        require(signature.getAlgorithm() == -7 && !headers.containsKey(new MapKey(2)) && !unprotected.containsKey(new MapKey(2))
            && headers.keySet().stream().noneMatch(unprotected::containsKey), "unsupported mdoc signature algorithm or COSE headers");
    }

    private String string(final Map<String, Object> values, final String key) throws Exception {
        val value = JSONObjectUtils.getString(values, key);
        require(value != null && !value.isBlank(), "mdoc requires saved request parameter " + key);
        return value;
    }

    private Map<String, Object> jsonMap(final MapElement map) {
        val result = new LinkedHashMap<String, Object>();
        map.getValue().forEach((key, value) -> {
            require(key.getType() == MapKeyType.string, "mdoc claim maps must have string keys");
            result.put(key.getStr(), jsonValue(value));
        });
        return result;
    }

    private Object jsonValue(final DataElement value) {
        if (value instanceof StringElement text) {
            return text.getValue();
        } else if (value instanceof NumberElement number) {
            require(Double.isFinite(number.getValue().doubleValue()), "non-finite mdoc claim value");
            return number.getValue();
        } else if (value instanceof BooleanElement bool) {
            return bool.getValue();
        } else if (value instanceof NullElement) {
            return null;
        } else if (value instanceof ByteStringElement bytes) {
            return Base64URL.encode(bytes.getValue()).toString();
        } else if (value instanceof DateTimeElement date) {
            return date.getValue().toString();
        } else if (value instanceof FullDateElement date) {
            return date.getValue().toString();
        } else if (value instanceof MapElement map) {
            return jsonMap(map);
        } else if (value instanceof ListElement list) {
            return list.getValue().stream().map(WaltMdocAdapter::jsonValue).toList();
        }
        throw new OpenId4VpException("unsupported mdoc claim value");
    }

    private void require(final boolean condition, final String message) {
        if (!condition) {
            throw new OpenId4VpException(message);
        }
    }
}
