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
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;

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
        var metadata = mock(OIDCProviderMetadata.class);
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
}
