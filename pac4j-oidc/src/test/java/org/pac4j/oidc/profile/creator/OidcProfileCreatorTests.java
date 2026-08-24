package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.test.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.federation.config.OidcFederationProperties;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.web.ServerResponse;
import org.pac4j.test.web.WebServer;

import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link OidcProfileCreatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.4.4
 */
public class OidcProfileCreatorTests implements TestsConstants {
    private OidcConfiguration configuration;

    private IDTokenClaimsSet idTokenClaims;

    private List<JWSAlgorithm> algorithms;

    private OIDCProviderMetadata metadata;

    @BeforeEach
    public void setUp() throws Exception {
        this.idTokenClaims = new IDTokenClaimsSet(new JWTClaimsSet.Builder()
            .issuer("pac4j")
            .audience("pac4j")
            .issueTime(new Date())
            .expirationTime(new Date(new Date().getTime() + 5000))
            .subject("pac4j")
            .build());

        configuration = mock(OidcConfiguration.class);
        metadata = mock(OIDCProviderMetadata.class);
        when(configuration.getFederation()).thenReturn(new OidcFederationProperties());
        when(metadata.getIssuer()).thenReturn(new Issuer(PAC4J_URL));
        when(metadata.getJWKSetURI()).thenReturn(new URI(PAC4J_BASE_URL));
        val metadataResolver = mock(OidcOpMetadataResolver.class);
        when(metadataResolver.load()).thenReturn(metadata);
        when(configuration.getOpMetadataResolver()).thenReturn(metadataResolver);

        var tokenValidator = mock(TokenValidator.class);
        when(tokenValidator.validateIdToken(any(), any())).thenAnswer(
                a -> IDTokenClaimsSet.parse(((JWT) a.getArgument(0)).getJWTClaimsSet().toString()));

        when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);
        when(configuration.getClientId()).thenReturn(ID);
        when(configuration.getSecret()).thenReturn(UUID.randomUUID().toString());
        algorithms = new ArrayList<>();
        when(metadata.getIDTokenJWSAlgs()).thenReturn(algorithms);
    }

    /**
     * Starts a local UserInfo endpoint returning the given subject and declares it in the OP metadata.
     */
    private WebServer startUserInfoEndpoint(final String subject) {
        val body = "{\"sub\":\"" + subject + "\",\"email\":\"" + subject + "@example.org\"}";
        val webServer = new WebServer(0)
            .defineResponse("ok", new ServerResponse(WebServer.Response.Status.OK, "application/json", body));
        webServer.start();

        when(metadata.getUserInfoEndpointURI())
            .thenReturn(java.net.URI.create("http://localhost:" + webServer.getListeningPort() + "/userinfo?r=ok"));
        when(configuration.isCallUserInfoEndpoint()).thenReturn(true);
        return webServer;
    }

    private Optional<UserProfile> createProfileWithIdToken() throws Exception {
        val client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        val creator = new OidcProfileCreator(configuration, client);
        val credentials = new OidcCredentials();
        credentials.setAccessToken(new BearerAccessToken(UUID.randomUUID().toString()).toJSONObject());
        credentials.setIdToken(new PlainJWT(idTokenClaims.toJWTClaimsSet()).serialize());
        return creator.create(new CallContext(MockWebContext.create(), new MockSessionStore()), credentials);
    }

    @Test
    public void testUserInfoSubjectMatchingTheIdTokenSubject() throws Exception {
        // the ID token subject is 'pac4j'
        val webServer = startUserInfoEndpoint("pac4j");
        try {
            val profile = createProfileWithIdToken();

            assertTrue(profile.isPresent());
            assertEquals("pac4j", profile.get().getId());
            assertEquals("pac4j@example.org", profile.get().getAttribute("email"));
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testUserInfoSubjectNotMatchingTheIdTokenSubject() throws Exception {
        // the access token belongs to another user than the one of the validated ID token
        val webServer = startUserInfoEndpoint("another-user");
        try {
            val e = assertThrows(OidcException.class, this::createProfileWithIdToken);
            assertTrue(e.getMessage().contains("does not match the ID token subject"));
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void testCreateOidcProfile() throws Exception {
        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();
        credentials.setAccessToken(new BearerAccessToken(UUID.randomUUID().toString()).toJSONObject());
        JWT idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken.serialize());
        assertTrue(creator.create(new CallContext(webContext, new MockSessionStore()), credentials).isPresent());
    }

    @Test
    public void testCreateOidcProfileWithoutAccessToken() throws Exception {
        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();
        credentials.setAccessToken(null);
        var idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken.serialize());
        assertTrue(creator.create(new CallContext(webContext, new MockSessionStore()), credentials).isPresent());
    }

    @Test
    public void testCreateOidcProfileJwtAccessToken() throws Exception {
        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(false);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();

        var accessTokenClaims = new JWTClaimsSet.Builder(idTokenClaims.toJWTClaimsSet()).claim("client", "pac4j").build();
        var accessTokenToken = new PlainJWT(accessTokenClaims);
        credentials.setAccessToken(new BearerAccessToken(accessTokenToken.serialize()).toJSONObject());

        JWT idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken.serialize());
        Optional<UserProfile> profile = creator.create(new CallContext(webContext, new MockSessionStore()), credentials);
        assertTrue(profile.isPresent());
        assertNull(profile.get().getAttribute("client"));

        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        profile = creator.create(new CallContext(webContext, new MockSessionStore()), credentials);
        assertTrue(profile.isPresent());
        assertEquals("pac4j", profile.get().getAttribute("client"));
    }

    @Test
    public void testNoOidcProfileWithoutAuthenticator() throws Exception {
        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(false);
        when(configuration.isCallUserInfoEndpoint()).thenReturn(false);
        ProfileCreator creator = new OidcProfileCreator(configuration, new OidcClient(configuration));
        var webContext = MockWebContext.create();
        var credentials = new TokenCredentials();

        var accessTokenClaims = new JWTClaimsSet.Builder(idTokenClaims.toJWTClaimsSet()).claim("client", "pac4j").build();
        var accessTokenToken = new PlainJWT(accessTokenClaims);
        credentials.setToken(accessTokenToken.serialize());

        assertThrows(OidcConfigurationException.class,
            () -> creator.create(new CallContext(webContext, new MockSessionStore()), credentials));
    }

    @Test
    public void testBearerAccessTokenSkipNonce() throws Exception {
        when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        when(configuration.isCallUserInfoEndpoint()).thenReturn(true);
        when(configuration.isUseNonce()).thenReturn(true);
        ProfileCreator creator = new OidcProfileCreator(configuration, new OidcClient(configuration));
        var webContext = MockWebContext.create();
        var credentials = new TokenCredentials();

        var accessTokenClaims = new JWTClaimsSet.Builder(idTokenClaims.toJWTClaimsSet()).claim("client", "pac4j").build();
        var accessTokenToken = new PlainJWT(accessTokenClaims);
        credentials.setToken(accessTokenToken.serialize());

        // create does not throw exception in this case and we do not extract the token claims
        var profile = creator.create(new CallContext(webContext, new MockSessionStore()), credentials);
        assertTrue(profile.isPresent());
        assertNull(profile.get().getAttribute("client"));
    }
}
