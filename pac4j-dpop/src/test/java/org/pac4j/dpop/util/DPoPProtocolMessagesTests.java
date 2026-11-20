package org.pac4j.dpop.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.read.ListAppender;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;
import org.pac4j.dpop.DPoPTestHelper;
import org.pac4j.dpop.client.direct.DirectDPoPAuthClient;
import org.pac4j.dpop.credentials.extractor.DPoPAuthExtractor;
import org.pac4j.dpop.http.DPoPHttpInterceptor;
import org.pac4j.dpop.proof.DPoPNonceManager;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DPoPProtocolMessages}, through the {@link DPoPHttpInterceptor} and the {@link DPoPAuthExtractor}.
 */
final class DPoPProtocolMessagesTests {

    private static final String NONCE_ERROR_BODY = "{\"error\":\"use_dpop_nonce\"}";

    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    private final DPoPHttpInterceptor interceptor = new DPoPHttpInterceptor();

    private final CallContext ctx = new CallContext(MockWebContext.create(), new MockSessionStore());

    private Logger logger;

    @BeforeEach
    void captureLogs() {
        logger = (Logger) LoggerFactory.getLogger("PROTOCOL_MESSAGE.DPOP");
        logger.setLevel(Level.DEBUG);
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void releaseLogs() {
        logger.detachAppender(appender);
        logger.setLevel(null);
    }

    @Test
    void logsTheProofsSentAndTheNoncesReceived() throws Exception {
        val request = new OAuthHttpRequest("POST", "https://server.example/token?client_secret=secret",
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER));

        interceptor.configureRequest(request, ctx);
        interceptor.handleResponse(request, new OAuthHttpResponse(400, Map.of("DPoP-Nonce", "nonce-1"), NONCE_ERROR_BODY), ctx);
        interceptor.handleResponse(request, new OAuthHttpResponse(200, Map.of("DPoP-Nonce", "nonce-2"), "{}"), ctx);
        interceptor.handleResponse(request, new OAuthHttpResponse(200, Map.of(), "{}"), ctx);

        val proof = request.headers().get("DPoP");
        assertNotNull(SignedJWT.parse(proof).getHeader().getJWK());
        assertEquals(List.of(
            ">>> server POST https://server.example/token DPoP: " + proof,
            "<<< server https://server.example DPoP-Nonce: nonce-1 (use_dpop_nonce: the request is sent again)",
            "<<< server https://server.example DPoP-Nonce: nonce-2"), lines());
        // neither the private key saved in the session nor the query string is logged
        val key = (String) ctx.sessionStore().get(ctx.webContext(), DPoPConstants.KEY_SESSION_ATTRIBUTE_PREFIX + "ES256").orElseThrow();
        assertTrue(key.contains("\"d\""));
        assertFalse(String.join("\n", lines()).contains("secret"));
    }

