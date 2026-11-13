package org.pac4j.dpop.http;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DPoPUtils;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;
import org.pac4j.dpop.util.DPoPConstants;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DPoPHttpInterceptor}.
 */
class DPoPHttpInterceptorTests {

    private static final String TOKEN_URL = "https://server.example/token";

    private static final String PROFILE_URL = "https://server.example/profile";

    private static final String ACCESS_TOKEN = "access-token";

    private static final String NONCE_ERROR_BODY = "{\"error\":\"use_dpop_nonce\",\"error_description\":\"nonce required\"}";

    private final DPoPHttpInterceptor interceptor = new DPoPHttpInterceptor();

    private final CallContext ctx = new CallContext(MockWebContext.create(), new MockSessionStore());

    @Test
    void addsAProofToAnOAuthTokenRequest() throws Exception {
        val request = oauthRequest("POST", TOKEN_URL + "?a=b#fragment", "Basic credentials");

        interceptor.configureRequest(request, ctx);

        assertEquals("Basic credentials", request.headers().get("Authorization"));
        val proof = SignedJWT.parse(request.headers().get("DPoP"));
        assertValidProof(proof, "POST", TOKEN_URL, null);
        assertEquals(JWSAlgorithm.ES256, proof.getHeader().getAlgorithm());
        assertEquals(KeyType.EC, proof.getHeader().getJWK().getKeyType());
    }

    @Test
    void addsAProofBoundToTheAccessTokenAndTheDPoPSchemeToAnOAuthProfileRequest() throws Exception {
        val request = oauthRequest("GET", PROFILE_URL, "Bearer " + ACCESS_TOKEN);

        interceptor.configureRequest(request, ctx);

        assertEquals("DPoP " + ACCESS_TOKEN, request.headers().get("Authorization"));
        assertValidProof(SignedJWT.parse(request.headers().get("DPoP")), "GET", PROFILE_URL, ACCESS_TOKEN);
    }

    @Test
    void addsAProofToANimbusTokenRequest() throws Exception {
        val request = new HTTPRequest(HTTPRequest.Method.POST, new URI(TOKEN_URL));
        request.setAuthorization("Basic credentials");

        interceptor.configureRequest(request, ctx);

        assertEquals("Basic credentials", request.getAuthorization());
        assertValidProof(request.getDPoP(), "POST", TOKEN_URL, null);
    }

    @Test
    void addsAProofBoundToTheAccessTokenToANimbusUserInfoRequest() throws Exception {
        val request = new HTTPRequest(HTTPRequest.Method.GET, new URI(PROFILE_URL));
        request.setAuthorization(new DPoPAccessToken(ACCESS_TOKEN).toAuthorizationHeader());

        interceptor.configureRequest(request, ctx);

        assertEquals("DPoP " + ACCESS_TOKEN, request.getAuthorization());
        assertValidProof(request.getDPoP(), "GET", PROFILE_URL, ACCESS_TOKEN);
    }

    @Test
    void savesTheGeneratedKeyPairInTheSessionAndReusesIt() throws Exception {
        assertTrue(ctx.sessionStore().get(ctx.webContext(), DPoPConstants.KEY_SESSION_ATTRIBUTE).isEmpty());

        val first = configuredProof(ctx);
        val saved = JWK.parse((String) ctx.sessionStore()
            .get(ctx.webContext(), DPoPConstants.KEY_SESSION_ATTRIBUTE).orElseThrow());
        val second = configuredProof(ctx);

        assertTrue(saved.isPrivate());
        assertEquals(saved.toPublicJWK(), first.getHeader().getJWK());
        assertEquals(saved.toPublicJWK(), second.getHeader().getJWK());
        assertNotEquals(first.getJWTClaimsSet().getJWTID(), second.getJWTClaimsSet().getJWTID());
    }

    @Test
    void usesTheKeyPairAlreadyInTheSession() throws Exception {
        val key = new DPoPHttpInterceptor().generateKey();
        ctx.sessionStore().set(ctx.webContext(), DPoPConstants.KEY_SESSION_ATTRIBUTE, key.toJSONString());

        assertEquals(key.toPublicJWK(), configuredProof(ctx).getHeader().getJWK());
    }

    @Test
    void usesOneKeyPairPerSession() throws Exception {
        val otherCtx = new CallContext(MockWebContext.create(), new MockSessionStore());

        assertNotEquals(configuredProof(ctx).getHeader().getJWK(), configuredProof(otherCtx).getHeader().getJWK());
    }

    @Test
    void requiresAWebSession() {
        assertThrows(TechnicalException.class, () -> configuredProof(null));
        assertThrows(TechnicalException.class, () -> configuredProof(new CallContext(null, null)));
        assertThrows(TechnicalException.class, () -> configuredProof(new CallContext(MockWebContext.create(), null)));
        assertThrows(TechnicalException.class, () -> configuredProof(new CallContext(null, new MockSessionStore())));
    }

