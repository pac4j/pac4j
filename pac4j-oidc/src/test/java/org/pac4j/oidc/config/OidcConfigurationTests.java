package org.pac4j.oidc.config;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.metadata.IOidcOpMetadataResolver;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link OidcConfiguration}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public class OidcConfigurationTests implements TestsConstants {

    private OidcConfiguration config;

    @BeforeEach
    public void setUp() {
        config = new OidcConfiguration();
        config.setClientId(KEY);
        config.setDiscoveryURI(CASSERVERPAC4J_OIDC_URL);
        config.setAllowUnsignedIdTokens(true);
    }

    @Test
    public void testOk() {
        config.init();

        assertEquals("OidcResourceRetriever", config.getResourceRetriever().getClass().getSimpleName());
        assertTrue(config.getOpMetadataResolver() instanceof OidcOpMetadataResolver);
        assertEquals("code", config.getResponseType());
        assertFalse(config.isDisablePkce());
    }


    @Test
    public void testOkNoDiscoveryURIButProviderMetadataResolver() {
        config.setDiscoveryURI(null);
        val resolver = mock(IOidcOpMetadataResolver.class);
        config.setOpMetadataResolver(resolver);

        config.init();

        assertEquals("OidcResourceRetriever", config.getResourceRetriever().getClass().getSimpleName());
        assertEquals(resolver, config.getOpMetadataResolver());
        assertEquals("code", config.getResponseType());
        assertFalse(config.isDisablePkce());
    }

    @Test
    public void testBadResponseType() {
        config.setResponseType("direct");

        val e = assertThrows(OidcConfigurationException.class, () -> {
            config.init();
        });
        assertEquals("Unsupported responseType: direct", e.getMessage());
    }

    @Test
    public void testMissingTrustAnchorsInFederation() {
        config.setDiscoveryURI(null);
        config.getFederation().setTargetOp(VALUE);

        val e = assertThrows(OidcConfigurationException.class, () -> {
            config.init();
        });
        assertEquals("No trust anchors defined", e.getMessage());
    }

    @Test
    public void testMissingClientId() {
        config.setClientId(null);

        val e = assertThrows(TechnicalException.class, () -> {
            config.init();
        });
        assertEquals("clientId cannot be blank", e.getMessage());
    }

    @Test
    public void testSecretMandatoryWhenPkceNotDisabledAndNotImplicitFlow() {
        config.setDisablePkce(true);
        config.setResponseType("code");

        val e = assertThrows(TechnicalException.class, () -> {
            config.init();
        });
        assertEquals("secret cannot be blank", e.getMessage());
    }

    @Test
    public void testNoUnsignedTokensWithImplicit() {
        config.setResponseType("id_token");

        val e = assertThrows(TechnicalException.class, () -> {
            config.init();
        });
        assertEquals("Unsigned ID tokens are not allowed: they must be explicitly enabled on client side and " +
            "the response_type used must return no ID Token from the authorization endpoint", e.getMessage());
    }

    @Test
    public void testDiscoveryURIMandatory() {
        config.setDiscoveryURI(null);

        val e = assertThrows(OidcConfigurationException.class, () -> {
            config.init();
        });
        assertEquals("You must define either the discovery URL or directly the provider metadata resolver or " +
            "the federation target entity (and the appropriate trust anchors)", e.getMessage());
    }

    @Test
    public void testOkSecretNotMandatoryWhenPkceNotDisabledAndNotImplicitFlowAndPrivateKeyJwt() {
        config.setDisablePkce(true);
        config.setResponseType("code");
        config.setClientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT);

        config.init();
    }
}
