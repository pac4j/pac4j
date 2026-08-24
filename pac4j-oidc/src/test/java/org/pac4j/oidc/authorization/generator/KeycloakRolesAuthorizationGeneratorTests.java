package org.pac4j.oidc.authorization.generator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pac4j.core.context.CallContext;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link KeycloakRolesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class KeycloakRolesAuthorizationGeneratorTests {

    private static final String CLIENT_ID = "keycloakid";
    private static final String ISSUER = "http://localhost:8080/auth/realms/demo";
    private static final String SECRET = "0123456789012345678901234567890123456789";
    private static final String OTHER_SECRET = "abcdefghijabcdefghijabcdefghijabcdefghij";

    private OidcConfiguration configuration;
    private CallContext context;

    @BeforeEach
    public void beforeEach() {
        configuration = new OidcConfiguration();
        configuration.setClientId(CLIENT_ID);
        configuration.setSecret(SECRET);
        configuration.setIdTokenSigningAlgorithm(JWSAlgorithm.HS256);

        val providerMetadata = Mockito.mock(OIDCProviderMetadata.class);
        Mockito.when(providerMetadata.getIssuer()).thenReturn(new Issuer(ISSUER));
        Mockito.when(providerMetadata.getIDTokenJWSAlgs()).thenReturn(List.of(JWSAlgorithm.HS256));

        // built outside the stubbing calls: it invokes the mocked metadata itself
        val tokenValidator = new TokenValidator(configuration, providerMetadata);

        val metadataResolver = Mockito.mock(OidcOpMetadataResolver.class);
        Mockito.when(metadataResolver.load()).thenReturn(providerMetadata);
        Mockito.when(metadataResolver.getTokenValidator()).thenReturn(tokenValidator);
        configuration.setOpMetadataResolver(metadataResolver);

        context = new CallContext(MockWebContext.create(), new MockSessionStore());
    }

    private static String buildAccessToken(final String secret) throws Exception {
        return buildAccessToken(secret, List.of(CLIENT_ID, "account"), CLIENT_ID, 600_000);
    }

    private static String buildAccessToken(final String secret, final List<String> audience, final String azp,
                                           final long validity) throws Exception {
        val claims = new JWTClaimsSet.Builder()
            .issuer(ISSUER)
            .subject("3b463018-95a1-4328-9920-b4377b29164e")
            .audience(audience)
            .claim("azp", azp)
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + validity))
            .claim("realm_access", Map.of("roles", List.of("offline_access", "ROLE_BINGO", "uma_authorization")))
            .claim("resource_access", Map.of(
                CLIENT_ID, Map.of("roles", List.of("ROLE_CLIENTISSIME")),
                "account", Map.of("roles", List.of("manage-account", "view-profile"))))
            .build();
        val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(secret));
        return jwt.serialize();
    }

    private static KeycloakOidcProfile buildProfile(final String accessToken) {
        val profile = new KeycloakOidcProfile();
        profile.setAccessToken(new BearerAccessToken(accessToken));
        return profile;
    }

    @Test
    public void testRolesOfAValidatedAccessToken() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val profile = buildProfile(buildAccessToken(SECRET));

        generator.generate(context, profile);

        assertEquals(4, profile.getRoles().size());
        assertTrue(profile.getRoles().contains("ROLE_BINGO"));
        assertTrue(profile.getRoles().contains("ROLE_CLIENTISSIME"));
    }

    @Test
    public void testRolesWhenTheClientIsNotInTheAudienceButIsTheAuthorizedParty() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val profile = buildProfile(buildAccessToken(SECRET, List.of("account", "realm-management"), CLIENT_ID, 600_000));

        generator.generate(context, profile);

        assertEquals(4, profile.getRoles().size());
    }

    @Test
    public void testNoRoleWhenNeitherTheAudienceNorTheAuthorizedPartyMatch() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val profile = buildProfile(buildAccessToken(SECRET, List.of("account"), "another-client", 600_000));

        generator.generate(context, profile);

        assertEquals(0, profile.getRoles().size());
    }

    @Test
    public void testNoRoleWhenTheAccessTokenIsExpiredEvenIfTheAuthorizedPartyMatches() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val profile = buildProfile(buildAccessToken(SECRET, List.of("account"), CLIENT_ID, -600_000));

        generator.generate(context, profile);

        assertEquals(0, profile.getRoles().size());
    }

    @Test
    public void testNoRoleWhenTheAccessTokenIsNotSigned() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val claims = new JWTClaimsSet.Builder()
            .issuer(ISSUER).subject("s").audience(List.of("account")).claim("azp", CLIENT_ID)
            .issueTime(new Date()).expirationTime(new Date(System.currentTimeMillis() + 600_000))
            .claim("realm_access", Map.of("roles", List.of("ROLE_BINGO")))
            .build();
        val profile = buildProfile(new com.nimbusds.jwt.PlainJWT(claims).serialize());

        generator.generate(context, profile);

        assertEquals(0, profile.getRoles().size());
    }

    @Test
    public void testNoRoleWhenTheAccessTokenSignatureIsInvalid() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator(configuration);
        val profile = buildProfile(buildAccessToken(OTHER_SECRET));

        generator.generate(context, profile);

        assertEquals(0, profile.getRoles().size());
    }

    @Test
    public void testNoRoleWithoutConfiguration() throws Exception {
        val generator = new KeycloakRolesAuthorizationGenerator();
        val profile = buildProfile(buildAccessToken(SECRET));

        generator.generate(context, profile);

        assertEquals(0, profile.getRoles().size());
    }
}
