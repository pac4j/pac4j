package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oidc.config.OidcConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests {@link TokenValidator}.
 *
 * @author Jerome LELEU
 * @since 5.2.0
 */
public final class TokenValidatorTests implements TestsConstants {

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
        when(configuration.getSecret()).thenReturn(SECRET);
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
            "Unsigned ID tokens are not allowed");
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
}
