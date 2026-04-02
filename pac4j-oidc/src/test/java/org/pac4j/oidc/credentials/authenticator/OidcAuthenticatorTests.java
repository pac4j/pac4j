package org.pac4j.oidc.credentials.authenticator;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.core.context.CallContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcTokenException;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.test.context.MockWebContext;

import java.net.URI;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link OidcAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
public final class OidcAuthenticatorTests {

    private static final String CALLBACK_URL = "https://client.example.org/callback";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String OIDC_SCOPE = "openid profile";
    private static final String TOKEN_ENDPOINT = "https://op.example.org/token";

    private OidcConfiguration configuration;
    private OidcClient client;
    private CallContext context;
    private MockSessionStore sessionStore;
    private OidcOpMetadataResolver metadataResolver;

    @BeforeEach
    public void beforeEach() throws Exception {
        configuration = new OidcConfiguration();
        configuration.setClientId(CLIENT_ID);
        configuration.setSecret(CLIENT_SECRET);
        configuration.setScope(OIDC_SCOPE);

        val providerMetadata = Mockito.mock(OIDCProviderMetadata.class);
        Mockito.when(providerMetadata.getTokenEndpointURI()).thenReturn(new URI(TOKEN_ENDPOINT));
        Mockito.when(providerMetadata.getIssuer()).thenReturn(new Issuer("https://op.example.org"));

        metadataResolver = Mockito.mock(OidcOpMetadataResolver.class);
        Mockito.when(metadataResolver.load()).thenReturn(providerMetadata);
        configuration.setOpMetadataResolver(metadataResolver);

        client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);

