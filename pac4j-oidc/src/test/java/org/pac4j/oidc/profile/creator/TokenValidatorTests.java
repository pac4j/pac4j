package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oidc.config.OidcConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests {@link TokenValidator}.
 *
 * @author Jerome LELEU
 * @since 5.2.0
 */
public final class TokenValidatorTests implements TestsConstants {

    private static final String CLIENT_SECRET = "123456789012345678901234567890ab";

    private OidcConfiguration configuration;

    private List<JWSAlgorithm> algorithms;

    @Before
    public void setUp() throws URISyntaxException {
        configuration = mock(OidcConfiguration.class);
        final OIDCProviderMetadata metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getIssuer()).thenReturn(new Issuer(PAC4J_URL));
        when(metadata.getJWKSetURI()).thenReturn(new URI(PAC4J_BASE_URL));
        when(configuration.findProviderMetadata()).thenReturn(metadata);
        when(configuration.getClientId()).thenReturn(ID);
        when(configuration.getSecret()).thenReturn(CLIENT_SECRET);
        algorithms = new ArrayList<>();
        when(metadata.getIDTokenJWSAlgs()).thenReturn(algorithms);
    }

    @Test
    public void testNoAlgoDefinedAtProvider() {
        TestsHelper.expectException(() -> new TokenValidator(configuration), TechnicalException.class,
            "There must at least one JWS algorithm supported on the OpenID Connect provider side");
    }

    @Test
    public void testNoneAlgoNotAllowed() {
        algorithms.add(JWSAlgorithm.parse("none"));
        TestsHelper.expectException(() -> new TokenValidator(configuration), TechnicalException.class,
            "Unsigned ID tokens are not allowed: they must be explicitly enabled on client side and " +
                "the response_type used must return no ID Token from the authorization endpoint");
    }

    @Test
    public void testNoneAlgoAllowed() {
        algorithms.add(JWSAlgorithm.parse("none"));
        when(configuration.isAllowUnsignedIdTokens()).thenReturn(true);
        final TokenValidator validator = new TokenValidator(configuration);
        final List<IDTokenValidator> validators = validator.getIdTokenValidators();
        assertEquals(1, validators.size());
        assertTrue(validators.get(0) instanceof IDTokenValidator);
    }

    @Test
    public void testNoneAlgoAllowedButIdTokenRequested() {
        when(configuration.getResponseType()).thenReturn("code id_token");
        algorithms.add(JWSAlgorithm.parse("none"));
        when(configuration.isAllowUnsignedIdTokens()).thenReturn(true);
        TestsHelper.expectException(() -> new TokenValidator(configuration), TechnicalException.class,
            "Unsigned ID tokens are not allowed: they must be explicitly enabled on client side and " +
                "the response_type used must return no ID Token from the authorization endpoint");
    }

    @Test
    public void testTwoAlgorithms() {
        algorithms.add(JWSAlgorithm.HS256);
        algorithms.add(JWSAlgorithm.RS256);
        final TokenValidator validator = new TokenValidator(configuration);
        final List<IDTokenValidator> validators = validator.getIdTokenValidators();
        assertEquals(2, validators.size());
    }

    @Test
    public void testTwoAlgorithmsOnePreferred() {
        algorithms.add(JWSAlgorithm.HS256);
        algorithms.add(JWSAlgorithm.RS256);
        when(configuration.getPreferredJwsAlgorithm()).thenReturn(JWSAlgorithm.HS256);
        final TokenValidator validator = new TokenValidator(configuration);
        final List<IDTokenValidator> validators = validator.getIdTokenValidators();
        assertEquals(1, validators.size());
    }

    @Test
    public void testValidateIdToken() throws Exception {
        algorithms.add(JWSAlgorithm.HS256);
        final TokenValidator validator = new TokenValidator(configuration);

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

        final IDTokenClaimsSet claimsSet = validator.validate(SignedJWT.parse(idToken), nonce);
        assertEquals(KEY, claimsSet.getSubject().toString());
        assertEquals(PAC4J_URL, claimsSet.getIssuer().toString());
        assertEquals(ID, claimsSet.getAudience().get(0).toString());
        assertNotNull(claimsSet.getExpirationTime());
        assertNotNull(claimsSet.getIssueTime());
        assertEquals(nonce, claimsSet.getNonce());
    }
}
