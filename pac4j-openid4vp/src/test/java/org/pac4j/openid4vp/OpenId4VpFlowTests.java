package org.pac4j.openid4vp;

import lombok.val;
import com.nimbusds.jose.util.JSONObjectUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;

import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.credentials.extractor.OpenId4VpCredentialsExtractor;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.verifier.CredentialVerifier;
import org.pac4j.openid4vp.verifier.VerifiedCredential;
import org.pac4j.openid4vp.transaction.PresentationStatus;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.wallet.WalletSimulator;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

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

    @TempDir
    private java.nio.file.Path directory;

    private OpenId4VpClient client;
    private OpenId4VpConfiguration configuration;

    @BeforeEach
    void setUp() throws Exception {
        configuration = new OpenId4VpConfiguration();
        configuration.setJwks(new JwksProperties(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.setClientId(CALLBACK_URL);
        configuration.setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\","
            + "\"meta\":{\"vct_values\":[\"urn:eudi:pid:1\"]}}]}");

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl(CALLBACK_URL);
    }


    @ParameterizedTest
    @CsvSource({"false, true", "true, true", "true, false"})
    void testTheWholePresentationFlow(final boolean useRedirectUri, final boolean saveRequest) throws Exception {
        configuration.setUseRedirectUri(useRedirectUri);
        installTestVerifier();
        val simulator = new WalletSimulator();

        // 1. the browser asks for a protected page and is handed the wallet URL
        val browserContext = MockWebContext.create();
        val browserCtx = new CallContext(browserContext, new MockSessionStore());
        if (saveRequest) {
            browserCtx.sessionStore().set(browserContext, Pac4jConstants.REQUESTED_URL, "https://app.example.org/protected");
        }
        assertEquals(PresentationStatus.EXPIRED, client.getPresentationStatus(browserCtx));
        val walletUrl = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browserCtx).get()).getLocation();
        // what a page polls while it shows the QR code
        assertEquals(PresentationStatus.PENDING, client.getPresentationStatus(browserCtx));

        val requestUri = simulator.readRequestUri(walletUrl);
        val transactionId = simulator.readParameter(requestUri, VP_TRANSACTION_ID);
        assertNotNull(transactionId);
        assertTrue(simulator.postsToRequestUri(walletUrl));

        // 2. the wallet posts its capabilities and a nonce to the request URI, on a request carrying no session at all,
        // and gets a request object built for it
        val walletNonce = simulator.generateWalletNonce();
        val fetch = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId);
        simulator.buildRequestUriPostParameters(walletNonce).forEach(fetch::addRequestParameter);
        val served = assertThrows(OkAction.class,
            () -> client.getCredentials(new CallContext(fetch, new MockSessionStore())));

        val request = simulator.readRequestObject(served.getContent(), walletNonce);
        assertEquals(ClientIdPrefix.DECENTRALIZED_IDENTIFIER.getValue() + ":" + CALLBACK_URL, request.getClientId());
        assertTrue(request.getResponseUri().contains(VP_TRANSACTION_ID + "=" + transactionId));

        // 3. the wallet answers, encrypted to the key the request object published
        val response = simulator.buildResponse(request, Map.of("pid", List.of(PRESENTATION)));
        val post = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId)
            .addRequestParameter(RESPONSE, response);
        val accepted = assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(post, new MockSessionStore())));
        if (useRedirectUri) {
            val redirectUri = (String) JSONObjectUtils.parse(accepted.getContent()).get("redirect_uri");
            assertEquals(client.computeFinalCallbackUrl(browserContext),
                redirectUri.substring(0, redirectUri.lastIndexOf("&response_code=")));
            assertNull(simulator.readParameter(redirectUri, VP_TRANSACTION_ID));
            browserContext.addRequestParameter("client_name", simulator.readParameter(redirectUri, "client_name"));
            browserContext.addRequestParameter("response_code", simulator.readParameter(redirectUri, "response_code"));
        } else {
            assertEquals("{}", accepted.getContent());
        }
        assertEquals(PresentationStatus.RECEIVED, client.getPresentationStatus(browserCtx));

        // 4. follow the wallet redirect to the regular callback: it saves the profile and restores the saved URL
        val config = new Config(client);
        config.setWebContextFactory(parameters -> browserContext);
        config.setSessionStoreFactory(parameters -> browserCtx.sessionStore());
        config.setProfileManagerFactory(ProfileManagerFactory.DEFAULT);
        config.setHttpActionAdapter((action, context) -> action);
        val result = new DefaultCallbackLogic().perform(config, "/", true, null, null);
        assertEquals(saveRequest ? "https://app.example.org/protected" : "/",
            assertInstanceOf(WithLocationAction.class, result).getLocation());
        val profile = new ProfileManager(browserContext, browserCtx.sessionStore()).getProfile().orElseThrow();
        assertNotNull(profile.getId());
        assertEquals(PRESENTATION, profile.getAttribute("presentation"));
        assertTrue(browserCtx.sessionStore().get(browserContext, Pac4jConstants.REQUESTED_URL).isEmpty());

        // and the transaction is consumed
        assertTrue(configuration.getTransactionStore().get(transactionId).isEmpty());
        assertEquals(PresentationStatus.EXPIRED, client.getPresentationStatus(browserCtx));
        assertTrue(client.getCredentials(browserCtx).isEmpty());
        // the presentation was verified once, when the wallet posted it: the browser took the result
        assertEquals(1, verifications.get());
    }

    @Test
    void testResponseCodeCannotAuthenticateAnotherSession() throws Exception {
        configuration.setUseRedirectUri(true);
        installTestVerifier();
        val ownerBrowser = MockWebContext.create();
        val owner = new CallContext(ownerBrowser, new MockSessionStore());
        val ownerCode = openAnsweredTransaction(owner);
        val ownerId = (String) owner.sessionStore().get(owner.webContext(), SESSION_TRANSACTION_ID).orElseThrow();

        // A copied return URL in a session which did not start a transaction cannot authenticate anyone.
        val emptyBrowser = MockWebContext.create().addRequestParameter(RESPONSE_CODE, ownerCode);
        val emptySession = new MockSessionStore();
        performCallback(new CallContext(emptyBrowser, emptySession));
        assertTrue(new ProfileManager(emptyBrowser, emptySession).getProfile().isEmpty());
        assertTrue(configuration.getTransactionStore().get(OpenId4VpCredentialsExtractor.responseKey(ownerId)).isPresent());

        // Even a session with its own answered transaction cannot claim the owner's response using that code.
        val otherBrowser = MockWebContext.create();
        val other = new CallContext(otherBrowser, new MockSessionStore());
        val otherCode = openAnsweredTransaction(other);
        val otherId = (String) other.sessionStore().get(otherBrowser, SESSION_TRANSACTION_ID).orElseThrow();
        otherBrowser.addRequestParameter(RESPONSE_CODE, ownerCode);
        assertThrows(OpenId4VpException.class, () -> performCallback(other));
        assertTrue(new ProfileManager(otherBrowser, other.sessionStore()).getProfile().isEmpty());
        assertTrue(configuration.getTransactionStore().get(OpenId4VpCredentialsExtractor.responseKey(ownerId)).isPresent());
        assertTrue(configuration.getTransactionStore().get(OpenId4VpCredentialsExtractor.responseKey(otherId)).isPresent());

        // Neither response was consumed by the failed attempt: each session still accepts only its own code.
        ownerBrowser.addRequestParameter(RESPONSE_CODE, ownerCode);
        performCallback(owner);
        assertTrue(new ProfileManager(owner.webContext(), owner.sessionStore()).getProfile().isPresent());
        otherBrowser.addRequestParameter(RESPONSE_CODE, otherCode);
        performCallback(other);
        assertTrue(new ProfileManager(otherBrowser, other.sessionStore()).getProfile().isPresent());
    }

    @Test
    void testResponseCodeCannotBeReusedAfterTheCallback() throws Exception {
        configuration.setUseRedirectUri(true);
        installTestVerifier();
        val browser = MockWebContext.create();
        val ctx = new CallContext(browser, new MockSessionStore());
        val responseCode = openAnsweredTransaction(ctx);
        val transactionId = (String) ctx.sessionStore().get(browser, SESSION_TRANSACTION_ID).orElseThrow();
        ctx.sessionStore().set(browser, Pac4jConstants.REQUESTED_URL, "https://app.example.org/protected");
        browser.addRequestParameter(RESPONSE_CODE, responseCode);

        val first = performCallback(ctx);
        assertEquals("https://app.example.org/protected", assertInstanceOf(WithLocationAction.class, first).getLocation());
        val profiles = new ProfileManager(browser, ctx.sessionStore());
        assertTrue(profiles.getProfile().isPresent());
        assertTrue(configuration.getTransactionStore().get(transactionId).isEmpty());
        assertTrue(configuration.getTransactionStore().get(OpenId4VpCredentialsExtractor.responseKey(transactionId)).isEmpty());
        assertTrue(ctx.sessionStore().get(browser, SESSION_TRANSACTION_ID).isEmpty());

        // Remove the authenticated profile so a replay cannot be mistaken for an already authenticated session.
        profiles.removeProfiles();
        val replay = performCallback(ctx);
        assertEquals("/", assertInstanceOf(WithLocationAction.class, replay).getLocation());
        assertTrue(profiles.getProfile().isEmpty());
        assertTrue(client.getCredentials(ctx).isEmpty());
        assertEquals(1, verifications.get());
    }

    private String openAnsweredTransaction(final CallContext browser) throws Exception {
        val simulator = new WalletSimulator();
        val walletUrl = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browser).orElseThrow()).getLocation();
        val transactionId = simulator.readParameter(simulator.readRequestUri(walletUrl), VP_TRANSACTION_ID);
        val fetch = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, transactionId);
        val served = assertThrows(OkAction.class,
            () -> client.getCredentials(new CallContext(fetch, new MockSessionStore())));
        val request = simulator.readRequestObject(served.getContent());
        val post = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId)
            .addRequestParameter(RESPONSE, simulator.buildResponse(request, Map.of("pid", List.of(PRESENTATION))));
        val accepted = assertThrows(OkAction.class,
            () -> client.getCredentials(new CallContext(post, new MockSessionStore())));
        val redirectUri = (String) JSONObjectUtils.parse(accepted.getContent()).get("redirect_uri");
        val responseCode = simulator.readParameter(redirectUri, RESPONSE_CODE);
        assertNotNull(responseCode);
        return responseCode;
    }

    private Object performCallback(final CallContext ctx) {
        val config = new Config(client);
        config.setWebContextFactory(parameters -> ctx.webContext());
        config.setSessionStoreFactory(parameters -> ctx.sessionStore());
        config.setProfileManagerFactory(ProfileManagerFactory.DEFAULT);
        config.setHttpActionAdapter((action, context) -> action);
        return new DefaultCallbackLogic().perform(config, "/", true, null, null);
    }

    @Test
    void testTheWholeFlowInClearUpToTheVpToken() {
        // the same flow with direct_post: the wallet posts its vp_token as it is, and the authenticator reads it
        configuration.setResponseMode(ResponseMode.DIRECT_POST);
        installTestVerifier();
        val simulator = new WalletSimulator();
        val browserContext = MockWebContext.create();
        val browserCtx = new CallContext(browserContext, new MockSessionStore());
        val walletUrl = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browserCtx).get()).getLocation();
        val transactionId = simulator.readParameter(simulator.readRequestUri(walletUrl), VP_TRANSACTION_ID);

        val fetch = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, transactionId);
        val served = assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(fetch, new MockSessionStore())));
        val request = simulator.readRequestObject(served.getContent());
        assertNull(request.getEncryptionKey());
        assertEquals("direct_post", request.getResponseMode());

        val post = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId);
        simulator.buildResponseParameters(request, Map.of("pid", List.of(PRESENTATION))).forEach(post::addRequestParameter);
        assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(post, new MockSessionStore())));

        val credentials = assertInstanceOf(VerifiablePresentationCredentials.class, client.getCredentials(browserCtx).get());
        val validated = assertInstanceOf(VerifiablePresentationCredentials.class,
            client.validateCredentials(browserCtx, credentials).get());
        assertEquals(Map.of("pid", List.of(PRESENTATION)), validated.getVpToken());
    }

    @Test
    void testAResponseWithoutTheStateIsRejected() {
        // without holder binding, no nonce comes back: only the state binds the posted vp_token to the request
        configuration.setResponseMode(ResponseMode.DIRECT_POST);
        configuration.getDcqlQuery().getCredentials().get(0).setRequireCryptographicHolderBinding(false);
        installTestVerifier();
        val simulator = new WalletSimulator();
        val browserCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        val walletUrl = assertInstanceOf(FoundAction.class, client.getRedirectionAction(browserCtx).get()).getLocation();
        val transactionId = simulator.readParameter(simulator.readRequestUri(walletUrl), VP_TRANSACTION_ID);

        val fetch = MockWebContext.create().addRequestParameter(VP_TRANSACTION_ID, transactionId);
        val served = assertThrows(OkAction.class, () -> client.getCredentials(new CallContext(fetch, new MockSessionStore())));
        assertNotNull(simulator.readRequestObject(served.getContent()).getState());

        val post = MockWebContext.create()
            .setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(VP_TRANSACTION_ID, transactionId)
            .addRequestParameter(VP_TOKEN, "{\"pid\":[\"" + PRESENTATION + "\"]}");
        // refused as soon as it is posted, with a 400: the transaction stays open, and the browser gets nothing
        val refused = assertThrows(BadRequestAction.class,
            () -> client.getCredentials(new CallContext(post, new MockSessionStore())));
        assertEquals("the response state does not match the request", refused.getCause().getMessage());
        assertTrue(client.getCredentials(browserCtx).isEmpty());
        assertTrue(configuration.getTransactionStore().get(transactionId).isPresent());
        assertEquals(PresentationStatus.PENDING, client.getPresentationStatus(browserCtx));
    }

    private final AtomicInteger verifications = new AtomicInteger();

    private void installTestVerifier() {
        configuration.getCredentialVerifiers().put(CredentialFormat.SD_JWT_VC, new CredentialVerifier() {
            @Override
            public CredentialFormat getFormat() {
                return CredentialFormat.SD_JWT_VC;
            }

            @Override
            public VerifiedCredential verify(final String rawCredential, final VpTransaction transaction,
                                             final OpenId4VpConfiguration configuration) {
                // nothing is verified here: the presentation is only expected to reach this point
                verifications.incrementAndGet();
                return new VerifiedCredential().setFormat(getFormat()).setType("urn:eudi:pid:1").setCryptographicHolderBinding(true)
                    .setIssuer("https://issuer.example.org")
                    .setClaims(Map.of("sub", "alice", "presentation", rawCredential));
            }
        });
    }

    @Test
    void testTheBrowserGetsNothingBeforeTheWalletAnswered() {
        val browserCtx = new CallContext(MockWebContext.create(), new MockSessionStore());
        client.getRedirectionAction(browserCtx);

        assertTrue(client.getCredentials(browserCtx).isEmpty());
    }
}