        val webContext = MockWebContext.create();
        sessionStore = new MockSessionStore();
        context = new CallContext(webContext, sessionStore);
    }

    @Test
    public void testValidateReturnsSameCredentialsWhenNotOidcCredentials() {
        val authenticator = new OidcAuthenticator(configuration, client);
        val credentials = Mockito.mock(Credentials.class);

        val validated = authenticator.validate(context, credentials).orElseThrow();
        assertSame(credentials, validated);
    }

    @Test
    public void testValidateReturnsSameCredentialsWhenAuthorizationCodeIsMissing() {
        val authenticator = Mockito.spy(new OidcAuthenticator(configuration, client));
        val credentials = new OidcCredentials();

        val validated = authenticator.validate(context, credentials).orElseThrow();
        assertSame(credentials, validated);
        Mockito.verify(authenticator, Mockito.never()).createTokenRequest(Mockito.any());
    }

    @Test
    public void testValidateExchangesAuthorizationCodeAndStoresTokens() throws Exception {
        val tokenRequest = mockTokenRequestReturning(buildSuccessfulTokenResponse());
        val authenticator = new TestableOidcAuthenticator(configuration, client, tokenRequest);
        val credentials = new OidcCredentials();
        credentials.setCode("auth-code");

        val verifier = new CodeVerifier("pkce-code-verifier-value-with-at-least-forty-three-characters");
        sessionStore.set(context.webContext(), client.getCodeVerifierSessionAttributeName(), verifier);

        val validated = authenticator.validate(context, credentials).orElseThrow();
        assertSame(credentials, validated);
        assertTrue(authenticator.getLastGrant() instanceof AuthorizationCodeGrant);
        val codeGrant = (AuthorizationCodeGrant) authenticator.getLastGrant();
        assertEquals("auth-code", codeGrant.getAuthorizationCode().getValue());
        assertEquals(verifier, codeGrant.getCodeVerifier());

        assertNotNull(credentials.getAccessToken());
        assertNotNull(credentials.getRefreshToken());
        assertNotNull(credentials.getIdToken());
    }

    @Test
    public void testValidateThrowsOidcTokenExceptionOnTokenErrorResponse() throws Exception {
        val errorObject = OAuth2Error.INVALID_GRANT.setDescription("Bad grant");
        val tokenErrorResponse = new TokenErrorResponse(errorObject).toHTTPResponse();
        val tokenRequest = mockTokenRequestReturning(tokenErrorResponse);
        val authenticator = new TestableOidcAuthenticator(configuration, client, tokenRequest);
        val credentials = new OidcCredentials();
        credentials.setCode("auth-code");

        val exception = assertThrows(OidcTokenException.class, () -> authenticator.validate(context, credentials));
        assertTrue(exception.getMessage().contains("invalid_grant"));
        assertTrue(exception.getMessage().contains("Bad grant"));
    }

    @Test
    public void testRefreshExchangesRefreshTokenAndStoresTokens() throws Exception {
        val tokenRequest = mockTokenRequestReturning(buildSuccessfulTokenResponse());
        val authenticator = new TestableOidcAuthenticator(configuration, client, tokenRequest);
        val credentials = new OidcCredentials();
        credentials.setRefreshTokenObject(new RefreshToken("refresh-token"));

        authenticator.refresh(credentials);

        assertTrue(authenticator.getLastGrant() instanceof RefreshTokenGrant);
        assertNotNull(credentials.getAccessToken());
        assertNotNull(credentials.getRefreshToken());
        assertNotNull(credentials.getIdToken());
    }

    @Test
    public void testRefreshWrapsParseExceptionsInOidcException() throws Exception {
        val invalidResponse = new HTTPResponse(200);
        invalidResponse.setContent("not-a-valid-token-response");
        val tokenRequest = mockTokenRequestReturning(invalidResponse);
        val authenticator = new TestableOidcAuthenticator(configuration, client, tokenRequest);
        val credentials = new OidcCredentials();
        credentials.setRefreshTokenObject(new RefreshToken("refresh-token"));

        assertThrows(OidcException.class, () -> authenticator.refresh(credentials));
    }

    @Test
    public void testCreateTokenRequestUsesClientAuthenticationWhenConfigured() {
        Mockito.when(metadataResolver.getClientAuthenticationTokenEndpoint())
            .thenReturn(new ClientSecretBasic(new ClientID(CLIENT_ID), new Secret(CLIENT_SECRET)));
        val authenticator = new OidcAuthenticator(configuration, client);

        val tokenRequest = authenticator.createTokenRequest(new RefreshTokenGrant(new RefreshToken("refresh-token")));
        val httpRequest = tokenRequest.toHTTPRequest();

        assertNotNull(httpRequest.getAuthorization());
    }

    @Test
    public void testCreateTokenRequestUsesClientIdWhenNoClientAuthentication() {
        Mockito.when(metadataResolver.getClientAuthenticationTokenEndpoint()).thenReturn(null);
        val authenticator = new OidcAuthenticator(configuration, client);

        val tokenRequest = authenticator.createTokenRequest(new RefreshTokenGrant(new RefreshToken("refresh-token")));
        val httpRequest = tokenRequest.toHTTPRequest();
        val body = httpRequest.getBody();

        assertNull(httpRequest.getAuthorization());
        assertNotNull(body);
        assertTrue(body.contains("client_id=" + CLIENT_ID));
    }

    private static HTTPResponse buildSuccessfulTokenResponse() {
        val idToken = new PlainJWT(new JWTClaimsSet.Builder()
            .issuer("https://op.example.org")
            .subject("subject")
            .issueTime(new Date())
            .build()).serialize();
        val tokens = new OIDCTokens(idToken, new BearerAccessToken("access-token"), new RefreshToken("refresh-token"));
        return new OIDCTokenResponse(tokens).toHTTPResponse();
    }

    private static TokenRequest mockTokenRequestReturning(final HTTPResponse response) throws Exception {
        val tokenRequest = Mockito.mock(TokenRequest.class);
        val httpRequest = Mockito.mock(HTTPRequest.class);
        Mockito.when(tokenRequest.toHTTPRequest()).thenReturn(httpRequest);
        Mockito.when(httpRequest.send()).thenReturn(response);
        return tokenRequest;
    }

    private static final class TestableOidcAuthenticator extends OidcAuthenticator {
        private final TokenRequest tokenRequest;
        @Getter
        private AuthorizationGrant lastGrant;

        private TestableOidcAuthenticator(final OidcConfiguration configuration,
                                          final OidcClient client,
                                          final TokenRequest tokenRequest) {
            super(configuration, client);
            this.tokenRequest = tokenRequest;
        }

        @Override
        protected TokenRequest createTokenRequest(final AuthorizationGrant grant) {
            this.lastGrant = grant;
            return tokenRequest;
        }
    }
}
