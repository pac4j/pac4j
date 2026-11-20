package org.pac4j.dpop.credentials.extractor;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.store.ConcurrentMapStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.dpop.proof.DPoPNonceManager;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.dpop.DPoPTestHelper.*;
import static org.pac4j.dpop.util.DPoPConstants.*;

/**
 * Tests {@link DPoPAuthExtractor}.
 */
final class DPoPAuthExtractorTests {

    private static final String SECRET = "a-secret-shared-by-all-the-nodes";

    private final DPoPAuthExtractor extractor = new DPoPAuthExtractor();

    private final JWK key = generateKey();

    @Test
    void extractsNothingWithoutAuthorization() throws Exception {
        val context = request("GET", URL, null, proof(key, "GET", URL, null));

        assertTrue(extract(context).isEmpty());
        assertTrue(context.getRequestAttribute(ERROR_REQUEST_ATTRIBUTE).isEmpty());
    }

    @Test
    void refusesAnotherAuthenticationScheme() throws Exception {
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, null, proof(key, "GET", URL, accessToken))
            .addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken);

        val e = assertThrows(CredentialsException.class, () -> extract(context));

        assertEquals("Wrong prefix for header: Authorization", e.getMessage());
        assertTrue(context.getRequestAttribute(ERROR_REQUEST_ATTRIBUTE).isEmpty());
    }

    @Test
    void extractsTheAccessTokenWithAValidProof() throws Exception {
        val accessToken = accessToken(key, null);
        // the query string is not part of the URL of the proof (RFC 9449, section 4.2)
        val context = request("GET", URL + "?a=b", accessToken, proof(key, "GET", URL, accessToken));

        val credentials = (TokenCredentials) extract(context).orElseThrow();

        assertEquals(accessToken, credentials.getToken());
        assertTrue(context.getRequestAttribute(ERROR_REQUEST_ATTRIBUTE).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ES256", "ES384", "ES512", "RS256", "PS256"})
    void acceptsTheAsymmetricAlgorithms(final String algorithm) throws Exception {
        val algorithmKey = JwkHelper.generateKey(JWSAlgorithm.parse(algorithm));
        val accessToken = accessToken(algorithmKey, null);

        assertTrue(extract(request("POST", URL, accessToken, proof(algorithmKey, "POST", URL, accessToken))).isPresent());
    }

    @Test
    void refusesAnAlgorithmNotAccepted() throws Exception {
        extractor.setJwsAlgorithms(Set.of(JWSAlgorithm.ES256));
        val rsaKey = JwkHelper.generateKey(JWSAlgorithm.RS256);
        val accessToken = accessToken(rsaKey, null);

        assertRefused(request("GET", URL, accessToken, proof(rsaKey, "GET", URL, accessToken)),
            INVALID_DPOP_PROOF_ERROR, "algorithm not accepted: RS256");
    }

    @Test
    void refusesAMissingProof() throws Exception {
        assertRefused(request("GET", URL, accessToken(key, null), null), INVALID_DPOP_PROOF_ERROR, "Missing DPoP proof");
    }

    @Test
    void refusesAMalformedProof() throws Exception {
        assertRefused(request("GET", URL, accessToken(key, null), "not-a-jwt"), INVALID_DPOP_PROOF_ERROR, "Malformed DPoP proof");
    }

    @Test
    void refusesAProofForAnotherMethod() throws Exception {
        val accessToken = accessToken(key, null);

        assertRefused(request("POST", URL, accessToken, proof(key, "GET", URL, accessToken)), INVALID_DPOP_PROOF_ERROR, "htm");
    }

    @Test
    void refusesAProofForAnotherUrl() throws Exception {
        val accessToken = accessToken(key, null);

        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL + "/other", accessToken)),
            INVALID_DPOP_PROOF_ERROR, "htu");
    }

    @Test
    void refusesAProofTooOld() throws Exception {
        val accessToken = accessToken(key, null);

        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(-90), null)),
            INVALID_DPOP_PROOF_ERROR, "iat too old");
    }

    @Test
    void refusesAProofTooFarInTheFuture() throws Exception {
        val accessToken = accessToken(key, null);

        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(90), null)),
            INVALID_DPOP_PROOF_ERROR, "iat too far in future");
    }

    @Test
    void acceptsAProofWithinTheMaximumAgeAndTheClockSkew() throws Exception {
        val accessToken = accessToken(key, null);

        assertTrue(extract(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(-30), null))).isPresent());
        assertTrue(extract(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(30), null))).isPresent());
    }

    @Test
    void appliesTheMaximumAgeAndTheClockSkewDefined() throws Exception {
        extractor.setMaxAgeSeconds(10).setClockSkewSeconds(5);
        val accessToken = accessToken(key, null);

        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(-30), null)),
            INVALID_DPOP_PROOF_ERROR, "iat too old");
        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, secondsFromNow(30), null)),
            INVALID_DPOP_PROOF_ERROR, "iat too far in future");
    }

    @Test
    void refusesAMaximumAgeLessThanTheClockSkew() {
        extractor.setMaxAgeSeconds(10).setClockSkewSeconds(60);

        val e = assertThrows(TechnicalException.class, () -> extract(MockWebContext.create()));

        assertEquals("maxAgeSeconds cannot be less than clockSkewSeconds", e.getMessage());
    }

    @Test
    void refusesAProofWithoutTheHashOfTheAccessToken() throws Exception {
        assertRefused(request("GET", URL, accessToken(key, null), proof(key, "GET", URL, null)), INVALID_DPOP_PROOF_ERROR, "ath");
    }

    @Test
    void refusesAProofForAnotherAccessToken() throws Exception {
        assertRefused(request("GET", URL, accessToken(key, null), proof(key, "GET", URL, "another-access-token")),
            INVALID_TOKEN_ERROR, "access token hash");
    }

    @Test
    void refusesAnAccessTokenBoundToAnotherKey() throws Exception {
        // a stolen access token, sent with a proof of another key
        val stolenAccessToken = accessToken(generateKey(), null);

        assertRefused(request("GET", URL, stolenAccessToken, proof(key, "GET", URL, stolenAccessToken)),
            INVALID_TOKEN_ERROR, "doesn't match the JWK SHA-256 thumbprint confirmation");
    }

    @Test
    void refusesAnAccessTokenNotBoundToAKey() throws Exception {
        val unboundAccessToken = accessToken(null, null);

        assertRefused(request("GET", URL, unboundAccessToken, proof(key, "GET", URL, unboundAccessToken)),
            INVALID_TOKEN_ERROR, "not bound to a DPoP key");
        assertRefused(request("GET", URL, "opaque-access-token", proof(key, "GET", URL, "opaque-access-token")),
            INVALID_TOKEN_ERROR, "not bound to a DPoP key");
    }

    @Test
    void acceptsAnOpaqueAccessTokenWhoseConfirmationIsRetrievedElsewhere() throws Exception {
        val introspectingExtractor = new DPoPAuthExtractor() {
            @Override
            protected JWKThumbprintConfirmation retrieveConfirmation(final CallContext ctx, final String accessToken) {
                try {
                    return JWKThumbprintConfirmation.of(key);
                } catch (final Exception e) {
                    throw new TechnicalException(e);
                }
            }
        };
        val context = request("GET", URL, "opaque-access-token", proof(key, "GET", URL, "opaque-access-token"));

        assertTrue(introspectingExtractor.extract(new CallContext(context, new MockSessionStore())).isPresent());
    }

    @Test
    void refusesAProofNotSignedByItsKey() throws Exception {
        val accessToken = accessToken(key, null);
        val proof = SignedJWT.parse(proof(key, "GET", URL, accessToken));
        val forged = new SignedJWT(proof.getHeader(), proof.getJWTClaimsSet());
        forged.sign(new ECDSASigner((ECKey) generateKey()));

        assertRefused(request("GET", URL, accessToken, forged.serialize()), INVALID_DPOP_PROOF_ERROR, "Invalid signature");
    }

    @Test
    void refusesAProofOfAnotherType() throws Exception {
        val accessToken = accessToken(key, null);
        val proof = SignedJWT.parse(proof(key, "GET", URL, accessToken));
        val jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256).type(JOSEObjectType.JWT).jwk(key.toPublicJWK()).build(),
            proof.getJWTClaimsSet());
        jwt.sign(new ECDSASigner((ECKey) key));

        assertRefused(request("GET", URL, accessToken, jwt.serialize()), INVALID_DPOP_PROOF_ERROR, "typ");
    }

    @Test
    void refusesAProofAlreadyUsed() throws Exception {
        val accessToken = accessToken(key, null);
        val proof = proof(key, "GET", URL, accessToken);

        assertTrue(extract(request("GET", URL, accessToken, proof)).isPresent());
        assertRefused(request("GET", URL, accessToken, proof), INVALID_DPOP_PROOF_ERROR, "already used");
        assertTrue(extract(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken))).isPresent());
    }

    @Test
    void retainsProofIdentifiersForTheMaximumAgePlusClockSkew() {
        extractor.setMaxAgeSeconds(120).setClockSkewSeconds(15);
        extractor.init();

        val store = assertInstanceOf(ConcurrentMapStore.class, extractor.getStore());
        assertEquals(135, store.getTimeout());
        assertEquals(TimeUnit.SECONDS, store.getTimeUnit());
    }

    @Test
    void savesAHashOfTheProofIdentifierInTheStoreDefined() throws Exception {
        val saved = new HashMap<String, Boolean>();
        extractor.setStore(mapStore(saved));
        val accessToken = accessToken(key, null);
        val proof = proof(key, "GET", URL, accessToken);

        assertTrue(extract(request("GET", URL, accessToken, proof)).isPresent());

        assertEquals(1, saved.size());
        val savedKey = saved.keySet().iterator().next();
        assertTrue(savedKey.startsWith(key.computeThumbprint() + ":"), savedKey);
        assertFalse(savedKey.contains(SignedJWT.parse(proof).getJWTClaimsSet().getJWTID()), savedKey);
        // forgetting the proof makes it usable again
        saved.clear();
        assertTrue(extract(request("GET", URL, accessToken, proof)).isPresent());
    }

    @Test
    void ignoresANonceWhenNoneIsRequired() throws Exception {
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), "nonce-of-another-server"));

        assertTrue(extract(context).isPresent());
        assertFalse(context.getResponseHeaders().containsKey(DPOP_NONCE_HEADER));
    }

    @Test
    void requiresANonceWhenASecretIsDefined() throws Exception {
        extractor.setNonceSecret(SECRET);
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken));

        assertRefused(context, USE_DPOP_NONCE_ERROR, "A valid nonce is required");

        // the nonce provided makes the next proof accepted, as many times as needed until it expires
        val nonce = context.getResponseHeaders().get(DPOP_NONCE_HEADER);
        assertTrue(new DPoPNonceManager(SECRET, 300).isValid(nonce));
        for (var i = 0; i < 2; i++) {
            val nextContext = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), nonce));
            assertTrue(extract(nextContext).isPresent());
            assertFalse(nextContext.getResponseHeaders().containsKey(DPOP_NONCE_HEADER));
        }
    }

    @Test
    void refusesANonceItDidNotGenerate() throws Exception {
        extractor.setNonceSecret(SECRET);
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, accessToken,
            proof(key, "GET", URL, accessToken, new Date(), new DPoPNonceManager("another-secret", 300).generate(Instant.now())));

        assertRefused(context, USE_DPOP_NONCE_ERROR, "A valid nonce is required");
        assertTrue(new DPoPNonceManager(SECRET, 300).isValid(context.getResponseHeaders().get(DPOP_NONCE_HEADER)));
    }

    @Test
    void refusesAnExpiredNonce() throws Exception {
        extractor.setNonceSecret(SECRET);
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), nonceAged(310)));

        assertRefused(context, USE_DPOP_NONCE_ERROR, "A valid nonce is required");
        assertTrue(new DPoPNonceManager(SECRET, 300).isValid(context.getResponseHeaders().get(DPOP_NONCE_HEADER)));
    }

    @Test
    void appliesTheMaximumAgeOfTheNonceDefined() throws Exception {
        extractor.setNonceSecret(SECRET).setNonceMaxAgeSeconds(600);
        val accessToken = accessToken(key, null);

        assertTrue(extract(request("GET", URL, accessToken,
            proof(key, "GET", URL, accessToken, new Date(), nonceAged(310)))).isPresent());
        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), nonceAged(610))),
            USE_DPOP_NONCE_ERROR, "A valid nonce is required");
    }

    @Test
    void renewsANonceGettingOldInTheResponseToAnAcceptedRequest() throws Exception {
        extractor.setNonceSecret(SECRET);
        val accessToken = accessToken(key, null);
        val oldNonce = nonceAged(200);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), oldNonce));

        assertTrue(extract(context).isPresent());

        val newNonce = context.getResponseHeaders().get(DPOP_NONCE_HEADER);
        assertNotEquals(oldNonce, newNonce);
        val manager = new DPoPNonceManager(SECRET, 300);
        assertTrue(manager.isValid(newNonce));
        assertFalse(manager.shouldBeRenewed(newNonce));
    }

    @Test
    void doesNotRenewTheNonceOfARefusedProof() throws Exception {
        extractor.setNonceSecret(SECRET);
        val accessToken = accessToken(key, null);
        val context = request("POST", URL, accessToken, proof(key, "GET", URL, accessToken, new Date(), nonceAged(200)));

        assertRefused(context, INVALID_DPOP_PROOF_ERROR, "htm");
        assertFalse(context.getResponseHeaders().containsKey(DPOP_NONCE_HEADER));
    }

    @Test
    void doesNotMarkAsUsedAProofRefusedForItsNonce() throws Exception {
        val saved = new HashMap<String, Boolean>();
        extractor.setNonceSecret(SECRET).setStore(mapStore(saved));
        val accessToken = accessToken(key, null);

        assertRefused(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken)),
            USE_DPOP_NONCE_ERROR, "A valid nonce is required");

        assertTrue(saved.isEmpty());
    }

    private Store<String, Boolean> mapStore(final Map<String, Boolean> map) {
        return new Store<>() {
            @Override
            public Optional<Boolean> get(final String key) {
                return Optional.ofNullable(map.get(key));
            }

            @Override
            public void set(final String key, final Boolean value) {
                map.put(key, value);
            }

            @Override
            public void remove(final String key) {
                map.remove(key);
            }
        };
    }

    private Optional<?> extract(final MockWebContext context) {
        return extractor.extract(new CallContext(context, new MockSessionStore()));
    }

    private void assertRefused(final MockWebContext context, final String error, final String message) {
        val e = assertThrows(CredentialsException.class, () -> extract(context));

        assertTrue(e.getMessage().contains(message), e.getMessage());
        assertEquals(error, context.getRequestAttribute(ERROR_REQUEST_ATTRIBUTE).orElse(null));
    }

    private String nonceAged(final int seconds) {
        return new DPoPNonceManager(SECRET, 300).generate(Instant.now().minusSeconds(seconds));
    }

    private Date secondsFromNow(final int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000L);
    }
}
