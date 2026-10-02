package org.pac4j.openid4vp.credentials.extractor;

import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.test.context.MockWebContext;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.openid4vp.wallet.WalletSimulator;
import org.pac4j.test.context.session.MockSessionStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
    private static final String WALLET_RESPONSE = "eyJhbGciOiJFQ0RILUVTIn0.encrypted.response";

    @TempDir
    private java.nio.file.Path directory;

    private OpenId4VpConfiguration configuration;
    private OpenId4VpClient client;
    private OpenId4VpCredentialsExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = buildExtractor(ResponseMode.DIRECT_POST_JWT);
    }

    private OpenId4VpCredentialsExtractor buildExtractor(final ResponseMode responseMode) {
        configuration = new OpenId4VpConfiguration();
        configuration.setResponseMode(responseMode);
        configuration.setClientId("https://app.example.org/callback");
        configuration.setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\","
            + "\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]}}]}");
        configuration.setClientIdPrefix(org.pac4j.openid4vp.config.ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setJwks(new JwksProperties(directory.resolve("keys.jwks").toString()).setKid("key-1"));

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl("https://app.example.org/callback");
        client.init();
        return new OpenId4VpCredentialsExtractor(client);
    }

    private VpTransaction storeTransaction() {
        val transaction = new VpTransaction()
            .setId(TX_ID)
            .setNonce("nonce")
            .setCreatedAt(Instant.now())
            .setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        configuration.getTransactionStore().set(TX_ID, transaction);
        return transaction;
    }

    /** Let every answer through the validation run at reception: these tests are about where it is kept. */
    private void acceptAnyAnswer() {
        client.setAuthenticator((ctx, credentials) -> Optional.of(credentials));
    }

    private VpTransaction storedAnswer() {
        return configuration.getTransactionStore().get(OpenId4VpCredentialsExtractor.responseKey(TX_ID)).orElse(null);
    }

    private void storeAnswer(final String rawResponse) {
        configuration.getTransactionStore().set(OpenId4VpCredentialsExtractor.responseKey(TX_ID),
            configuration.getTransactionStore().get(TX_ID).get().copy().setRawResponse(rawResponse).toResponse());
    }

    /** A transaction the wallet has fetched the request object of, so that it can answer it. */
    private VpTransaction storeFetchedTransaction() {
        return storeTransaction().setStatus(VpTransaction.Status.REQUEST_RETRIEVED);
    }

    @Test
    void testWalletFetchesTheRequestObject() throws Exception {
        storeTransaction();
        val webContext = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, TX_ID);
        val ctx = new CallContext(webContext, new MockSessionStore());

        val action = assertThrows(OkAction.class, () -> extractor.extract(ctx));
        // the request object is built on demand, for this very transaction
        val requestObject = SignedJWT.parse(action.getContent());
        assertEquals(REQUEST_OBJECT_TYPE, requestObject.getHeader().getType().toString());
        assertEquals("nonce", requestObject.getJWTClaimsSet().getStringClaim(NONCE));
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED,
            configuration.getTransactionStore().get(TX_ID).get().getStatus());
    }

    @Test
    void testWalletPostsItsCapabilitiesThenGetsARequestObjectFitForIt() throws Exception {
        storeTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(WALLET_METADATA, WalletSimulator.WALLET_METADATA_JSON)
            .addRequestParameter(WALLET_NONCE, "wallet-nonce");
        val ctx = new CallContext(webContext, new MockSessionStore());

        val action = assertThrows(OkAction.class, () -> extractor.extract(ctx));
        // the request object carries the wallet nonce back, and the transaction keeps what the wallet said
        val claims = SignedJWT.parse(action.getContent()).getJWTClaimsSet();
        assertEquals("wallet-nonce", claims.getStringClaim(WALLET_NONCE));
        val transaction = configuration.getTransactionStore().get(TX_ID).get();
        assertEquals("wallet-nonce", transaction.getWalletNonce());
        assertEquals(WalletSimulator.WALLET_METADATA_JSON, transaction.getWalletMetadata());
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED, transaction.getStatus());
    }

    @Test
    void testARequestObjectFetchedWithAGetCarriesNoWalletNonce() throws Exception {
        storeTransaction();
        val webContext = MockWebContext.create()
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(WALLET_NONCE, "ignored-on-a-get");
        val ctx = new CallContext(webContext, new MockSessionStore());

        val action = assertThrows(OkAction.class, () -> extractor.extract(ctx));
        assertNull(SignedJWT.parse(action.getContent()).getJWTClaimsSet().getStringClaim(WALLET_NONCE));
        assertNull(configuration.getTransactionStore().get(TX_ID).get().getWalletNonce());
    }

    @Test
    void testMalformedWalletMetadataIsRefused() {
        storeTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(WALLET_METADATA, "not-json");
        val ctx = new CallContext(webContext, new MockSessionStore());

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertTrue(e.getMessage().startsWith("the wallet metadata is not a JSON object"));
    }

    @Test
    void testWalletPostsItsResponseInClearWhenAsked() {
        val clearExtractor = buildExtractor(ResponseMode.DIRECT_POST);
        acceptAnyAnswer();
        storeFetchedTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(VP_TOKEN, "{\"pid\":[\"a-presentation\"]}");
        val ctx = new CallContext(webContext, new MockSessionStore());

        assertThrows(OkAction.class, () -> clearExtractor.extract(ctx));

        val answer = storedAnswer();
        assertEquals("{\"pid\":[\"a-presentation\"]}", answer.getRawVpToken());
        assertNull(answer.getRawResponse());
        assertEquals(VpTransaction.Status.RESPONSE_RECEIVED, answer.getStatus());
        // the transaction itself is left as it was
        assertNull(configuration.getTransactionStore().get(TX_ID).get().getRawVpToken());
    }

    @Test
    void testAClearAnswerToAnEncryptedRequestIsRefused() {
        storeFetchedTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(VP_TOKEN, "{\"pid\":[\"a-presentation\"]}");
        val ctx = new CallContext(webContext, new MockSessionStore());

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertTrue(e.getMessage().startsWith("the wallet answered in clear a request asking for an encrypted response (direct_post.jwt)"));
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED, configuration.getTransactionStore().get(TX_ID).get().getStatus());
        assertNull(storedAnswer());
    }

    @Test
    void testWalletAnswersWithAnError() {
        storeFetchedTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(ERROR, "access_denied")
            .addRequestParameter(ERROR_DESCRIPTION, "the End-User did not consent");
        assertThrows(OkAction.class, () -> extractor.extract(new CallContext(webContext, new MockSessionStore())));
        // an error is not validated: nothing authenticates it
        val answer = storedAnswer();
        assertEquals("access_denied", answer.getError());
        assertTrue(answer.isAnswered());

        // the browser comes back: the refusal surfaces, and the transaction is consumed
        val browser = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(browser, SESSION_TRANSACTION_ID, TX_ID);
        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(new CallContext(browser, sessionStore)));
        assertEquals("the wallet refused the presentation of the transaction tx-1: access_denied (the End-User did not consent)",
            e.getMessage());
        assertTrue(configuration.getTransactionStore().get(TX_ID).isEmpty());
        assertNull(storedAnswer());
    }

    @Test
    void testWalletPostsItsResponse() {
        acceptAnyAnswer();
        storeFetchedTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, WALLET_RESPONSE);
        val ctx = new CallContext(webContext, new MockSessionStore());

        assertThrows(OkAction.class, () -> extractor.extract(ctx));

        assertEquals(WALLET_RESPONSE, storedAnswer().getRawResponse());
        assertEquals(VpTransaction.Status.RESPONSE_RECEIVED, storedAnswer().getStatus());
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED, configuration.getTransactionStore().get(TX_ID).get().getStatus());
    }

    @Test
    void testAnInvalidAnswerIsRefusedAndLeavesTheTransactionOpen() {
        // whoever saw the QR code can post: an answer which does not validate must not take the wallet's place
        storeFetchedTransaction();
        val forged = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, WALLET_RESPONSE);
        assertThrows(OpenId4VpException.class, () -> extractor.extract(new CallContext(forged, new MockSessionStore())));
        assertNull(storedAnswer());
        val transaction = configuration.getTransactionStore().get(TX_ID).get();
        assertNull(transaction.getRawResponse());
        assertEquals(VpTransaction.Status.REQUEST_RETRIEVED, transaction.getStatus());

        // the wallet's answer is still taken
        acceptAnyAnswer();
        val genuine = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, "the.wallet.response");
        assertThrows(OkAction.class, () -> extractor.extract(new CallContext(genuine, new MockSessionStore())));
        assertEquals("the.wallet.response", storedAnswer().getRawResponse());
    }

    @Test
    void testARequestObjectServedMeanwhileCannotEraseTheAnswer() {
        // the race: a request object fetch reads the transaction, the wallet's answer is stored, then the fetch
        // writes back its stale copy of the transaction
        val stale = storeFetchedTransaction().copy();
        storeAnswer(WALLET_RESPONSE);
        configuration.getTransactionStore().set(TX_ID, stale);

        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, TX_ID);
        val credentials = (VerifiablePresentationCredentials) extractor.extract(new CallContext(webContext, sessionStore)).get();
        assertEquals(WALLET_RESPONSE, credentials.getTransaction().getRawResponse());
    }

    @Test
    void testASecondAnswerIsRefused() {
        storeFetchedTransaction();
        storeAnswer(WALLET_RESPONSE);
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, "another.encrypted.response");
        val ctx = new CallContext(webContext, new MockSessionStore());

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertEquals("the transaction was already answered: tx-1", e.getMessage());
        // the first answer is the one kept
        assertEquals(WALLET_RESPONSE, storedAnswer().getRawResponse());
    }

    @Test
    void testAnAnswerToARequestNobodyFetchedIsRefused() {
        storeTransaction();
        val webContext = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, TX_ID)
            .addRequestParameter(RESPONSE, WALLET_RESPONSE);
        val ctx = new CallContext(webContext, new MockSessionStore());

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertEquals("the wallet never fetched the request object of the transaction: tx-1", e.getMessage());
        val transaction = configuration.getTransactionStore().get(TX_ID).get();
        assertEquals(VpTransaction.Status.CREATED, transaction.getStatus());
        assertFalse(transaction.isAnswered());
        assertNull(storedAnswer());
    }

    @Test
    void testTheRequestObjectIsNotServedOnceAnswered() {
        storeFetchedTransaction();
        storeAnswer(WALLET_RESPONSE);
        val webContext = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, TX_ID);
        val ctx = new CallContext(webContext, new MockSessionStore());

        val e = assertThrows(OpenId4VpException.class, () -> extractor.extract(ctx));
        assertEquals("the wallet already answered the transaction: tx-1", e.getMessage());
        assertEquals(WALLET_RESPONSE, storedAnswer().getRawResponse());
    }

    @Test
    void testBrowserComesBackOnceTheWalletAnswered() {
        storeTransaction();
        storeAnswer(WALLET_RESPONSE);
        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, TX_ID);
        val ctx = new CallContext(webContext, sessionStore);

        val credentials = (VerifiablePresentationCredentials) extractor.extract(ctx).get();
        assertEquals(TX_ID, credentials.getTransaction().getId());
        assertEquals(WALLET_RESPONSE, credentials.getTransaction().getRawResponse());
        // a transaction is used once
        assertTrue(configuration.getTransactionStore().get(TX_ID).isEmpty());
        assertNull(storedAnswer());
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
