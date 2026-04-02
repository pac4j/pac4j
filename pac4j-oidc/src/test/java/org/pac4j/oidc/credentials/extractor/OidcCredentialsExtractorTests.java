package org.pac4j.oidc.credentials.extractor;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.core.context.CallContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.core.credentials.SessionKeyCredentials;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.exceptions.OidcIssuerMismatchException;
import org.pac4j.oidc.exceptions.OidcMissingSessionStateException;
import org.pac4j.oidc.exceptions.OidcMissingStateParameterException;
import org.pac4j.oidc.exceptions.OidcStateMismatchException;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.pac4j.test.context.MockWebContext;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link OidcCredentialsExtractor}.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
public final class OidcCredentialsExtractorTests {

    private static final String CALLBACK_URL = "https://client.example.org/callback";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "secret";
    private static final String SCOPE = "openid profile";

    private OidcConfiguration configuration;
    private OidcClient client;
    private OidcCredentialsExtractor extractor;
    private OidcOpMetadataResolver metadataResolver;
    private OIDCProviderMetadata providerMetadata;
    private MockWebContext webContext;
    private MockSessionStore sessionStore;
    private CallContext context;

    @BeforeEach
    public void beforeEach() {
        configuration = new OidcConfiguration();
        configuration.setClientId(CLIENT_ID);
        configuration.setSecret(CLIENT_SECRET);
        configuration.setScope(SCOPE);

        providerMetadata = Mockito.mock(OIDCProviderMetadata.class);
        Mockito.when(providerMetadata.supportsAuthorizationResponseIssuerParam()).thenReturn(false);
        Mockito.when(providerMetadata.getIssuer()).thenReturn(new Issuer("https://issuer.expected"));

        metadataResolver = Mockito.mock(OidcOpMetadataResolver.class);
        Mockito.when(metadataResolver.load()).thenReturn(providerMetadata);
        configuration.setOpMetadataResolver(metadataResolver);

        client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);

