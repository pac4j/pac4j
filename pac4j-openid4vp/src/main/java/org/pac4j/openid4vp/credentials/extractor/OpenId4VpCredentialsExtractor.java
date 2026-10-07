package org.pac4j.openid4vp.credentials.extractor;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.http.BadRequestAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.util.ProtocolMessages;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.util.OpenId4VpProtocolMessages;

import java.text.ParseException;
import java.util.Optional;
import java.util.function.Supplier;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Extracts an OpenID4VP presentation, dispatching the three kinds of requests reaching the callback endpoint.
 *
 * <p>In the cross device flow, the wallet talks to the application on a channel which carries no session:
 * it fetches the request object, then posts its response. Rather than exposing two more endpoints, which
 * would mean changing every framework integration, both legs go through the regular pac4j callback and are
 * dispatched here. They are answered by throwing an {@link HttpAction}: as it extends
 * {@code TechnicalException}, the callback logic catches it and hands it straight to the HTTP action
 * adapter, so no profile is ever created for those two legs.</p>
 *
 * <p>Only the third branch, the browser coming back, runs the regular pac4j chain.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class OpenId4VpCredentialsExtractor implements CredentialsExtractor {

    /** Appended to the transaction identifier to form the key the answer of the wallet is stored under. */
    public static final String RESPONSE_KEY_SUFFIX = "#response";

    private final OpenId4VpClient client;

    /**
     * <p>The key the answer of the wallet is stored under, apart from its transaction.</p>
     *
     * @param transactionId the identifier of the transaction
     * @return the key of its answer
     */
    public static String responseKey(final String transactionId) {
        return transactionId + RESPONSE_KEY_SUFFIX;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        val transactionId = webContext.getRequestParameter(VP_TRANSACTION_ID).orElse(null);
        val post = HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(webContext.getRequestMethod());

        // the wallet posts its response: encrypted, in clear, or an error
        if (post && WalletResponseReader.carriesAnswer(webContext)) {
            OpenId4VpProtocolMessages.received(transactionId, OpenId4VpProtocolMessages.WALLET, webContext);
            throw answerWallet(transactionId, () -> acceptWalletResponse(ctx, transactionId));
        }
        // the wallet fetches the signed request object, having posted its capabilities first or not
        if (transactionId != null) {
            OpenId4VpProtocolMessages.received(transactionId, OpenId4VpProtocolMessages.WALLET, webContext);
            throw answerWallet(transactionId, () -> serveRequestObject(ctx, transactionId, post));
        }
        // the browser comes back: this is the only branch with a session
        return buildCredentials(ctx);
    }

    /**
     * <p>Answer a request of the wallet, a refused request being answered with a 400.</p>
     *
     * <p>The specification only fixes the answer to what the verifier accepts: "If the Response URI has successfully
     * processed the Authorization Response or Authorization Error Response, it MUST respond with an HTTP status code of
     * 200". A wallet posting an error is thus answered with a 200, like any processed response. Nothing is said about a
     * request the verifier refuses (no live transaction, second answer, presentation which does not validate...): the
     * fault lies with the request, hence a 400 rather than the 500 an exception reaching the framework would give. The
     * refusal is kept as the cause of the action. Any other failure, of the verifier itself, still propagates.</p>
     *
     * @param transactionId the identifier of the transaction, for the logs
     * @param leg the leg answering the wallet
     * @return the action to perform
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.2">
     *     OpenID4VP 1.0, response mode direct_post</a>
     */
    protected HttpAction answerWallet(final String transactionId, final Supplier<HttpAction> leg) {
        try {
            return leg.get();
        } catch (final OpenId4VpException e) {
            LOGGER.warn("wallet request refused for transaction {}: {}", ProtocolMessages.oneLine(transactionId),
                ProtocolMessages.oneLine(OpenId4VpException.reasons(e)));
            OpenId4VpProtocolMessages.sent(transactionId, OpenId4VpProtocolMessages.WALLET, "400");
            val action = new BadRequestAction();
            action.initCause(e);
            return action;
        }
    }

    /**
     * <p>Serve the signed request object to the wallet. On a POST, the wallet first says what it supports
     * and hands over a nonce: the request object carries the nonce back, and publishes only the credential
     * formats and the encryption algorithms the wallet declared.</p>
     *
     * @param ctx the context
     * @param transactionId the identifier of the pending transaction
     * @param post whether the wallet posted to the request URI rather than fetching it
     * @return the action to perform
     */
    protected HttpAction serveRequestObject(final CallContext ctx, final String transactionId, final boolean post) {
        val transaction = findTransaction(transactionId);
        // the request object is only served while the transaction awaits its answer: the transaction identifier
        // being visible in the wallet URL (thus in the QR code), a request the wallet already answered has no
        // reason to be read again, and the presentation it asks for must not be answered twice
        if (isAnswered(transactionId)) {
            throw new OpenId4VpException("the wallet already answered the transaction: " + transactionId);
        }
        if (post) {
            readWalletCapabilities(ctx, transaction);
        }
        val requestObject = client.getRequestObjectBuilder().build(ctx, transaction);
        val previousStatus = transaction.getStatus();
        transaction.setStatus(VpTransaction.Status.REQUEST_RETRIEVED);
        // only the transaction is rewritten: an answer posted meanwhile lives under its own key
        client.getConfiguration().getTransactionStore().set(transactionId, transaction);
        VpTransaction.logTransition(transactionId, previousStatus, transaction.getStatus(), post
            ? "POST to the request URI, wallet metadata " + (transaction.getWalletMetadata() != null ? "received" : "absent")
            : "GET of the request URI");
        OpenId4VpProtocolMessages.sent(transactionId, OpenId4VpProtocolMessages.WALLET, "200 " + requestObject);
        ctx.webContext().setResponseContentType(REQUEST_OBJECT_CONTENT_TYPE);
        return new OkAction(requestObject);
    }

    /**
     * <p>Keep what the wallet posted to the request URI: its metadata, and a nonce of its own. Both are
     * optional, and anything else is ignored, as the specification requires: "The Verifier MUST ignore any
     * unrecognized parameters".</p>
     *
     * @param ctx the context
     * @param transaction the transaction being answered
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#request_uri_method_post">
     *     OpenID4VP 1.0, request URI method post</a>
     */
    protected void readWalletCapabilities(final CallContext ctx, final VpTransaction transaction) {
        val webContext = ctx.webContext();
        webContext.getRequestParameter(WALLET_METADATA).ifPresent(metadata -> {
            try {
                JSONObjectUtils.parse(metadata);
            } catch (final ParseException e) {
                throw new OpenId4VpException("the wallet metadata is not a JSON object: " + e.getMessage(), e);
            }
            transaction.setWalletMetadata(metadata);
        });
        webContext.getRequestParameter(WALLET_NONCE).ifPresent(walletNonce -> {
            transaction.setWalletNonce(walletNonce);
        });
    }

    /**
     * <p>Store the response posted by the wallet, so that the browser can claim it.</p>
     *
     * <p>A response is validated before it is kept, and it is kept under its own key: the transaction itself is
     * never rewritten here, so that a request object served at the same time cannot erase the answer.</p>
     *
     * @param ctx the context
     * @param transactionId the identifier of the pending transaction
     * @return the action to perform
     */
    protected HttpAction acceptWalletResponse(final CallContext ctx, final String transactionId) {
        val transaction = findTransaction(transactionId).copy();
        val previousStatus = transaction.getStatus();
        checkAwaitsAnswer(transaction);
        WalletResponseReader.read(ctx.webContext(), transaction, client.getConfiguration().getResponseMode());
        if (transaction.getError() == null) {
            val validated = validateResponse(ctx, transaction);
            transaction.setValidatedVpToken(validated.getVpToken()).setVerifiedCredentials(validated.getVerifiedCredentials())
                .setUserProfile(validated.getUserProfile());
        }
        client.getConfiguration().getTransactionStore().set(responseKey(transactionId), transaction.toResponse());
        VpTransaction.logTransition(transactionId, previousStatus, transaction.getStatus(), transaction.getError() != null
            ? "error " + transaction.getError() : "presentation validated");
        // "it MUST respond with an HTTP status code of 200 with Content-Type of application/json and a JSON object in the
        // response body"; without a redirect_uri in it, "the Wallet is not required to perform any further steps"
        // https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.2
        ctx.webContext().setResponseContentType("application/json");
        OpenId4VpProtocolMessages.sent(transactionId, OpenId4VpProtocolMessages.WALLET, "200 {}");
        return new OkAction("{}");
    }

    /**
     * <p>Validate the answer as soon as the wallet posts it, before keeping it, with the authenticator of the
     * client: the very validation the browser leg runs again, against the same saved request.</p>
     *
     * <p>The response URI is not authenticated and the transaction identifier is visible in the wallet URL,
     * thus in the QR code of the cross device flow: whoever sees it can post to the response URI. An answer
     * which does not validate is refused here and leaves the transaction open, so that such a post cannot take
     * the place of the wallet's answer. This goes beyond what the specification asks: "The Verifier SHOULD
     * protect its Response URI from inadvertent requests by checking that the value of the received state
     * parameter corresponds to a recent Authorization Request".</p>
     *
     * <p>An error answer is not validated: nothing authenticates it, and it ends the transaction when the
     * browser claims it.</p>
     *
     * <p>The result is kept with the answer: the browser takes it as is, nothing being verified twice.</p>
     *
     * @param ctx the context
     * @param transaction the transaction, holding the answer just read
     * @return the validated credentials
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-14.3.2">
     *     OpenID4VP 1.0, protection of the response URI</a>
     */
    protected VerifiablePresentationCredentials validateResponse(final CallContext ctx, final VpTransaction transaction) {
        val credentials = new VerifiablePresentationCredentials(transaction);
        client.getAuthenticator().validate(ctx, credentials);
        return credentials;
    }

    /**
     * <p>Whether the wallet already answered the transaction.</p>
     *
     * @param transactionId the identifier of the transaction
     * @return whether an answer is stored for it
     */
    protected boolean isAnswered(final String transactionId) {
        return client.getConfiguration().getTransactionStore().get(responseKey(transactionId)).isPresent();
    }

    /**
     * <p>Check that the transaction can take the answer the wallet posts: one answer at most, and only once
     * the wallet has read the request, when it had to fetch it.</p>
     *
     * <p>The first valid answer is the one kept, so a later post cannot overwrite what the wallet said; and
     * with a signed request, served by reference, an answer to a request nobody fetched can only be forged,
     * since the nonce to bind it to lives in that request. A request passed by value in the wallet URL, the
     * case of the {@code redirect_uri} prefix, is never fetched: its transaction stays created until it is
     * answered.</p>
     *
     * @param transaction the transaction the wallet answers
     * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#session_fixation">
     *     OpenID4VP 1.0, session fixation</a>
     */
    protected void checkAwaitsAnswer(final VpTransaction transaction) {
        val status = transaction.getStatus();
        if (isAnswered(transaction.getId())) {
            throw new OpenId4VpException("the transaction was already answered: " + transaction.getId());
        }
        if (status == VpTransaction.Status.CREATED && client.getConfiguration().getClientIdPrefix().isSignedRequest()) {
            throw new OpenId4VpException("the wallet never fetched the request object of the transaction: "
                + transaction.getId());
        }
    }

    /**
     * <p>Build the credentials when the browser comes back, the wallet having answered.</p>
     *
     * @param ctx the context
     * @return the credentials (optional)
     */
    protected Optional<Credentials> buildCredentials(final CallContext ctx) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();
        val transactionId = sessionStore.get(webContext, SESSION_TRANSACTION_ID).map(Object::toString).orElse(null);
        if (transactionId == null) {
            LOGGER.debug("no pending OpenID4VP transaction in the session");
            return Optional.empty();
        }
        val store = client.getConfiguration().getTransactionStore();
        val transaction = store.get(transactionId).orElse(null);
        if (transaction == null) {
            LOGGER.debug("the OpenID4VP transaction expired or was already consumed: {}", transactionId);
            sessionStore.set(webContext, SESSION_TRANSACTION_ID, null);
            return Optional.empty();
        }
        val response = store.get(responseKey(transactionId)).orElse(null);
        if (response == null) {
            LOGGER.debug("the wallet has not answered the transaction yet: {}", transactionId);
            return Optional.empty();
        }
        transaction.withResponse(response);
        // a transaction is used once
        store.remove(responseKey(transactionId));
        store.remove(transactionId);
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, null);
        VpTransaction.logTransition(transactionId, transaction.getStatus(), VpTransaction.CONSUMED, "the browser came back");
        if (transaction.getError() != null) {
            throw new OpenId4VpException(WalletResponseReader.refusalMessage(transaction));
        }
        return Optional.of(new VerifiablePresentationCredentials(transaction));
    }

    /**
     * <p>Find a live transaction, or fail.</p>
     *
     * @param transactionId the identifier of the transaction
     * @return the transaction
     */
    protected VpTransaction findTransaction(final String transactionId) {
        if (transactionId == null) {
            throw new OpenId4VpException("no " + VP_TRANSACTION_ID + " parameter on the wallet request");
        }
        return client.getConfiguration().getTransactionStore().get(transactionId)
            .orElseThrow(() -> new OpenId4VpException("no live OpenID4VP transaction: " + transactionId));
    }
}
