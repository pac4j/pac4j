package org.pac4j.openid4vp.redirect;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.wallet.WalletSimulator;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the wallet URL of a request which cannot be signed.
 *
 * <p>The {@code redirect_uri} Client Identifier Prefix gives the wallet no key it could trust, so the
 * specification forbids signing such a request. The parameters therefore travel in the URL, and no request
 * object is ever built nor fetched.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class UnsignedRequestTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";

    private OpenId4VpClient client;

    @BeforeEach
    void setUp() {
        // no client identifier is typed: for this prefix it is the response URI of each transaction
        val configuration = new OpenId4VpConfiguration();
        configuration.setClientIdPrefix(ClientIdPrefix.REDIRECT_URI)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}");
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        client = new OpenId4VpClient(configuration);
        client.setName("Wallet");
        client.setCallbackUrl(CALLBACK_URL);
    }

    private String walletUrl() {
        val ctx = new CallContext(MockWebContext.create(), new MockSessionStore());
        return assertInstanceOf(FoundAction.class, client.getRedirectionAction(ctx).get()).getLocation();
    }

    private static String parameter(final String url, final String name) {
        for (val parameter : url.substring(url.indexOf('?') + 1).split("&")) {
            val separator = parameter.indexOf('=');
            if (separator > 0 && name.equals(parameter.substring(0, separator))) {
                return URLDecoder.decode(parameter.substring(separator + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    @Test
    void testNoRequestObjectIsHandedOver() {
        val url = walletUrl();

        // there is nothing to fetch: the request is the URL
        assertNull(parameter(url, REQUEST_URI));
        assertTrue(url.startsWith("openid4vp://"));
    }

    @Test
    void testTheParametersTravelInTheUrl() {
        val url = walletUrl();

        // the identifier is the response URI itself, transaction included, so a wallet may post there as such
        assertEquals(ClientIdPrefix.REDIRECT_URI.getValue() + ":" + parameter(url, RESPONSE_URI), parameter(url, CLIENT_ID));
        assertEquals(RESPONSE_TYPE_VP_TOKEN, parameter(url, RESPONSE_TYPE));
        assertEquals("direct_post.jwt", parameter(url, RESPONSE_MODE));
        assertNotNull(parameter(url, NONCE));
        assertTrue(parameter(url, RESPONSE_URI).contains(VP_TRANSACTION_ID + "="));
        // the query and the metadata are carried whole, as JSON
        assertTrue(parameter(url, DCQL_QUERY).contains("\"format\":\"dc+sd-jwt\""));
        assertTrue(parameter(url, CLIENT_METADATA).contains("\"" + JWKS + "\""));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTheWholeUnsignedFlow() {
        val simulator = new WalletSimulator();
        val browserCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        val url = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browserCtx).get()).getLocation();

        // the wallet reads the request from the URL itself, nothing to fetch
        assertFalse(simulator.hasRequestUri(url));
        val request = simulator.readRequestParameters(url);
        assertEquals(ClientIdPrefix.REDIRECT_URI.getValue() + ":" + request.getResponseUri(), request.getClientId());
        assertNotNull(request.getEncryptionKey());
        val credentialQueries = (List<Map<String, Object>>) request.getDcqlQuery().get("credentials");
        assertEquals(1, credentialQueries.size());
        assertEquals("pid", credentialQueries.get(0).get("id"));

        // then answers, encrypted, at the response URI
        val response = simulator.buildResponse(request, Map.of("pid", List.of("a-presentation")));
        val post = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, parameter(request.getResponseUri(), VP_TRANSACTION_ID))
            .addRequestParameter(RESPONSE, response);
        assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(post, new MockSessionStore())));

        // and the browser comes back to what the wallet left
        val credentials = assertInstanceOf(VerifiablePresentationCredentials.class, client.getCredentials(browserCtx).get());
        assertEquals(response, credentials.getTransaction().getRawResponse());
        assertEquals(request.getNonce(), credentials.getTransaction().getNonce());
    }

    @Test
    void testNoSigningKeyIsNeeded() {
        // nothing was configured to sign with, and the client initializes all the same
        assertNull(client.getConfiguration().getRequestObjectSigningKey());
    }
}