    @Test
    void signsWithTheDefinedAlgorithm() throws Exception {
        interceptor.setJwsAlgorithm(JWSAlgorithm.PS256);

        val proof = configuredProof(ctx);

        assertValidProof(proof, "POST", TOKEN_URL, null);
        assertEquals(JWSAlgorithm.PS256, proof.getHeader().getAlgorithm());
        assertInstanceOf(RSAKey.class, proof.getHeader().getJWK());

        interceptor.setJwsAlgorithm(JWSAlgorithm.ES384);
        val otherCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        assertInstanceOf(ECKey.class, configuredProof(otherCtx).getHeader().getJWK());
    }

    @Test
    void rejectsAnUnsupportedAlgorithm() {
        assertSame(interceptor, interceptor.setJwsAlgorithm(JWSAlgorithm.HS256));

        assertThrows(TechnicalException.class, () -> configuredProof(ctx));
    }

    @Test
    void rejectsAnUnsupportedRequestType() {
        assertThrows(TechnicalException.class, () -> interceptor.configureRequest("request", ctx));
        assertThrows(TechnicalException.class, () -> interceptor.configureRequest(null, ctx));
    }

    @Test
    void sendsAgainWithTheNonceRequiredByTheAuthorizationServer() throws Exception {
        val request = oauthRequest("POST", TOKEN_URL, null);
        interceptor.configureRequest(request, ctx);
        assertNull(SignedJWT.parse(request.headers().get("DPoP")).getJWTClaimsSet().getClaim("nonce"));

        assertTrue(interceptor.handleResponse(request, oauthResponse(400, "nonce-1", null, NONCE_ERROR_BODY), ctx));

        interceptor.configureRequest(request, ctx);
        assertValidProof(SignedJWT.parse(request.headers().get("DPoP")), "POST", TOKEN_URL, null, "nonce-1");
    }

    @Test
    void sendsAgainWithTheNonceRequiredByTheResourceServer() throws Exception {
        val request = oauthRequest("GET", PROFILE_URL + "?a=b", "Bearer " + ACCESS_TOKEN);
        interceptor.configureRequest(request, ctx);

        assertTrue(interceptor.handleResponse(request,
            oauthResponse(401, "nonce-1", "DPoP error=\"use_dpop_nonce\", error_description=\"nonce required\"", null), ctx));

        interceptor.configureRequest(request, ctx);
        assertEquals("DPoP " + ACCESS_TOKEN, request.headers().get("Authorization"));
        assertValidProof(SignedJWT.parse(request.headers().get("DPoP")), "GET", PROFILE_URL, ACCESS_TOKEN, "nonce-1");
    }

    @Test
    void sendsAgainANimbusRequestWithTheRequiredNonce() throws Exception {
        val request = new HTTPRequest(HTTPRequest.Method.POST, new URI(TOKEN_URL));
        interceptor.configureRequest(request, ctx);
        val response = new HTTPResponse(400);
        response.setDPoPNonce(new Nonce("nonce-1"));
        response.setBody(NONCE_ERROR_BODY);

        assertTrue(interceptor.handleResponse(request, response, ctx));

        interceptor.configureRequest(request, ctx);
        assertValidProof(request.getDPoP(), "POST", TOKEN_URL, null, "nonce-1");
    }

    @Test
    void keepsTheLastNonceProvidedWithoutSendingAgain() throws Exception {
        val request = oauthRequest("POST", TOKEN_URL, null);

        assertFalse(interceptor.handleResponse(request, oauthResponse(200, "nonce-1", null, "{}"), ctx));
        assertEquals("nonce-1", configuredProof(ctx).getJWTClaimsSet().getStringClaim("nonce"));

        // another error, even naming the nonce error elsewhere, does not require to send again
        assertFalse(interceptor.handleResponse(request, oauthResponse(400, "nonce-2", null,
            "{\"error\":\"invalid_grant\",\"error_description\":\"not use_dpop_nonce\"}"), ctx));
        assertEquals("nonce-2", configuredProof(ctx).getJWTClaimsSet().getStringClaim("nonce"));

        assertFalse(interceptor.handleResponse(request, oauthResponse(200, null, null, "{}"), ctx));
        assertEquals("nonce-2", configuredProof(ctx).getJWTClaimsSet().getStringClaim("nonce"));
    }