        webContext = MockWebContext.create();
        sessionStore = new MockSessionStore();
        context = new CallContext(webContext, sessionStore);
        extractor = new OidcCredentialsExtractor(configuration, client);
    }

    @Test
    public void testExtractFrontChannelLogoutCredentials() {
        webContext.addRequestParameter("sid", "front-session-id");

        val credentials = (SessionKeyCredentials) extractor.extract(context).orElseThrow();
        assertEquals(LogoutType.FRONT, credentials.getLogoutType());
        assertEquals("front-session-id", credentials.getSessionKey());
    }

    @Test
    public void testExtractBackChannelLogoutWithoutValidation() {
        configuration.setLogoutValidation(false);
        webContext.addRequestParameter("logout_token", buildPlainJwt(Map.of("sid", "back-session-id")));

        val credentials = (SessionKeyCredentials) extractor.extract(context).orElseThrow();
        assertEquals(LogoutType.BACK, credentials.getLogoutType());
        assertEquals("back-session-id", credentials.getSessionKey());
    }

    @Test
    public void testExtractBackChannelLogoutWithValidation() throws Exception {
        val claims = Mockito.mock(LogoutTokenClaimsSet.class);
        Mockito.when(claims.getClaim(OidcConfiguration.NONCE)).thenReturn(null);
        Mockito.when(claims.getClaim("events")).thenReturn(Map.of("http://schemas.openid.net/event/backchannel-logout", Map.of()));
        Mockito.when(claims.getClaim("sid")).thenReturn("validated-session-id");

        val tokenValidator = Mockito.mock(TokenValidator.class);
        Mockito.when(tokenValidator.validateLogoutToken(Mockito.any())).thenReturn(claims);
        Mockito.when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);

        webContext.addRequestParameter("logout_token", buildPlainJwt(Map.of("sid", "ignored")));

        val credentials = (SessionKeyCredentials) extractor.extract(context).orElseThrow();
        assertEquals(LogoutType.BACK, credentials.getLogoutType());
        assertEquals("validated-session-id", credentials.getSessionKey());
    }

    @Test
    public void testExtractBackChannelLogoutFailsWhenNonceClaimExists() throws Exception {
        val claims = Mockito.mock(LogoutTokenClaimsSet.class);
        Mockito.when(claims.getClaim(OidcConfiguration.NONCE)).thenReturn("nonce-value");
        val tokenValidator = Mockito.mock(TokenValidator.class);
        Mockito.when(tokenValidator.validateLogoutToken(Mockito.any())).thenReturn(claims);
        Mockito.when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);

        webContext.addRequestParameter("logout_token", buildPlainJwt(Map.of("sid", "session-id")));

        assertThrows(BadRequestAction.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractBackChannelLogoutFailsWhenEventsClaimIsInvalid() throws Exception {
        val claims = Mockito.mock(LogoutTokenClaimsSet.class);
        Mockito.when(claims.getClaim(OidcConfiguration.NONCE)).thenReturn(null);
        Mockito.when(claims.getClaim("events")).thenReturn(Map.of("wrong-event", Map.of()));
        val tokenValidator = Mockito.mock(TokenValidator.class);
        Mockito.when(tokenValidator.validateLogoutToken(Mockito.any())).thenReturn(claims);
        Mockito.when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);

        webContext.addRequestParameter("logout_token", buildPlainJwt(Map.of("sid", "session-id")));

        assertThrows(BadRequestAction.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractBackChannelLogoutFailsWhenSidClaimIsMissing() throws Exception {
        val claims = Mockito.mock(LogoutTokenClaimsSet.class);
        Mockito.when(claims.getClaim(OidcConfiguration.NONCE)).thenReturn(null);
        Mockito.when(claims.getClaim("events")).thenReturn(Map.of("http://schemas.openid.net/event/backchannel-logout", Map.of()));
        Mockito.when(claims.getClaim("sid")).thenReturn(" ");
        val tokenValidator = Mockito.mock(TokenValidator.class);
        Mockito.when(tokenValidator.validateLogoutToken(Mockito.any())).thenReturn(claims);
        Mockito.when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);

        webContext.addRequestParameter("logout_token", buildPlainJwt(Map.of("sid", "ignored")));

        assertThrows(BadRequestAction.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractBackChannelLogoutFailsWhenTokenCannotBeParsed() {
        webContext.addRequestParameter("logout_token", "not-a-jwt");
        assertThrows(BadRequestAction.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractAuthenticationReturnsEmptyOnErrorResponse() {
        webContext.addRequestParameter("error", "access_denied");
        assertTrue(extractor.extract(context).isEmpty());
    }

    @Test
    public void testExtractAuthenticationFailsOnIssuerMismatch() {
        configuration.setWithState(false);
        Mockito.when(providerMetadata.supportsAuthorizationResponseIssuerParam()).thenReturn(true);
        Mockito.when(providerMetadata.getIssuer()).thenReturn(new Issuer("https://issuer.expected"));

        webContext.addRequestParameter("code", "auth-code");
        webContext.addRequestParameter("iss", "https://issuer.other");

        assertThrows(OidcIssuerMismatchException.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractAuthenticationFailsWhenSessionStateIsMissing() {
        webContext.addRequestParameter("code", "auth-code");
        webContext.addRequestParameter("state", "state-value");

        assertThrows(OidcMissingSessionStateException.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractAuthenticationFailsWhenResponseStateIsMissing() {
        sessionStore.set(webContext, client.getStateSessionAttributeName(), new State("expected-state"));
        webContext.addRequestParameter("code", "auth-code");

        assertThrows(OidcMissingStateParameterException.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractAuthenticationFailsWhenStateDoesNotMatch() {
        sessionStore.set(webContext, client.getStateSessionAttributeName(), new State("expected-state"));
        webContext.addRequestParameter("code", "auth-code");
        webContext.addRequestParameter("state", "other-state");

        assertThrows(OidcStateMismatchException.class, () -> extractor.extract(context));
    }

    @Test
    public void testExtractAuthenticationSuccessWithCodeIdTokenAndAccessToken() {
        sessionStore.set(webContext, client.getStateSessionAttributeName(), new State("expected-state"));
        webContext.addRequestParameter("code", "auth-code");
        webContext.addRequestParameter("state", "expected-state");
        webContext.addRequestParameter("access_token", "access-token");
        webContext.addRequestParameter("token_type", "Bearer");
        webContext.addRequestParameter("id_token", buildPlainJwt(Map.of(
            "iss", "https://issuer.expected",
            "sub", "subject",
            "iat", new Date().getTime() / 1000)));

        val extracted = (OidcCredentials) extractor.extract(context).orElseThrow();
        assertEquals("auth-code", extracted.getCode());
        assertNotNull(extracted.getIdToken());
        assertNotNull(extracted.getAccessToken());
    }

    private static String buildPlainJwt(final Map<String, Object> claims) {
        val builder = new JWTClaimsSet.Builder();
        for (val entry : claims.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        return new PlainJWT(builder.build()).serialize();
    }
}
