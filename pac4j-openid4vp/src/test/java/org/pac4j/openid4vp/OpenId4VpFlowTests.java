package org.pac4j.openid4vp;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.WalletInvocationMode;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.openid4vp.wallet.WalletSimulator;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.RESPONSE;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.VP_TRANSACTION_ID;

/**
 * Drives a whole presentation, the three legs in a row: the browser leaves, the wallet fetches the request
 * object then posts its response, and the browser comes back.
 *
 * <p>Everything runs in process, so this is the loop to work in: no phone, no certificate, no network.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpFlowTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";
    private static final String PRESENTATION = "a-presentation-this-verifier-cannot-validate-yet";

    private OpenId4VpClient client;
    private OpenId4VpConfiguration configuration;

    @BeforeEach
    void setUp() throws Exception {
        val generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));

        configuration = new OpenId4VpConfiguration();
        configuration.setClientId(CALLBACK_URL);
        configuration.setClientIdPrefix(ClientIdPrefix.REDIRECT_URI);
        configuration.setInvocationMode(WalletInvocationMode.CUSTOM_SCHEME);
        configuration.setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}");
        configuration.setRequestObjectSignatureConfiguration(new ECSignatureConfiguration(generator.generateKeyPair()));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl(CALLBACK_URL);
    }


    @Test
    void testTheWholePresentationFlow() {
        val simulator = new WalletSimulator();

        // 1. the browser asks for a protected page and is handed the wallet URL
        val browserContext = MockWebContext.create();
        val browserCtx = new CallContext(browserContext, new MockSessionStore());
        val walletUrl = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browserCtx).get()).getLocation();

        val requestUri = simulator.readRequestUri(walletUrl);
        val transactionId = simulator.readParameter(requestUri, VP_TRANSACTION_ID);
        assertNotNull(transactionId);

        // 2. the wallet fetches the request object, on a request carrying no session at all
        val fetch = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, transactionId);
        val served = assertThrows(OkAction.class,
            () -> client.getCredentials(new CallContext(fetch, new MockSessionStore())));

        val request = simulator.readRequestObject(served.getContent());
        assertEquals(ClientIdPrefix.REDIRECT_URI.getValue() + ":" + CALLBACK_URL, request.getClientId());
        assertTrue(request.getResponseUri().contains(VP_TRANSACTION_ID + "=" + transactionId));

        // 3. the wallet answers, encrypted to the key the request object published
        val response = simulator.buildResponse(request, Map.of("pid", List.of(PRESENTATION)));
        val post = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId)
            .addRequestParameter(RESPONSE, response);
        assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(post, new MockSessionStore())));

        // 4. the browser comes back: this is the only leg with a session, and the only one making credentials
        val credentials = assertInstanceOf(VerifiablePresentationCredentials.class,
            client.getCredentials(browserCtx).get());
        assertEquals(transactionId, credentials.getTransaction().getId());
        assertEquals(response, credentials.getTransaction().getRawResponse());
        assertEquals(request.getNonce(), credentials.getTransaction().getNonce());

        // and the transaction is consumed
        assertTrue(configuration.getTransactionStore().get(transactionId).isEmpty());
    }

    @Test
    void testTheBrowserGetsNothingBeforeTheWalletAnswered() {
        val browserCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        client.getRedirectionAction(browserCtx);

        assertTrue(client.getCredentials(browserCtx).isEmpty());
    }
}