    @Test
    void doesNotSendAgainWithoutNonce() {
        val request = oauthRequest("POST", TOKEN_URL, null);

        assertFalse(interceptor.handleResponse(request, oauthResponse(400, null, null, NONCE_ERROR_BODY), ctx));
        assertFalse(interceptor.handleResponse(request, oauthResponse(400, " ", null, NONCE_ERROR_BODY), ctx));
        assertFalse(interceptor.handleResponse(request, oauthResponse(400, "nonce-1", null, "not json use_dpop_nonce"), ctx));
        assertFalse(interceptor.handleResponse(request, oauthResponse(400, "nonce-1", null, null), ctx));
    }

    @Test
    void keepsOneNoncePerServer() throws Exception {
        val request = oauthRequest("POST", TOKEN_URL, null);
        interceptor.handleResponse(request, oauthResponse(400, "nonce-1", null, NONCE_ERROR_BODY), ctx);

        val other = oauthRequest("GET", "https://resource.example/profile", "Bearer " + ACCESS_TOKEN);
        interceptor.configureRequest(other, ctx);
        assertNull(SignedJWT.parse(other.headers().get("DPoP")).getJWTClaimsSet().getClaim("nonce"));

        val sameServer = oauthRequest("GET", PROFILE_URL, "Bearer " + ACCESS_TOKEN);
        interceptor.configureRequest(sameServer, ctx);
        assertEquals("nonce-1", SignedJWT.parse(sameServer.headers().get("DPoP")).getJWTClaimsSet().getStringClaim("nonce"));

        val otherSession = new CallContext(MockWebContext.create(), new MockSessionStore());
        assertNull(configuredProof(otherSession).getJWTClaimsSet().getClaim("nonce"));
    }

    @Test
    void rejectsUnsupportedResponseTypesAndAMissingSession() {
        val request = oauthRequest("POST", TOKEN_URL, null);
        val response = oauthResponse(400, "nonce-1", null, NONCE_ERROR_BODY);

        assertThrows(TechnicalException.class, () -> interceptor.handleResponse(request, "response", ctx));
        assertThrows(TechnicalException.class, () -> interceptor.handleResponse(request, new HTTPResponse(400), ctx));
        assertThrows(TechnicalException.class, () -> interceptor.handleResponse(null, response, ctx));
        assertThrows(TechnicalException.class, () -> interceptor.handleResponse(request, response, new CallContext(null, null)));
    }

    private OAuthHttpResponse oauthResponse(final int code, final String nonce, final String wwwAuthenticate, final String body) {
        val headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        if (nonce != null) {
            headers.put("dpop-nonce", nonce);
        }
        if (wwwAuthenticate != null) {
            headers.put("www-authenticate", wwwAuthenticate);
        }
        return new OAuthHttpResponse(code, headers, body);
    }

    private SignedJWT configuredProof(final CallContext context) throws Exception {
        val request = oauthRequest("POST", TOKEN_URL, null);
        interceptor.configureRequest(request, context);
        return SignedJWT.parse(request.headers().get("DPoP"));
    }

    private OAuthHttpRequest oauthRequest(final String verb, final String url, final String authorization) {
        val headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        if (authorization != null) {
            headers.put("authorization", authorization);
        }
        return new OAuthHttpRequest(verb, url, headers);
    }

    /**
     * Check the proof against RFC 9449, section 4.2.
     */
    private void assertValidProof(final SignedJWT proof, final String method, final String url, final String accessToken)
        throws Exception {
        assertValidProof(proof, method, url, accessToken, null);
    }

    private void assertValidProof(final SignedJWT proof, final String method, final String url, final String accessToken,
                                  final String nonce) throws Exception {
        val header = proof.getHeader();
        assertEquals(new JOSEObjectType("dpop+jwt"), header.getType());
        val jwk = header.getJWK();
        assertFalse(jwk.isPrivate());
        val publicKey = jwk instanceof ECKey ecKey ? ecKey.toPublicKey() : ((RSAKey) jwk).toPublicKey();
        assertTrue(proof.verify(new DefaultJWSVerifierFactory().createJWSVerifier(header, publicKey)));

        val claims = proof.getJWTClaimsSet();
        assertNotNull(claims.getJWTID());
        assertEquals(method, claims.getStringClaim("htm"));
        assertEquals(url, claims.getStringClaim("htu"));
        assertTrue(Math.abs(new Date().getTime() - claims.getIssueTime().getTime()) < 60_000);
        val expectedClaims = 4 + (accessToken == null ? 0 : 1) + (nonce == null ? 0 : 1);
        assertEquals(nonce, claims.getStringClaim("nonce"));
        assertEquals(expectedClaims, claims.getClaims().size(), claims.toString());
        if (accessToken != null) {
            assertEquals(DPoPUtils.computeSHA256(new DPoPAccessToken(accessToken)).toString(), claims.getStringClaim("ath"));
        }
        assertEquals(Map.of(), header.getCustomParams());
    }
}