    @Test
    void logsTheDecodedProofAtTraceLevelOnTheInterceptorLogger() throws Exception {
        val interceptorLogger = (Logger) LoggerFactory.getLogger(DPoPHttpInterceptor.class);
        val decoded = new ListAppender<ILoggingEvent>();
        decoded.start();
        interceptorLogger.addAppender(decoded);
        interceptorLogger.setLevel(Level.TRACE);
        try {
            val request = new OAuthHttpRequest("GET", "https://server.example/profile", new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
            request.headers().put("Authorization", "Bearer access-token");

            interceptor.configureRequest(request, ctx);

            val proof = SignedJWT.parse(request.headers().get("DPoP"));
            val traces = decoded.list.stream().filter(event -> event.getLevel() == Level.TRACE)
                .map(ILoggingEvent::getFormattedMessage).toList();
            assertEquals(List.of("DPoP proof: header=" + proof.getHeader() + " claims=" + proof.getPayload()), traces);
            assertTrue(traces.get(0).contains("\"typ\":\"dpop+jwt\""), traces.get(0));
            assertTrue(traces.get(0).contains("\"htu\":\"https://server.example/profile\""), traces.get(0));
            assertTrue(traces.get(0).contains("\"ath\":\"" + proof.getJWTClaimsSet().getStringClaim("ath") + "\""), traces.get(0));
            assertFalse(traces.get(0).contains("\"d\""));
        } finally {
            interceptorLogger.detachAppender(decoded);
            interceptorLogger.setLevel(null);
        }
    }

    @Test
    void logsTheProofsReceivedAndTheNoncesSentOnTheServerSide() throws Exception {
        val key = DPoPTestHelper.generateKey();
        val accessToken = DPoPTestHelper.accessToken(key, null);
        val proof = DPoPTestHelper.proof(key, "GET", DPoPTestHelper.URL, accessToken);
        val context = DPoPTestHelper.request("GET", DPoPTestHelper.URL + "?secret=secret", accessToken, proof);

        assertThrows(CredentialsException.class, () -> new DPoPAuthExtractor().setNonceSecret("nonce-secret")
            .extract(new CallContext(context, new MockSessionStore())));

        assertEquals(List.of(
            "<<< client GET " + DPoPTestHelper.URL + " DPoP proof received",
            ">>> client DPoP-Nonce: " + context.getResponseHeaders().get("DPoP-Nonce") + " (use_dpop_nonce)"), lines());
        assertFalse(String.join("\n", lines()).contains("secret="));
    }

    @Test
    void refusesAProofCarryingAPrivateKeyWithoutLoggingIt() throws Exception {
        val key = (ECKey) DPoPTestHelper.generateKey();
        val accessToken = DPoPTestHelper.accessToken(key, null);
        val proof = SignedJWT.parse(DPoPTestHelper.proof(key, "GET", DPoPTestHelper.URL, accessToken));
        val header = Base64URL.encode("{\"typ\":\"dpop+jwt\",\"alg\":\"ES256\",\"jwk\":" + key.toJSONString() + "}");
        val payload = proof.getPayload().toBase64URL();
        val signature = new ECDSASigner(key).sign(new JWSHeader(JWSAlgorithm.ES256),
            (header + "." + payload).getBytes(StandardCharsets.UTF_8));

        val received = header + "." + payload + "." + signature;
        val context = DPoPTestHelper.request("GET", DPoPTestHelper.URL, accessToken, received);
        val client = new DirectDPoPAuthClient(DPoPTestHelper.issuerAuthenticator());
        val clientLogger = (Logger) LoggerFactory.getLogger(DirectDPoPAuthClient.class);
        val previousLevel = clientLogger.getLevel();
        clientLogger.setLevel(Level.DEBUG);
        clientLogger.addAppender(appender);
        try {
            assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());
        } finally {
            clientLogger.detachAppender(appender);
            clientLogger.setLevel(previousLevel);
        }

        assertEquals(DPoPConstants.INVALID_DPOP_PROOF_ERROR,
            context.getRequestAttribute(DPoPConstants.ERROR_REQUEST_ATTRIBUTE).orElseThrow());
        assertTrue(lines().contains("<<< client GET " + DPoPTestHelper.URL + " DPoP proof received"));
        assertTrue(appender.list.stream().anyMatch(event -> event.getLoggerName().equals(clientLogger.getName())
            && event.getLevel() == Level.INFO && event.getFormattedMessage().contains("Malformed DPoP proof")));
        for (val event : appender.list) {
            val cause = event.getThrowableProxy();
            val log = event.getFormattedMessage() + (cause == null ? "" : ThrowableProxyUtil.asString(cause));
            assertFalse(log.contains(header.toString()));
            assertFalse(log.contains(key.getD().toString()));
        }
    }

    @Test
    void logsTheNonceRenewedOnTheServerSide() throws Exception {
        val key = DPoPTestHelper.generateKey();
        val accessToken = DPoPTestHelper.accessToken(key, null);
        val oldNonce = new DPoPNonceManager("nonce-secret", 300).generate(Instant.now().minusSeconds(200));
        val proof = DPoPTestHelper.proof(key, "GET", DPoPTestHelper.URL, accessToken, new Date(), oldNonce);
        val context = DPoPTestHelper.request("GET", DPoPTestHelper.URL, accessToken, proof);

        new DPoPAuthExtractor().setNonceSecret("nonce-secret").extract(new CallContext(context, new MockSessionStore()));

        assertEquals(List.of(
            "<<< client GET " + DPoPTestHelper.URL + " DPoP proof received",
            ">>> client DPoP-Nonce: " + context.getResponseHeaders().get("DPoP-Nonce")), lines());
    }

    @Test
    void logsNothingWhenDisabled() {
        logger.setLevel(Level.INFO);

        interceptor.configureRequest(new OAuthHttpRequest("POST", "https://server.example/token",
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER)), ctx);

        assertTrue(lines().isEmpty());
    }

    private List<String> lines() {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }
}
