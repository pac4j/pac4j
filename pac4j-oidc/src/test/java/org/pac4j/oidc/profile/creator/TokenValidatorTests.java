package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oidc.config.OidcConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link TokenValidator}.
 *
 * @author Jerome LELEU
 * @since 5.2.0
 */
public final class TokenValidatorTests implements TestsConstants {

    private static final String CLIENT_SECRET = "123456789012345678901234567890ab";

    private OidcConfiguration configuration;

    private OIDCProviderMetadata metadata;

    private List<JWSAlgorithm> algorithms;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        configuration = mock(OidcConfiguration.class);
        metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getIssuer()).thenReturn(new Issuer(PAC4J_URL));
        when(metadata.getJWKSetURI()).thenReturn(new URI(PAC4J_BASE_URL));
        when(configuration.getClientId()).thenReturn(ID);
        when(configuration.getSecret()).thenReturn(CLIENT_SECRET);
        algorithms = new ArrayList<>();
        when(metadata.getIDTokenJWSAlgs()).thenReturn(algorithms);
    }

    @Test
    public void testNoAlgoDefinedAtProvider() {
        TestsHelper.expectException(() -> new TokenValidator(configuration, metadata), TechnicalException.class,
            "There must at least one JWS algorithm supported on the OpenID Connect provider side");
    }

    @Test
    public void testNoneAlgoNotAllowed() {
        algorithms.add(JWSAlgorithm.parse("none"));
        TestsHelper.expectException(() -> new TokenValidator(configuration, metadata), TechnicalException.class,
            "Unsigned ID tokens are not allowed: they must be explicitly enabled on client side and " +
                "the response_type used must return no ID Token from the authorization endpoint");
    }

    @Test
    public void testNoneAlgoAllowed() {
        algorithms.add(JWSAlgorithm.parse("none"));
        when(configuration.isAllowUnsignedIdTokens()).thenReturn(true);
        final TokenValidator validator = new TokenValidator(configuration, metadata);
        final List<IDTokenValidator> idTokenValidators = validator.getIdTokenValidators();
        assertEquals(1, idTokenValidators.size());
        assertTrue(idTokenValidators.get(0) instanceof IDTokenValidator);
        final List<LogoutTokenValidator> logoutTokenValidators = validator.getLogoutTokenValidators();
        assertEquals(0, logoutTokenValidators.size());
    }

    @Test
    public void testNoneAlgoAllowedButIdTokenRequested() {
        when(configuration.getResponseType()).thenReturn("code id_token");
        algorithms.add(JWSAlgorithm.parse("none"));
        when(configuration.isAllowUnsignedIdTokens()).thenReturn(true);
        TestsHelper.expectException(() -> new TokenValidator(configuration, metadata), TechnicalException.class,
            "Unsigned ID tokens are not allowed: they must be explicitly enabled on client side and " +
                "the response_type used must return no ID Token from the authorization endpoint");
    }

    @Test
    public void testTwoAlgorithms() {
        algorithms.add(JWSAlgorithm.HS256);
        algorithms.add(JWSAlgorithm.RS256);
        when(metadata.supportsBackChannelLogout()).thenReturn(true);

        final TokenValidator validator = new TokenValidator(configuration, metadata);
        final List<IDTokenValidator> idTokenValidators = validator.getIdTokenValidators();
        assertEquals(2, idTokenValidators.size());

        final List<LogoutTokenValidator> logoutTokenValidators = validator.getLogoutTokenValidators();
        assertEquals(2, logoutTokenValidators.size());
    }

    @Test
    public void testTwoAlgorithmsOnePreferred() {
        algorithms.add(JWSAlgorithm.HS256);
        algorithms.add(JWSAlgorithm.RS256);
        when(metadata.supportsBackChannelLogout()).thenReturn(true);

        when(configuration.getPreferredJwsAlgorithm()).thenReturn(JWSAlgorithm.HS256);
        final TokenValidator validator = new TokenValidator(configuration, metadata);
        final List<IDTokenValidator> idTokenValidators = validator.getIdTokenValidators();
        assertEquals(1, idTokenValidators.size());

        final List<LogoutTokenValidator> logoutTokenValidators = validator.getLogoutTokenValidators();
        assertEquals(1, logoutTokenValidators.size());
    }

    @Test
    public void testValidateIdToken() throws Exception {
        algorithms.add(JWSAlgorithm.HS256);
        final TokenValidator validator = new TokenValidator(configuration, metadata);

        final JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(CLIENT_SECRET, JWSAlgorithm.HS256));
        final Map<String, Object> claims = new HashMap<>();
        claims.put("iss", PAC4J_URL);
        claims.put("sub", KEY);
        claims.put("aud", ID);
        final long now = new Date().getTime() / 1000;
        claims.put("exp", now + 1000);
        claims.put("iat", now);
        final Nonce nonce = new Nonce();
        claims.put("nonce", nonce.toString());
        final String idToken = generator.generate(claims);

        final IDTokenClaimsSet claimsSet = validator.validateIdToken(SignedJWT.parse(idToken), nonce);
        assertEquals(KEY, claimsSet.getSubject().toString());
        assertEquals(PAC4J_URL, claimsSet.getIssuer().toString());
        assertEquals(ID, claimsSet.getAudience().get(0).toString());
        assertNotNull(claimsSet.getExpirationTime());
        assertNotNull(claimsSet.getIssueTime());
        assertEquals(nonce, claimsSet.getNonce());
    }

    @Test
    public void testValidateLogoutToken() throws Exception {
        algorithms.add(JWSAlgorithm.HS256);
        when(metadata.supportsBackChannelLogout()).thenReturn(true);

        final TokenValidator validator = new TokenValidator(configuration, metadata);

        final JwtGenerator generator = new JwtGenerator(new SecretSignatureConfiguration(CLIENT_SECRET, JWSAlgorithm.HS256));
        final Map<String, Object> claims = new HashMap<>();
        claims.put("iss", PAC4J_URL);
        claims.put("sub", KEY);
        claims.put("aud", ID);
        final long now = new Date().getTime() / 1000;
        claims.put("exp", now + 1000);
        claims.put("iat", now);

        JSONObject events = new JSONObject();
        events.put(LogoutTokenClaimsSet.EVENT_TYPE, new JSONObject());
        claims.put(LogoutTokenClaimsSet.EVENTS_CLAIM_NAME, events);

        final String jti = UUID.randomUUID().toString();
        claims.put("jti", jti);

        String sessionId = UUID.randomUUID().toString();
        claims.put("sid", sessionId);
        final String logoutToken = generator.generate(claims);

        final LogoutTokenClaimsSet claimsSet = validator.validateLogoutToken(SignedJWT.parse(logoutToken));
        assertEquals(KEY, claimsSet.getSubject().toString());
        assertEquals(PAC4J_URL, claimsSet.getIssuer().toString());
        assertEquals(ID, claimsSet.getAudience().get(0).toString());
        assertNotNull(claimsSet.getExpirationTime());
        assertNotNull(claimsSet.getIssueTime());
        assertEquals(claimsSet.getClaim(LogoutTokenClaimsSet.EVENTS_CLAIM_NAME), events);
        assertEquals(jti, claimsSet.getJWTID().toString());
        assertEquals(sessionId, claimsSet.getSessionID().toString());
    }
}
