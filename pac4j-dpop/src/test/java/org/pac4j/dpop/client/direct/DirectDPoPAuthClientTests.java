package org.pac4j.dpop.client.direct;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.dpop.credentials.extractor.DPoPAuthExtractor;
import org.pac4j.dpop.http.DPoPHttpInterceptor;
import org.pac4j.dpop.proof.DPoPNonceManager;
import org.pac4j.dpop.util.DPoPConstants;
import org.pac4j.http.client.direct.DirectBearerAuthClient;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsConstants;
import org.pac4j.test.util.TestsHelper;

import java.time.Instant;
import java.util.Date;
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
        val accessToken = accessToken(key, null);
        val context = request("GET", URL, accessToken, proof(key, "GET", URL, accessToken));
        val ctx = new CallContext(context, new MockSessionStore());

        val credentials = (TokenCredentials) client.getCredentials(ctx).orElseThrow();
        assertEquals(accessToken, credentials.getToken());
        val authnCredentials = client.validateCredentials(ctx, credentials).orElseThrow();
        val profile = client.getUserProfile(ctx, authnCredentials).orElseThrow();

        assertEquals(accessToken, profile.getId());
        assertEquals("", context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void bearerRejectsBoundTokenRegardlessOfProofHeader(final boolean sendProof) throws Exception {
        val authenticator = issuerAuthenticator();
        val dpop = new DirectDPoPAuthClient(authenticator);
        val bearer = new DirectBearerAuthClient(authenticator);
        val token = accessToken(key, null);
        val context = request("GET", URL, null, sendProof ? proof(key, "GET", URL, token) : null);
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + token);
        assertUnauthorized(context, new Config(dpop, bearer), dpop.getName() + "," + bearer.getName());
        assertEquals("Bearer realm=\"authentication required\", error=\"invalid_token\"",
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    private void assertUnauthorized(final MockWebContext context, final Config config, final String clientNames) {
        config.setWebContextFactory(p -> context);
        config.setSessionStoreFactory(p -> new MockSessionStore());
        config.setHttpActionAdapter((action, ctx) -> action.getCode());
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        val status = new DefaultSecurityLogic().perform(config, (ctx, store, profiles) -> fail("Unexpected access"),
            clientNames, null, null, null);
        assertEquals(401, status);
    }

    @Test
    void bearerRejectsBoundJwtWhenCustomValidatorOmitsTokenClaims() throws Exception {
        val validator = issuerAuthenticator();
        val client = new DirectBearerAuthClient((Authenticator) (ctx, credentials) -> {
            val identity = validator.validateToken(((TokenCredentials) credentials).getToken());
            assertEquals("user", identity.getId());
            val profile = new CommonProfile();
            profile.setId(identity.getId());
            credentials.setUserProfile(profile);
            return Optional.of(credentials);
        });
        val context = request("GET", URL, null, null);
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken(key, null));
        val ctx = new CallContext(context, new MockSessionStore());
        assertTrue(client.validateCredentials(ctx, client.getCredentials(ctx).orElseThrow()).isEmpty());
    }

    @Test
    void bearerChecksConfirmationInsideEncryptedAccessToken() throws Exception {
        val encryption = new SecretEncryptionConfiguration(MAC_SECRET);
        val authenticator = issuerAuthenticator();
        authenticator.setEncryptionConfiguration(encryption);
        val client = new DirectBearerAuthClient(authenticator);
        val token = encryption.encrypt(SignedJWT.parse(accessToken(key, null)));
        val context = request("GET", URL, null, null);
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + token);
        val ctx = new CallContext(context, new MockSessionStore());

        assertEquals("user", authenticator.validateToken(token).getId());
        assertTrue(client.validateCredentials(ctx, client.getCredentials(ctx).orElseThrow()).isEmpty());
    }

    @Test
    void acceptsSignedTokensThroughTheirRespectiveSchemes() throws Exception {
        val authenticator = issuerAuthenticator();
        val bearer = new DirectBearerAuthClient(authenticator);
        val bearerContext = request("GET", URL, null, null);
        bearerContext.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken(null, null));
        val bearerCtx = new CallContext(bearerContext, new MockSessionStore());
        val bearerCredentials = bearer.validateCredentials(bearerCtx, bearer.getCredentials(bearerCtx).orElseThrow()).orElseThrow();
        assertEquals("user", bearer.getUserProfile(bearerCtx, bearerCredentials).orElseThrow().getId());

        val dpop = new DirectDPoPAuthClient(authenticator);
        val token = accessToken(key, null);
        val dpopCtx = new CallContext(request("GET", URL, token, proof(key, "GET", URL, token)), new MockSessionStore());
        val dpopCredentials = dpop.validateCredentials(dpopCtx, dpop.getCredentials(dpopCtx).orElseThrow()).orElseThrow();
        assertEquals("user", dpop.getUserProfile(dpopCtx, dpopCredentials).orElseThrow().getId());
    }

    @Test
    void testProfileCreation() throws Exception {
        val client = new DirectDPoPAuthClient((ProfileCreator) (ctx, credentials) -> {
            UserProfile profile = new CommonProfile();
            profile.setId(KEY);
            return Optional.of(profile);
        });
        val accessToken = accessToken(key, null);
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
        val accessToken = accessToken(key, null);
        val context = request("POST", URL, accessToken, proof(key, "GET", URL, accessToken));

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_dpop_proof\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheErrorOfAnAccessTokenBoundToAnotherKey() throws Exception {
        val client = new DirectDPoPAuthClient(TOKEN_AUTHENTICATOR);
        val stolenAccessToken = accessToken(generateKey(), null);
        val context = request("GET", URL, stolenAccessToken, proof(key, "GET", URL, stolenAccessToken));

        assertTrue(client.getCredentials(new CallContext(context, new MockSessionStore())).isEmpty());

        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_token\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesAnExpiredSignedTokenWithAFreshProof() throws Exception {
        val client = new DirectDPoPAuthClient(issuerAuthenticator());
        val token = accessToken(key, Date.from(Instant.now().minusSeconds(3600)));
        val context = request("GET", URL, token, proof(key, "GET", URL, token));

        assertUnauthorized(context, new Config(client), client.getName());
        assertEquals("DPoP realm=\"authentication required\", error=\"invalid_token\", " + ALGS,
            context.getResponseHeaders().get(HttpConstants.AUTHENTICATE_HEADER));
    }

    @Test
    void challengesWithTheErrorOfAnAccessTokenRefusedByTheAuthenticator() throws Exception {
        val client = new DirectDPoPAuthClient((Authenticator) (ctx, credentials) -> Optional.empty());
        val accessToken = accessToken(key, null);
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
        val accessToken = accessToken(key, null);
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
            Pac4jConstants.DPOP_KEY_SESSION_ATTRIBUTE_PREFIX + "ES256").orElseThrow());
        val accessToken = accessToken(callerKey, null);
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
            Pac4jConstants.DPOP_KEY_SESSION_ATTRIBUTE_PREFIX + "ES256").orElseThrow());
        val accessToken = accessToken(callerKey, null);
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
