package org.pac4j.oidc.redirect;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;

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

    private static OidcClient getClient(
        JWSAlgorithm requestObjectSigningAlgorithm,
        boolean federation,
        List<JWSAlgorithm> opRequestObjectAlgs,
        JwksProperties rpJwks
    ) throws Exception {
        val providerMetadata = mock(OIDCProviderMetadata.class);
        when(providerMetadata.getAuthorizationEndpointURI()).thenReturn(new URI("http://localhost:8080/auth"));
        when(providerMetadata.getRequestObjectJWSAlgs()).thenReturn(opRequestObjectAlgs);
        when(providerMetadata.getIssuer()).thenReturn(new Issuer("http://localhost:8080"));

        val configuration = new OidcConfiguration();
        configuration.setLoginHint("user@example.org");
        configuration.setClientId("testClient");
        configuration.setSecret("secret");
        configuration.setScope("openid,profile,email");

        if (requestObjectSigningAlgorithm != null) {
            configuration.setRequestObjectSigningAlgorithm(requestObjectSigningAlgorithm);
        }
        if (rpJwks != null) {
            configuration.setRpJwks(rpJwks);
        }
        if (federation) {
            configuration.getFederation().setTargetIssuer("https://federation.example.org");
        }

        val metadataResolver = mock(OidcOpMetadataResolver.class);
        when(metadataResolver.load()).thenReturn(providerMetadata);
        configuration.setOpMetadataResolver(metadataResolver);

        val client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    private static CallContext getContext() {
        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        return new CallContext(webContext, sessionStore, ProfileManagerFactory.DEFAULT);
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
        assertEquals("testClient", claims.getIssuer());
        assertEquals(List.of("http://localhost:8080"), claims.getAudience());
        assertNotNull(claims.getIssueTime());
        assertNotNull(claims.getExpirationTime());
        assertTrue(claims.getExpirationTime().after(claims.getIssueTime()));
        assertEquals(60_000L, claims.getExpirationTime().getTime() - claims.getIssueTime().getTime());
        assertNotNull(claims.getJWTID());
    }

    @Test
    public void testOidcRedirectionScopes() throws Exception {
        val builder = new OidcRedirectionActionBuilder(getClient(null, false, null, null));
        val action = (FoundAction) builder.getRedirectionAction(getContext()).orElseThrow();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        assertEquals("user@example.org", url.getFirstQueryParam(OidcConfiguration.LOGIN_HINT).getValue());
        assertEquals("openid profile email", url.getFirstQueryParam(OidcConfiguration.SCOPE).getValue());
        assertEquals("code", url.getFirstQueryParam(OidcConfiguration.RESPONSE_TYPE).getValue());
        assertComputedCallbackUrl(url.getFirstQueryParam(OidcConfiguration.REDIRECT_URI).getValue());
    }

    @Test
    public void testOidcRedirectionUsesSignedRequestObjectWhenRequested() throws Exception {
        val builder = new OidcRedirectionActionBuilder(
            getClient(JWSAlgorithm.RS256, false, List.of(JWSAlgorithm.RS256), buildRpJwks("request-object-kid")));
        val action = (FoundAction) builder.getRedirectionAction(getContext()).orElseThrow();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        val requestParam = url.getFirstQueryParam("request");
        assertNotNull(requestParam);
        assertEquals("testClient", url.getFirstQueryParam(OidcConfiguration.CLIENT_ID).getValue());
        assertEquals("openid profile email", url.getFirstQueryParam(OidcConfiguration.SCOPE).getValue());
        assertNull(url.getFirstQueryParam(OidcConfiguration.REDIRECT_URI));

        val signedRequest = SignedJWT.parse(requestParam.getValue());
        assertEquals(JWSAlgorithm.RS256, signedRequest.getHeader().getAlgorithm());
        assertEquals("request-object-kid", signedRequest.getHeader().getKeyID());
        assertStandardSignedRequestClaims(signedRequest);
        assertEquals("openid profile email", signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.SCOPE));
        assertComputedCallbackUrl(signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.REDIRECT_URI));
        assertEquals("user@example.org", signedRequest.getJWTClaimsSet().getStringClaim(OidcConfiguration.LOGIN_HINT));
    }

    @Test
    public void testOidcRedirectionUsesSignedRequestObjectInFederationMode() throws Exception {
        val builder = new OidcRedirectionActionBuilder(
            getClient(null, true, List.of(JWSAlgorithm.RS512, JWSAlgorithm.RS256), buildRpJwks("federation-kid")));
        val action = (FoundAction) builder.getRedirectionAction(getContext()).orElseThrow();
        assertEquals(302, action.getCode());

        val url = new URIBuilder(action.getLocation());
        assertAuthorizationEndpointUrl(url);
        val requestParam = url.getFirstQueryParam("request");
        assertNotNull(requestParam);
        assertEquals("testClient", url.getFirstQueryParam(OidcConfiguration.CLIENT_ID).getValue());

        val signedRequest = SignedJWT.parse(requestParam.getValue());
        assertEquals(JWSAlgorithm.RS512, signedRequest.getHeader().getAlgorithm());
        assertEquals("federation-kid", signedRequest.getHeader().getKeyID());
        assertStandardSignedRequestClaims(signedRequest);
    }

    @Test
    public void testOidcRedirectionFailsWhenSigningRequestedWithoutRpJwks() throws Exception {
        val builder = new OidcRedirectionActionBuilder(
            getClient(JWSAlgorithm.RS256, false, List.of(JWSAlgorithm.RS256), null));
        val exception = assertThrows(TechnicalException.class, () -> builder.getRedirectionAction(getContext()));
        assertEquals("config.rpJwks must be defined to sign request objects", exception.getMessage());
    }
}
