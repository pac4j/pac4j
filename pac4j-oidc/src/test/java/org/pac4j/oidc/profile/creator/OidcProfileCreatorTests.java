package org.pac4j.oidc.profile.creator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.credentials.authenticator.OidcAuthenticator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

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

    @Before
    public void setUp() throws Exception {
        this.idTokenClaims = new IDTokenClaimsSet(new JWTClaimsSet.Builder()
            .issuer("pac4j")
            .audience("pac4j")
            .issueTime(new Date())
            .expirationTime(new Date(new Date().getTime() + 5000))
            .subject("pac4j")
            .build());

        configuration = Mockito.mock(OidcConfiguration.class);
        var metadata = Mockito.mock(OIDCProviderMetadata.class);
        Mockito.when(metadata.getIssuer()).thenReturn(new Issuer(PAC4J_URL));
        Mockito.when(metadata.getJWKSetURI()).thenReturn(new URI(PAC4J_BASE_URL));
        Mockito.when(configuration.findProviderMetadata()).thenReturn(metadata);

        var tokenValidator = Mockito.mock(TokenValidator.class);
        Mockito.when(tokenValidator.validateIdToken(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(
                a -> IDTokenClaimsSet.parse(((JWT) a.getArgument(0)).getJWTClaimsSet().toString()));

        Mockito.when(configuration.findTokenValidator()).thenReturn(tokenValidator);
        Mockito.when(configuration.getClientId()).thenReturn(ID);
        Mockito.when(configuration.getSecret()).thenReturn(UUID.randomUUID().toString());
        algorithms = new ArrayList<>();
        Mockito.when(metadata.getIDTokenJWSAlgs()).thenReturn(algorithms);
    }

    @Test
    public void testCreateOidcProfile() throws Exception {
        Mockito.when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();
        credentials.setAccessToken(new BearerAccessToken(UUID.randomUUID().toString()));
        var idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken);
        assertTrue(creator.create(credentials, webContext, new MockSessionStore()).isPresent());
    }

    @Test
    public void testCreateOidcProfileWithoutAccessToken() throws Exception {
        Mockito.when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();
        credentials.setAccessToken(null);
        var idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken);
        assertTrue(creator.create(credentials, webContext, new MockSessionStore()).isPresent());
    }

    @Test
    public void testCreateOidcProfileJwtAccessToken() throws Exception {
        Mockito.when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(false);
        OidcClient client = new OidcClient(configuration);
        client.setAuthenticator(new OidcAuthenticator(configuration, client));
        ProfileCreator creator = new OidcProfileCreator(configuration, client);
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();

        var accessTokenClaims = new JWTClaimsSet.Builder(idTokenClaims.toJWTClaimsSet()).claim("client", "pac4j").build();
        var accessTokenToken = new PlainJWT(accessTokenClaims);
        credentials.setAccessToken(new BearerAccessToken(accessTokenToken.serialize()));

        var idToken = new PlainJWT(idTokenClaims.toJWTClaimsSet());
        credentials.setIdToken(idToken);
        Optional<UserProfile> profile = creator.create(credentials, webContext, new MockSessionStore());
        assertTrue(profile.isPresent());
        assertNull(profile.get().getAttribute("client"));

        Mockito.when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(true);
        profile = creator.create(credentials, webContext, new MockSessionStore());
        assertTrue(profile.isPresent());
        assertEquals("pac4j", profile.get().getAttribute("client"));
    }

    @Test
    public void testNoOidcProfileWithoutAuthenticator() throws Exception {
        Mockito.when(configuration.isIncludeAccessTokenClaimsInProfile()).thenReturn(false);
        Mockito.when(configuration.isCallUserInfoEndpoint()).thenReturn(false);
        ProfileCreator creator = new OidcProfileCreator(configuration, new OidcClient(configuration));
        var webContext = MockWebContext.create();
        var credentials = new OidcCredentials();

        var accessTokenClaims = new JWTClaimsSet.Builder(idTokenClaims.toJWTClaimsSet()).claim("client", "pac4j").build();
        var accessTokenToken = new PlainJWT(accessTokenClaims);
        credentials.setAccessToken(new BearerAccessToken(accessTokenToken.serialize()));

        assertThrows(TechnicalException.class,
            () -> creator.create(credentials, webContext, new MockSessionStore()));
    }
}
