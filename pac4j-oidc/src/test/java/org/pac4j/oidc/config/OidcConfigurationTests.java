package org.pac4j.oidc.config;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.interceptor.HttpInterceptor;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.metadata.IOidcOpMetadataResolver;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.test.util.TestsConstants;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link OidcConfiguration}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class OidcConfigurationTests implements TestsConstants {

    private OidcConfiguration config;

    @BeforeEach
    public void setUp() {
        config = new OidcConfiguration();
        config.setClientId(KEY);
        config.setDiscoveryURI(CASSERVERPAC4J_OIDC_URL);
        config.setAllowUnsignedIdTokens(true);
        config.setOidcClient(mock(OidcClient.class));
    }

    @Test
    public void testHttpSettingsAreAppliedBeforeTheHttpInterceptor() throws Exception {
        config.setConnectTimeout(1234);
        config.setReadTimeout(5678);
        val ssl = (SSLSocketFactory) SSLSocketFactory.getDefault();
        val hostnameVerifier = mock(HostnameVerifier.class);
        config.setSslSocketFactory(ssl);
        config.setHostnameVerifier(hostnameVerifier);
        val request = new HTTPRequest(
            HTTPRequest.Method.POST, new URI("https://op.example/token"));
        val ctx = new CallContext(null, null);
        config.setHttpInterceptor((configuredRequest, callContext) -> {
            val configured = (HTTPRequest) configuredRequest;
            assertSame(ctx, callContext);
            // the HTTP settings are already applied and can be overridden
            assertEquals(5678, configured.getReadTimeout());
            configured.setReadTimeout(9999);
            configured.setHeader("X-Test", "configured");
        });
        config.configureHttpRequest(request, ctx);
        assertEquals(1234, request.getConnectTimeout());
        assertEquals(9999, request.getReadTimeout());
        assertSame(ssl, request.getSSLSocketFactory());
        assertSame(hostnameVerifier, request.getHostnameVerifier());
        assertEquals("configured", request.getHeaderMap().get("X-Test").get(0));

        config.setHttpInterceptor(null);
        val untouched = new HTTPRequest(HTTPRequest.Method.POST, new URI("https://op.example/token"));
        config.configureHttpRequest(untouched, ctx);
        assertEquals(5678, untouched.getReadTimeout());
        assertNull(untouched.getHeaderMap().get("X-Test"));
    }

    @Test
    public void testSendSendsAgainOnceWhenTheHttpInterceptorAsksForIt() throws Exception {
        val request = mock(HTTPRequest.class);
        val refused = new HTTPResponse(400);
        val accepted = new HTTPResponse(200);
        when(request.send()).thenReturn(refused, accepted, refused);
        val ctx = new CallContext(null, null);
        val handled = new ArrayList<Object>();
        config.setHttpInterceptor(new HttpInterceptor() {
            @Override
            public void configureRequest(final Object configured, final CallContext callContext) {
                ((HTTPRequest) configured).setHeader("X-Test", "configured");
            }

            @Override
            public boolean handleResponse(final Object handledRequest, final Object response, final CallContext callContext) {
                assertSame(request, handledRequest);
                assertSame(ctx, callContext);
                handled.add(response);
                return true;
            }
        });

        // sent again once, even if the interceptor asks for more
        assertSame(accepted, config.send(request, ctx));
        assertEquals(List.of(refused, accepted), handled);
        verify(request, times(2)).setHeader("X-Test", "configured");
        verify(request, times(2)).send();

        config.setHttpInterceptor((configured, callContext) -> { });
        assertSame(refused, config.send(request, ctx));
        config.setHttpInterceptor(null);
        when(request.send()).thenReturn(accepted);
        assertSame(accepted, config.send(request, ctx));
        verify(request, times(4)).send();
    }

    @Test
    public void testToString() {
        config = new OidcConfiguration();
        val client = new OidcClient(config);
        assertNotNull(client.toString());
        assertNotNull(config.toString());
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
