package org.pac4j.openid4vp.credentials.extractor;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests {@link OpenId4VpCredentialsExtractor}: the three kinds of requests reaching the callback endpoint
 * must be told apart, the two wallet ones being answered without ever building any credentials.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpCredentialsExtractorTests {

    private static final String TX_ID = "tx-1";
    private static final String REQUEST_OBJECT = "eyJ0eXAiOiJvYXV0aC1hdXRoei1yZXErand0In0.claims.signature";
    private static final String WALLET_RESPONSE = "eyJhbGciOiJFQ0RILUVTIn0.encrypted.response";

    private OpenId4VpConfiguration configuration;
    private OpenId4VpCredentialsExtractor extractor;

    @BeforeEach
    void setUp() {
        configuration = new OpenId4VpConfiguration();
        val client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        extractor = new OpenId4VpCredentialsExtractor(client);
    }

    private VpTransaction storeTransaction() {
        val transaction = new VpTransaction()
            .setId(TX_ID)
            .setNonce("nonce")
            .setRequestObject(REQUEST_OBJECT)
            .setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        configuration.getTransactionStore().set(TX_ID, transaction);
        return transaction;
    }

    @Test
    void testWalletFetchesTheRequestObject() {
        storeTransaction();
        val webContext = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, TX_ID);
        val ctx = new CallContext(webContext, new MockSessionStore());

        val action = assertThrows(OkAction.class, () -> extractor.extract(ctx));
        assertEquals(REQUEST_OBJECT, action.getContent());
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED,
            configuration.getTransactionStore().get(TX_ID).get().getStatus());
    }

    @Test
    void testWalletPostsItsResponse() {
        storeTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, WALLET_RESPONSE);
        val ctx = new CallContext(webContext, new MockSessionStore());

        assertThrows(OkAction.class, () -> extractor.extract(ctx));

        val transaction = configuration.getTransactionStore().get(TX_ID).get();
        assertEquals(WALLET_RESPONSE, transaction.getRawResponse());
        assertEquals(VpTransaction.Status.RESPONSE_RECEIVED, transaction.getStatus());
    }

    @Test
    void testBrowserComesBackOnceTheWalletAnswered() {
        val transaction = storeTransaction();
        transaction.setRawResponse(WALLET_RESPONSE);
        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, TX_ID);
        val ctx = new CallContext(webContext, sessionStore);

        val credentials = (VerifiablePresentationCredentials) extractor.extract(ctx).get();
        assertEquals(TX_ID, credentials.getTransaction().getId());
        assertEquals(WALLET_RESPONSE, credentials.getTransaction().getRawResponse());
        // a transaction is used once
        assertTrue(configuration.getTransactionStore().get(TX_ID).isEmpty());
        assertTrue(sessionStore.get(webContext, SESSION_TRANSACTION_ID).isEmpty());
    }

    @Test
    void testBrowserComesBackBeforeTheWalletAnswered() {
        storeTransaction();
        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, TX_ID);

        assertTrue(extractor.extract(new CallContext(webContext, sessionStore)).isEmpty());
    }

    @Test
    void testNoPendingTransaction() {
        val ctx = new CallContext(MockWebContext.create(), new MockSessionStore());
        assertTrue(extractor.extract(ctx).isEmpty());
    }

    @Test
    void testUnknownTransaction() {
        val webContext = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, "unknown");
        val ctx = new CallContext(webContext, new MockSessionStore());
        assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
    }
}
