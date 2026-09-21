package org.pac4j.openid4vp.verifier;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.dcql.CredentialQuery;
import org.pac4j.openid4vp.dcql.DcqlValidator;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private VpTransaction transaction;
    private Map<String, Object> issuerClaims;
    private List<String> disclosures;
    private Map<String, Object> proofClaims;

    @BeforeEach
    void setUp() throws Exception {
        issuerKey = new ECKeyGenerator(Curve.P_256).keyID("issuer-key").generate();
        holderKey = new ECKeyGenerator(Curve.P_256).generate();
        verifier = new SdJwtVcVerifier().setTrustedIssuers(Map.of(ISSUER,
            new SdJwtVcTrustedIssuer(new JWKSet(issuerKey.toPublicJWK()))
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
        val jwt = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.ES256).type(new JOSEObjectType(type)).keyID(key.getKeyID()).build(),
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
    void rejectsUnknownIssuer() throws Exception {
        issuerClaims.put("iss", "https://untrusted.example");
        val raw = presentation();
        assertThrows(OpenId4VpException.class, () -> verify(raw));
    }

    @Test
    void rejectsUnconfiguredTrust() throws Exception {
        verifier.setTrustedIssuers(Map.of());
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
    void rejectsModifiedDisclosureWithoutLeakingIt() throws Exception {
        disclosures.set(0, disclose("salt-name", "given_name", "SECRET-ATTACKER-VALUE"));
        val raw = presentation();
        val error = assertThrows(OpenId4VpException.class, () -> verify(raw));
        assertFalse(error.toString().contains("SECRET"));
        assertNull(error.getCause());
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
