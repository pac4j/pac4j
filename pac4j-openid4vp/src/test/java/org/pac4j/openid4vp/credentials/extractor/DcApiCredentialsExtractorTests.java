package org.pac4j.openid4vp.credentials.extractor;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.context.CallContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.openid4vp.client.OpenId4VpDcApiClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpDcApiConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.test.context.MockWebContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the single leg of the digital credentials API binding: the page brings the answer back, in any of
 * its three forms.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class DcApiCredentialsExtractorTests {

    private static final String TX_ID = "tx-1";

    @TempDir
    private java.nio.file.Path directory;

    private OpenId4VpDcApiConfiguration configuration;

    private DcApiCredentialsExtractor buildExtractor(final ResponseMode responseMode) {
        configuration = new OpenId4VpDcApiConfiguration();
        configuration.setResponseMode(responseMode);
        configuration.setClientId("did:example:123")
            .setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER)
            .setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}")
            .setJwks(new JwksProperties().setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.setExpectedOrigins(List.of("https://app.example.org"));

        val client = new OpenId4VpDcApiClient(configuration);
        client.setName("Wallet");
        client.setCallbackUrl("https://app.example.org/callback");
        client.init();
        return new DcApiCredentialsExtractor(client);
    }

    private CallContext pageContext(final MockWebContext webContext) {
        configuration.getTransactionStore().set(TX_ID, new VpTransaction().setId(TX_ID).setNonce("nonce")
            .setCreatedAt(Instant.now()).setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES)));
        val sessionStore = new MockSessionStore();
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, TX_ID);
        return new CallContext(webContext, sessionStore);
    }

    @BeforeEach
    void setUp() {
        buildExtractor(ResponseMode.DC_API_JWT);
    }

    @Test
    void testThePageBringsTheEncryptedAnswerBack() {
        val extractor = buildExtractor(ResponseMode.DC_API_JWT);
        val ctx = pageContext(MockWebContext.create().addRequestParameter(RESPONSE, "eyJ.encrypted.answer"));

        val credentials = assertInstanceOf(VerifiablePresentationCredentials.class, extractor.extract(ctx).get());
        assertEquals("eyJ.encrypted.answer", credentials.getTransaction().getRawResponse());
        assertTrue(configuration.getTransactionStore().get(TX_ID).isEmpty());
    }

    @Test
    void testThePageBringsTheClearAnswerBackWhenAsked() {
        val extractor = buildExtractor(ResponseMode.DC_API);
        val ctx = pageContext(MockWebContext.create().addRequestParameter(VP_TOKEN, "{\"pid\":[\"a-presentation\"]}"));

        val credentials = assertInstanceOf(VerifiablePresentationCredentials.class, extractor.extract(ctx).get());
        assertEquals("{\"pid\":[\"a-presentation\"]}", credentials.getTransaction().getRawVpToken());
    }

    @Test
    void testAClearAnswerToAnEncryptedRequestIsRefused() {
        val extractor = buildExtractor(ResponseMode.DC_API_JWT);
        val ctx = pageContext(MockWebContext.create().addRequestParameter(VP_TOKEN, "{\"pid\":[\"a-presentation\"]}"));

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertTrue(e.getMessage().startsWith("the wallet answered in clear a request asking for an encrypted response (dc_api.jwt)"));
    }

    @Test
    void testTheWalletRefused() {
        val extractor = buildExtractor(ResponseMode.DC_API_JWT);
        val ctx = pageContext(MockWebContext.create().addRequestParameter(ERROR, "access_denied"));

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertEquals("the wallet refused the presentation of the transaction tx-1: access_denied", e.getMessage());
        assertTrue(configuration.getTransactionStore().get(TX_ID).isEmpty());
    }
}
