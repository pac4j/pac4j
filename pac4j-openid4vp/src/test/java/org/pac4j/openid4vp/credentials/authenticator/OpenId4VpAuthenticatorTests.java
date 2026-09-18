package org.pac4j.openid4vp.credentials.authenticator;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.dcql.CredentialQuery;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.CredentialVerifier;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Response validation with real JWE encryption and an explicitly fake credential verifier.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpAuthenticatorTests {

    private ECKey key;
    private OpenId4VpConfiguration configuration;
    private OpenId4VpAuthenticator authenticator;
    private DcqlQuery query;
    private Map<String, Object> request;
    private VpTransaction transaction;
    private final List<String> verifiedInputs = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        key = new ECKeyGenerator(Curve.P_256).keyID("response-key").algorithm(JWEAlgorithm.ECDH_ES).generate();
        query = new DcqlQuery().addCredential(new CredentialQuery("pid", CredentialFormat.SD_JWT_VC)
            .setVctValues("urn:pid").addClaim("name"));
        configuration = new OpenId4VpConfiguration().setDcqlQuery(query);
        configuration.addCredentialVerifier(new CredentialVerifier() {
            @Override
            public CredentialFormat getFormat() {
                return CredentialFormat.SD_JWT_VC;
            }

            @Override
            public VerifiedCredential verify(final String raw, final VpTransaction tx, final OpenId4VpConfiguration config) {
                verifiedInputs.add(raw);
                if ("invalid".equals(raw)) {
                    throw new OpenId4VpException("invalid test credential");
                }
                if ("null".equals(raw)) {
                    return null;
                }
                return new VerifiedCredential().setFormat(getFormat()).setType("urn:pid")
                    .setCryptographicHolderBinding(true).setClaims(Map.of("name", raw));
            }
        });
        authenticator = new OpenId4VpAuthenticator(new OpenId4VpClient(configuration));
        request = new LinkedHashMap<>(Map.of(CLIENT_ID, "redirect_uri:https://verifier.example/callback", NONCE, "nonce",
            STATE, "expected-state", CLIENT_METADATA,
            Map.of(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED, List.of("A128GCM", "A256GCM"))));
        transaction = new VpTransaction().setId("transaction").setNonce("nonce").setState("expected-state")
            .setExpiresAt(Instant.now().plusSeconds(60)).setEncryptionKey(key.toJSONString())
            .setResponseMode(ResponseMode.DIRECT_POST_JWT).setDcqlQuery(query.toJsonString())
            .setRequestParameters(JSONObjectUtils.toJSONString(request));
    }

    private String encrypt(final Map<String, Object> response, final ECKey recipient,
                           final JWEAlgorithm algorithm, final EncryptionMethod method, final String kid) throws Exception {
        val jwe = new JWEObject(new JWEHeader.Builder(algorithm, method).keyID(kid).build(), new Payload(response));
        jwe.encrypt(new ECDHEncrypter(recipient.toPublicJWK()));
        return jwe.serialize();
    }

    private void encrypted(final Map<String, Object> response) throws Exception {
        transaction.setRawResponse(encrypt(response, key, JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM, key.getKeyID()));
    }

    private Map<String, Object> response() {
        return Map.of(STATE, "expected-state", VP_TOKEN, Map.of("pid", List.of("Alice")));
    }

    private VerifiablePresentationCredentials validate() {
        val credentials = new VerifiablePresentationCredentials(transaction);
        return (VerifiablePresentationCredentials) authenticator.validate(null, credentials).orElseThrow();
    }

    private void clear(final String vpToken) {
        transaction.setResponseMode(ResponseMode.DIRECT_POST).setRawResponse(null).setRawVpToken(vpToken)
            .setResponseState("expected-state");
    }

    @Test
    void decryptsBothAdvertisedEncryptionMethods() throws Exception {
        for (val method : List.of(EncryptionMethod.A128GCM, EncryptionMethod.A256GCM)) {
            transaction.setRawResponse(encrypt(response(), key, JWEAlgorithm.ECDH_ES, method, key.getKeyID()));
            val validated = validate();
            assertEquals("Alice", validated.getVerifiedCredentials().get("pid").get(0).getClaims().get("name"));
        }
        assertEquals(List.of("Alice", "Alice"), verifiedInputs);
    }

    @Test
    void rejectsWrongKeyWrongKidAndAlteredCiphertext() throws Exception {
        val other = new ECKeyGenerator(Curve.P_256).generate();
        transaction.setRawResponse(encrypt(response(), other, JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM, key.getKeyID()));
        assertThrows(OpenId4VpException.class, this::validate);
        for (val kid : java.util.Arrays.asList(null, "other-key")) {
            transaction.setRawResponse(encrypt(response(), key, JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM, kid));
            assertThrows(OpenId4VpException.class, this::validate);
        }
        encrypted(response());
        val parts = transaction.getRawResponse().split("\\.", -1);
        parts[3] = (parts[3].startsWith("A") ? "B" : "A") + parts[3].substring(1);
        transaction.setRawResponse(String.join(".", parts));
        assertThrows(OpenId4VpException.class, this::validate);
        assertTrue(verifiedInputs.isEmpty());
    }

    @Test
    void rejectsUnadvertisedAlgorithms() throws Exception {
        transaction.setRawResponse(encrypt(response(), key, JWEAlgorithm.ECDH_ES_A128KW,
            EncryptionMethod.A128GCM, key.getKeyID()));
        assertThrows(OpenId4VpException.class, this::validate);
        transaction.setRawResponse(encrypt(response(), key, JWEAlgorithm.ECDH_ES,
            EncryptionMethod.A128CBC_HS256, key.getKeyID()));
        assertThrows(OpenId4VpException.class, this::validate);
        request.put(CLIENT_METADATA, Map.of(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED, List.of("A256GCM")));
        transaction.setRequestParameters(JSONObjectUtils.toJSONString(request));
        encrypted(response());
        assertThrows(OpenId4VpException.class, this::validate);
    }

    @Test
    void rejectsCompressionCriticalHeadersAndMissingDecryptionKey() throws Exception {
        for (val header : List.of(
            new JWEHeader.Builder(JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM).keyID(key.getKeyID())
                .compressionAlgorithm(CompressionAlgorithm.DEF).build(),
            new JWEHeader.Builder(JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM).keyID(key.getKeyID())
                .customParam("extension", true).criticalParams(Set.of("extension")).build())) {
            val jwe = new JWEObject(header, new Payload(response()));
            jwe.encrypt(new ECDHEncrypter(key.toPublicJWK()));
            transaction.setRawResponse(jwe.serialize());
            assertThrows(OpenId4VpException.class, this::validate);
        }
        encrypted(response());
        transaction.setEncryptionKey(null);
        assertThrows(OpenId4VpException.class, this::validate);
    }

    @Test
    void checksStateInsideTheEncryptedEnvelope() throws Exception {
        for (val response : List.of(Map.of(VP_TOKEN, Map.of("pid", List.of("Alice"))),
            Map.of(STATE, "wrong", VP_TOKEN, Map.of("pid", List.of("Alice"))),
            Map.of(STATE, 42, VP_TOKEN, Map.of("pid", List.of("Alice"))))) {
            encrypted(new LinkedHashMap<>(response));
            transaction.setResponseState("expected-state");
            assertThrows(OpenId4VpException.class, this::validate);
        }
        assertTrue(verifiedInputs.isEmpty());
    }

    @Test
    void checksClearStateAndRejectsEncryptionDowngrade() {
        clear("{\"pid\":[\"Alice\"]}");
        assertDoesNotThrow(this::validate);
        transaction.setResponseState("wrong");
        assertThrows(OpenId4VpException.class, this::validate);
        transaction.setResponseState(null);
        assertThrows(OpenId4VpException.class, this::validate);
        transaction.setResponseState("expected-state").setResponseMode(ResponseMode.DIRECT_POST_JWT);
        assertThrows(OpenId4VpException.class, this::validate);
    }

    @Test
    void supportsDcApiWithoutState() throws Exception {
        transaction.setResponseMode(ResponseMode.DC_API_JWT);
        encrypted(Map.of(VP_TOKEN, Map.of("pid", List.of("Alice"))));
        assertDoesNotThrow(this::validate);
        clear("{\"pid\":[\"Alice\"]}");
        transaction.setResponseMode(ResponseMode.DC_API).setResponseState(null);
        assertDoesNotThrow(this::validate);
    }

    @Test
    void rejectsEncryptedErrorsAndMalformedVpTokens() throws Exception {
        for (val payload : List.of(Map.of(ERROR, "access_denied"), Map.of(VP_TOKEN, "not-an-object"), Map.of("other", true))) {
            val response = new LinkedHashMap<String, Object>(payload);
            response.put(STATE, "expected-state");
            encrypted(response);
            assertThrows(OpenId4VpException.class, this::validate);
        }
        for (val raw : List.of("not-json", "[]", "{}", "{\"pid\":[]}", "{\"pid\":\"Alice\"}",
            "{\"pid\":[null]}", "{\"pid\":[42]}", "{\"pid\":[{}]}", "{\"pid\":[\"\"]}")) {
            clear(raw);
            assertThrows(OpenId4VpException.class, this::validate, raw);
        }
    }

    @Test
    void rejectsUnknownQueriesAndUnexpectedMultiplicity() {
        clear("{\"other\":[\"Alice\"]}");
        assertThrows(OpenId4VpException.class, this::validate);
        clear("{\"pid\":[\"Alice\",\"Bob\"]}");
        assertThrows(OpenId4VpException.class, this::validate);
        assertTrue(verifiedInputs.isEmpty());
    }

    @Test
    void verifiesAndKeepsEveryPresentationWhenMultipleIsRequested() {
        query.getCredentials().get(0).setMultiple(true);
        transaction.setDcqlQuery(query.toJsonString());
        clear("{\"pid\":[\"Alice\",\"Bob\"]}");
        assertEquals(2, validate().getVerifiedCredentials().get("pid").size());
        assertEquals(List.of("Alice", "Bob"), verifiedInputs);
    }

    @Test
    void neverPublishesPartialOrStaleVerifiedCredentials() {
        query.getCredentials().get(0).setMultiple(true);
        transaction.setDcqlQuery(query.toJsonString());
        clear("{\"pid\":[\"Alice\",\"invalid\"]}");
        val credentials = new VerifiablePresentationCredentials(transaction);
        credentials.getVerifiedCredentials().put("old", List.of(new VerifiedCredential()));
        assertThrows(OpenId4VpException.class, () -> authenticator.validate(null, credentials));
        assertTrue(credentials.getVerifiedCredentials().isEmpty());
        assertTrue(credentials.getVpToken().isEmpty());
        clear("{\"pid\":[\"null\"]}");
        assertThrows(OpenId4VpException.class, this::validate);
    }

    @Test
    void usesSavedQueryAndRejectsExpiredOrIncompleteTransactions() {
        clear("{\"pid\":[\"Alice\"]}");
        configuration.setDcqlQuery(new DcqlQuery());
        assertDoesNotThrow(this::validate);
        transaction.setExpiresAt(Instant.now().minusSeconds(1));
        assertThrows(OpenId4VpException.class, this::validate);
        transaction.setExpiresAt(Instant.now().plusSeconds(60)).setRequestParameters(null);
        assertThrows(OpenId4VpException.class, this::validate);
    }
}
