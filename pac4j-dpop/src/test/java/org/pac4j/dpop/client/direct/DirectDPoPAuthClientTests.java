package org.pac4j.dpop.client.direct;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.dpop.credentials.extractor.DPoPAuthExtractor;
import org.pac4j.dpop.http.DPoPHttpInterceptor;
import org.pac4j.dpop.proof.DPoPNonceManager;
import org.pac4j.dpop.util.DPoPConstants;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsConstants;
import org.pac4j.test.util.TestsHelper;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.dpop.DPoPTestHelper.*;

/**
 * This class tests the {@link DirectDPoPAuthClient} class.
 */
final class DirectDPoPAuthClientTests implements TestsConstants {

    private static final String SECRET = "a-secret-shared-by-all-the-nodes";

    private static final String ALGS = "algs=\"ES256 ES384 ES512 PS256 PS384 PS512 RS256 RS384 RS512\"";

    /** Accepts any access token, the profile being identified by it. */
    private static final Authenticator TOKEN_AUTHENTICATOR = (ctx, credentials) -> {
        val profile = new CommonProfile();
        profile.setId(((TokenCredentials) credentials).getToken());
        credentials.setUserProfile(profile);
        return Optional.of(credentials);
    };

    private final JWK key = generateKey();

    @Test
    void testMissingTokenAuthenticator() {
        val client = new DirectDPoPAuthClient((Authenticator) null);
        TestsHelper.expectException(() -> client.getCredentials(new CallContext(MockWebContext.create(),
            new MockSessionStore())), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    void testMissingProfileCreator() {
        val client = new DirectDPoPAuthClient((ProfileCreator) null);
        TestsHelper.expectException(() -> client.getCredentials(new CallContext(MockWebContext.create(),
            new MockSessionStore())), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    void testMissingProfileCreator2() {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR, null);
        TestsHelper.expectException(() -> client.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
            new TokenCredentials(TOKEN)), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    void testHasDefaultProfileCreatorAndExtractor() {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        client.init();
        assertInstanceOf(DPoPAuthExtractor.class, client.getCredentialsExtractor());
    }

    @Test
    void testAuthentication() throws Exception {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        val accessToken = accessToken(key);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken));
        val ctx = new CallContext(context, new MockSessionStore());

        val credentials = (TokenCredentials) client.getCredentials(ctx).orElseThrow();
        assertEquals(accessToken, credentials.getToken());
        val authnCredentials = client.validateCredentials(ctx, credentials).orElseThrow();
        val profile = client.getUserProfile(ctx, authnCredentials).orElseThrow();

        assertEquals(accessToken, profile.getId());
        assertEquals("", context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void testProfileCreation() throws Exception {
        val client = new DirectDPoPAuthClient((ProfileCreator) (ctx, credentials) -> {
            UserProfile profile = new CommonProfile();
            profile.setId(KEY);
            return Optional.of(profile);
        });
        val accessToken = accessToken(key);
        val ctx = new CallContext(request("GET", URL, accessToken, proof(key, "GET", URL, accessToken)), new MockSessionStore());

        val credentials = client.getCredentials(ctx).orElseThrow();
        val authnCredentials = client.validateCredentials(ctx, credentials).orElseThrow();

        assertEquals(KEY, client.getUserProfile(ctx, authnCredentials).orElseThrow().getId());
    }

    @Test
    void challengesARequestWithoutAuthorization() {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        val context = MockWebContext.create();

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheRealmAndTheAlgorithmsDefined() {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        client.setRealmName("api");
        client.setCredentialsExtractor(new DPoPAuthExtractor().setJwsAlgorithms(Set.of(JWSAlgorithm.PS256)));
        val context = MockWebContext.create();

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"api\", algs=\"PS256\"", context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheErrorOfARefusedProof() throws Exception {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        val accessToken = accessToken(key);
        val context = request("POST", URL, accessToken, proof(key, "GET", URL, accessToken));

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_dpop_proof\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheErrorOfAnAccessTokenBoundToAnotherKey() throws Exception {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        val stolenAccessToken = accessToken(generateKey());
        val context = request("GET", URL, stolenAccessToken, proof(key, "GET", URL, stolenAccessToken));

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_token\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheErrorOfAnAccessTokenRefusedByTheAuthenticator() throws Exception {
        val client = new DirectDPoPAuthClient((Authenticator) (ctx, credentials) -> Optional.empty());
        val accessToken = accessToken(key);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken));
        val ctx = new CallContext(context, new MockSessionStore());

        val credentials = client.getCredentials(ctx).orElseThrow();
        assertTrue(client.validateCredentials(ctx, credentials).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_token\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithANonceWhenASecretIsDefined() throws Exception {
        val client = nonceClient();
        val accessToken = accessToken(key);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken));

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"use_dpop_nonce\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
        assertNotNull(context.getResponseHeaders().get(DPoPConstants.DPOP_NONCE_HEADER));
    }

    @Test
    void authenticatesTheRequestsOfTheDPoPHttpInterceptor() throws Exception {
        val client = nonceClient();
        val interceptor = new DPoPHttpInterceptor();
        // the application calling the protected resource: its key pair is generated in its web session by the first proof
        val callerCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        interceptor.configureRequest(new OAuthHttpRequest("POST", "https://server.example.org/token",
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER)), callerCtx);
        val callerKey = JWK.parse((String) callerCtx.sessionStore().get(callerCtx.webContext(),
            DPoPConstants.KEY_SESSION_ATTRIBUTE_PREFIX + "ES256").orElseThrow());
        val accessToken = accessToken(callerKey);
        val request = new OAuthHttpRequest("GET", URL + "?a=b", new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        request.headers().put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DPOP_HEADER_PREFIX + accessToken);

        // refused once, to require a nonce
        interceptor.configureRequest(request, callerCtx);
        val refused = request("GET", request.url(), accessToken, request.headers().get(DPoPConstants.DPOP_HEADER));
        assertTrue(client.getCredentials(new CallContext(refused, new MockSessionStore())).isEmpty());
        assertTrue(interceptor.handleResponse(request, new OAuthHttpResponse(401, refused.getResponseHeaders(), ""), callerCtx));

        // and accepted when sent again
        interceptor.configureRequest(request, callerCtx);
        val accepted = request("GET", request.url(), accessToken, request.headers().get(DPoPConstants.DPOP_HEADER));
        val credentials = (TokenCredentials) client.getCredentials(new CallContext(accepted, new MockSessionStore())).orElseThrow();
        assertEquals(accessToken, credentials.getToken());
    }

    @Test
    void renewsTheNonceOfTheDPoPHttpInterceptorBeforeItExpires() throws Exception {
        val client = nonceClient();
        val interceptor = new DPoPHttpInterceptor();
        val callerCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        interceptor.configureRequest(new OAuthHttpRequest("POST", "https://server.example.org/token",
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER)), callerCtx);
        val callerKey = JWK.parse((String) callerCtx.sessionStore().get(callerCtx.webContext(),
            DPoPConstants.KEY_SESSION_ATTRIBUTE_PREFIX + "ES256").orElseThrow());
        val accessToken = accessToken(callerKey);
        // the application holds a nonce of the protected resource, which has passed half of its maximum age
        val oldNonce = new DPoPNonceManager(SECRET, 300).generate(Instant.now().minusSeconds(200));
        val nonceSessionAttribute = DPoPConstants.NONCE_SESSION_ATTRIBUTE_PREFIX + "https://resource.example.org";
        callerCtx.sessionStore().set(callerCtx.webContext(), nonceSessionAttribute, oldNonce);
        val request = new OAuthHttpRequest("GET", URL, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
        request.headers().put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DPOP_HEADER_PREFIX + accessToken);

        // the request is accepted and its response provides a new nonce, kept without sending the request again
        interceptor.configureRequest(request, callerCtx);
        val accepted = request("GET", URL, accessToken, request.headers().get(DPoPConstants.DPOP_HEADER));
        assertTrue(client.getCredentials(new CallContext(accepted, new MockSessionStore())).isPresent());
        val newNonce = accepted.getResponseHeaders().get(DPoPConstants.DPOP_NONCE_HEADER);
        assertNotNull(newNonce);
        assertNotEquals(oldNonce, newNonce);
        assertFalse(interceptor.handleResponse(request, new OAuthHttpResponse(200, accepted.getResponseHeaders(), ""), callerCtx));

        // the next proof carries the new nonce, which is accepted without being renewed
        interceptor.configureRequest(request, callerCtx);
        val proof = request.headers().get(DPoPConstants.DPOP_HEADER);
        assertEquals(newNonce, SignedJWT.parse(proof).getJWTClaimsSet().getStringClaim("nonce"));
        val next = request("GET", URL, accessToken, proof);
        assertTrue(client.getCredentials(new CallContext(next, new MockSessionStore())).isPresent());
        assertFalse(next.getResponseHeaders().containsKey(DPoPConstants.DPOP_NONCE_HEADER));
    }

    private DirectDPoPAuthClient nonceClient() {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        client.setCredentialsExtractor(new DPoPAuthExtractor().setNonceSecret(SECRET));
        return client;
    }
}
