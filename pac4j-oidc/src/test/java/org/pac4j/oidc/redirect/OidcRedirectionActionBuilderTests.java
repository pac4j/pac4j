package org.pac4j.oidc.redirect;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import fi.iki.elonen.NanoHTTPD;
import lombok.val;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.metadata.OidcFederationOpMetadataResolver;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsConstants;
import org.pac4j.test.web.ServerResponse;
import org.pac4j.test.web.WebServer;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link OidcRedirectionActionBuilder}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcRedirectionActionBuilderTests implements TestsConstants {
    private static final String AUTHORIZATION_ENDPOINT = "http://localhost:8080/auth";
    private static final String OP_ISSUER = "http://localhost:8080";
    private static final String TEST_CLIENT_ID = "testClient";
    private static final String TEST_CLIENT_SECRET = "secret";
    private static final String TEST_SCOPE = "openid,profile,email";
    private static final String TEST_LOGIN_HINT = "user@example.org";
    private static final String FEDERATION_TARGET_OP = "https://federation.example.org";
    private OidcConfiguration configuration;
    private OidcOpMetadataResolver metadataResolver;
    private OIDCProviderMetadata providerMetadata;
    private OidcClient client;
    private MockWebContext webContext;
    private MockSessionStore sessionStore;
    private CallContext context;

    @BeforeEach
    public void beforeEach() throws Exception {
        providerMetadata = mock(OIDCProviderMetadata.class);
        when(providerMetadata.getAuthorizationEndpointURI()).thenReturn(new URI(AUTHORIZATION_ENDPOINT));
        when(providerMetadata.getIssuer()).thenReturn(new Issuer(OP_ISSUER));

        metadataResolver = mock(OidcOpMetadataResolver.class);
        when(metadataResolver.load()).thenReturn(providerMetadata);

        configuration = new OidcConfiguration();
        configuration.setLoginHint(TEST_LOGIN_HINT);
        configuration.setClientId(TEST_CLIENT_ID);
        configuration.setSecret(TEST_CLIENT_SECRET);
        configuration.setScope(TEST_SCOPE);
        configuration.setOpMetadataResolver(metadataResolver);

        client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);

        webContext = MockWebContext.create();
        sessionStore = new MockSessionStore();
        context = new CallContext(webContext, sessionStore, ProfileManagerFactory.DEFAULT);
    }

    private OidcRedirectionActionBuilder newBuilder() {
        return new OidcRedirectionActionBuilder(client);
    }

    private FoundAction getFoundAction() throws Exception {
        return (FoundAction) newBuilder().getRedirectionAction(context).orElseThrow();
    }

    private void enablePar(URI parEndpointUri) {
        configuration.setPushedAuthorizationRequest(true);
        when(providerMetadata.getPushedAuthorizationRequestEndpointURI()).thenReturn(parEndpointUri);
        when(metadataResolver.getClientAuthenticationPAREndpoint())
            .thenReturn(new ClientSecretBasic(new ClientID(TEST_CLIENT_ID), new Secret(TEST_CLIENT_SECRET)));
    }

    private static JwksProperties buildRpJwks(String kid) throws Exception {
        val jwksPath = Files.createTempDirectory("oidc-redirection-action-builder-tests").resolve(kid + ".jwks");
        Files.deleteIfExists(jwksPath);
        val rpJwks = new JwksProperties();
        rpJwks.setJwksPath(jwksPath.toString());
        rpJwks.setKid(kid);
        return rpJwks;
    }

    private static void assertAuthorizationEndpointUrl(URIBuilder url) {
        assertEquals("http", url.getScheme());
        assertEquals("localhost", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("/auth", url.getPath());
    }

    private static void assertComputedCallbackUrl(String callbackUrl) throws Exception {
        val computedCallback = new URIBuilder(callbackUrl);
        val configuredCallback = new URIBuilder(CALLBACK_URL);
        assertEquals(configuredCallback.getScheme(), computedCallback.getScheme());
        assertEquals(configuredCallback.getHost(), computedCallback.getHost());
        assertEquals(configuredCallback.getPath(), computedCallback.getPath());
        assertEquals("OidcClient", computedCallback.getFirstQueryParam("client_name").getValue());
    }

    private static void assertStandardSignedRequestClaims(SignedJWT signedRequest) throws Exception {
        val claims = signedRequest.getJWTClaimsSet();
        assertEquals(TEST_CLIENT_ID, claims.getIssuer());
        assertEquals(List.of(OP_ISSUER), claims.getAudience());
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertTrue(claims.getExpirationTime().after(claims.getIssueTime()));
        assertEquals(60_000L, claims.getExpirationTime().getTime() - claims.getIssueTime().getTime());
        assertNotNull(claims.getJWTID());
    }

    @Test
    public void testOidcRedirectionScopes() throws Exception {
        val action = getFoundAction();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        assertEquals(TEST_LOGIN_HINT, url.getFirstQueryParam(OidcConfiguration.LOGIN_HINT).getValue());
        assertEquals("openid profile email", url.getFirstQueryParam(OidcConfiguration.SCOPE).getValue());
        assertEquals("code", url.getFirstQueryParam(OidcConfiguration.RESPONSE_TYPE).getValue());
        assertComputedCallbackUrl(url.getFirstQueryParam(OidcConfiguration.REDIRECT_URI).getValue());
    }
    @Test
    public void testOidcRedirectionIncludesMaxAgeWhenDefined() throws Exception {
        configuration.setMaxAge(3600);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        assertEquals("3600", url.getFirstQueryParam(OidcConfiguration.MAX_AGE).getValue());
        assertNull(url.getFirstQueryParam(OidcConfiguration.PROMPT));
    }

    @Test
    public void testOidcRedirectionForcesAuthenticationWhenRequested() throws Exception {
        configuration.setMaxAge(3600);
        webContext.setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN, true);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        assertEquals("login", url.getFirstQueryParam(OidcConfiguration.PROMPT).getValue());
        assertEquals("0", url.getFirstQueryParam(OidcConfiguration.MAX_AGE).getValue());
    }

    @Test
    public void testOidcRedirectionUsesPassivePromptWhenRequested() throws Exception {
        webContext.setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE, true);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        assertEquals("none", url.getFirstQueryParam(OidcConfiguration.PROMPT).getValue());
    }

    @Test
    public void testOidcRedirectionAddsNonceWhenEnabled() throws Exception {
        configuration.setUseNonce(true);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        val nonce = url.getFirstQueryParam(OidcConfiguration.NONCE).getValue();
        assertNotNull(nonce);
        assertEquals(nonce, sessionStore.get(webContext, client.getNonceSessionAttributeName()).orElse(null));
    }

    @Test
    public void testOidcRedirectionAddsPkceChallengeWhenConfigured() throws Exception {
        configuration.setPkceMethod(CodeChallengeMethod.S256);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        assertNotNull(url.getFirstQueryParam(OidcConfiguration.CODE_CHALLENGE));
        assertEquals(CodeChallengeMethod.S256.getValue(),
            url.getFirstQueryParam(OidcConfiguration.CODE_CHALLENGE_METHOD).getValue());
        assertTrue(sessionStore.get(webContext, client.getCodeVerifierSessionAttributeName()).isPresent());
    }

    @Test
    public void testOidcRedirectionUsesSignedRequestObjectWhenRequested() throws Exception {
        configuration.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);
        configuration.setRpJwks(buildRpJwks("request-object-kid"));
        when(providerMetadata.getRequestObjectJWSAlgs()).thenReturn(List.of(JWSAlgorithm.RS256));

        val action = getFoundAction();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        val requestParam = url.getFirstQueryParam("request");
        assertNotNull(requestParam);
        assertEquals(TEST_CLIENT_ID, url.getFirstQueryParam(OidcConfiguration.CLIENT_ID).getValue());
        assertEquals("openid profile email", url.getFirstQueryParam(OidcConfiguration.SCOPE).getValue());
        assertNull(url.getFirstQueryParam(OidcConfiguration.REDIRECT_URI));

        val signedRequest = SignedJWT.parse(requestParam.getValue());
        assertEquals(JWSAlgorithm.RS256, signedRequest.getHeader().getAlgorithm());
        assertEquals("request-object-kid", signedRequest.getHeader().getKeyID());
        assertStandardSignedRequestClaims(signedRequest);
        assertEquals("openid profile email", signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.SCOPE));
        assertComputedCallbackUrl(signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.REDIRECT_URI));
        assertEquals(TEST_LOGIN_HINT, signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.LOGIN_HINT));
    }

    @Test
    public void testOidcRedirectionUsesSignedRequestObjectInFederationMode() throws Exception {
        configuration.getFederation().setTargetOp(FEDERATION_TARGET_OP);
        configuration.setRpJwks(buildRpJwks("federation-kid"));
        when(providerMetadata.getRequestObjectJWSAlgs()).thenReturn(List.of(JWSAlgorithm.RS512, JWSAlgorithm.RS256));

        val action = getFoundAction();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        val requestParam = url.getFirstQueryParam("request");
        assertNotNull(requestParam);
        assertEquals(TEST_CLIENT_ID, url.getFirstQueryParam(OidcConfiguration.CLIENT_ID).getValue());

        val signedRequest = SignedJWT.parse(requestParam.getValue());
        assertEquals(JWSAlgorithm.RS512, signedRequest.getHeader().getAlgorithm());
        assertEquals("federation-kid", signedRequest.getHeader().getKeyID());
        assertStandardSignedRequestClaims(signedRequest);
    }

    @Test
    public void testOidcRedirectionAddsTrustChainClaimWhenEnabled() throws Exception {
        configuration.getFederation().setTargetOp(FEDERATION_TARGET_OP);
        configuration.getFederation().setSendTrustChain(true);
        configuration.setRpJwks(buildRpJwks("federation-trust-chain-kid"));
        when(providerMetadata.getRequestObjectJWSAlgs()).thenReturn(List.of(JWSAlgorithm.RS256));

        val federationResolver = mock(OidcFederationOpMetadataResolver.class);
        when(federationResolver.load()).thenReturn(providerMetadata);
        val trustChain = List.of("entity-statement-1", "entity-statement-2");
        when(federationResolver.getTrustChain()).thenReturn(trustChain);
        configuration.setOpMetadataResolver(federationResolver);

        val action = getFoundAction();
        val url = new URIBuilder(action.getLocation());
        val requestParam = url.getFirstQueryParam("request");
        assertNotNull(requestParam);

        val signedRequest = SignedJWT.parse(requestParam.getValue());
        assertEquals(trustChain, signedRequest.getJWTClaimsSet().getClaim("trust_chain"));
    }

    @Test
    public void testOidcRedirectionUsesPushedAuthorizationRequest() throws Exception {
        val requestUri = "urn:ietf:params:oauth:request_uri:example";
        val parSuccessResponse = "{\"request_uri\":\"" + requestUri + "\",\"expires_in\":90}";

        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(NanoHTTPD.Response.Status.CREATED,
                "application/json", parSuccessResponse));
        webServer.start();
        try {
            enablePar(new URI("http://localhost:" + webServer.getListeningPort() + "/par?r=ok"));
            val action = getFoundAction();
            assertEquals(302, action.getCode());

            val url = new URIBuilder(action.getLocation());
            assertAuthorizationEndpointUrl(url);
            assertEquals(TEST_CLIENT_ID, url.getFirstQueryParam(OidcConfiguration.CLIENT_ID).getValue());
            assertEquals(requestUri, url.getFirstQueryParam("request_uri").getValue());
            assertNull(url.getFirstQueryParam(OidcConfiguration.SCOPE));
            assertNull(url.getFirstQueryParam(OidcConfiguration.RESPONSE_TYPE));
            assertNull(url.getFirstQueryParam(OidcConfiguration.REDIRECT_URI));
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testOidcRedirectionFailsWhenParEndpointIsUndefined() throws Exception {
        enablePar(null);
        val exception = assertThrows(OidcException.class, this::getFoundAction);
        assertEquals("Pushed authorization request URL is undefined", exception.getMessage());
    }

    @Test
    public void testOidcRedirectionFailsWhenParReturnsError() throws Exception {
        val parErrorResponse = """
            {
              "error": "invalid_request",
              "error_description": "PAR request rejected"
            }
            """;

        val webServer = new WebServer(0)
            .defineResponse("ko", new ServerResponse(NanoHTTPD.Response.Status.BAD_REQUEST,
                "application/json", parErrorResponse));
        webServer.start();
        try {
            enablePar(new URI("http://localhost:" + webServer.getListeningPort() + "/par?r=ko"));
            val exception = assertThrows(OidcException.class, this::getFoundAction);
            assertEquals("PAR request rejected", exception.getMessage());
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testOidcRedirectionFailsWhenSigningRequestedWithoutRpJwks() throws Exception {
        configuration.setRequestObjectSigningAlgorithm(JWSAlgorithm.RS256);
        when(providerMetadata.getRequestObjectJWSAlgs()).thenReturn(List.of(JWSAlgorithm.RS256));

        val exception = assertThrows(TechnicalException.class, this::getFoundAction);
        assertEquals("config.rpJwks must be defined to sign request objects", exception.getMessage());
    }
}
