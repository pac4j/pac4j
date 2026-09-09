package org.pac4j.openid4vp.credentials.extractor;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.text.ParseException;
import java.util.Optional;

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

    private final OpenId4VpClient client;

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        val transactionId = webContext.getRequestParameter(VP_TRANSACTION_ID).orElse(null);
        val post = HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(webContext.getRequestMethod());

        // the wallet posts its response
        if (post && webContext.getRequestParameter(RESPONSE).isPresent()) {
            throw acceptWalletResponse(ctx, transactionId);
        }
        // the wallet fetches the signed request object, having posted its capabilities first or not
        if (transactionId != null) {
            throw serveRequestObject(ctx, transactionId, post);
        }
        // the browser comes back: this is the only branch with a session
        return buildCredentials(ctx);
    }

    /**
     * <p>Serve the signed request object to the wallet. On a POST, the wallet first says what it supports
     * and hands over a nonce, and the request object is built accordingly.</p>
     *
     * @param ctx the context
     * @param transactionId the identifier of the pending transaction
     * @param post whether the wallet posted to the request URI rather than fetching it
     * @return the action to perform
     */
    protected HttpAction serveRequestObject(final CallContext ctx, final String transactionId, final boolean post) {
        val transaction = findTransaction(transactionId);
        if (post) {
            readWalletCapabilities(ctx, transaction);
        }
        transaction.setStatus(VpTransaction.Status.REQUEST_RETRIEVED);
        client.getConfiguration().getTransactionStore().set(transactionId, transaction);
        LOGGER.debug("the wallet fetches the request object of the transaction: {}", transactionId);
        val requestObject = client.getRequestObjectBuilder().build(ctx, transaction);
        LOGGER.trace("request object of the transaction {}: {}", transactionId, requestObject);
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
            LOGGER.debug("the wallet posted its metadata for the transaction {}: {}", transaction.getId(), metadata);
        });
        webContext.getRequestParameter(WALLET_NONCE).ifPresent(walletNonce -> {
            transaction.setWalletNonce(walletNonce);
            LOGGER.debug("the wallet posted its nonce for the transaction {}: {}", transaction.getId(), walletNonce);
        });
    }

    /**
     * <p>Store the response posted by the wallet, so that the browser can claim it.</p>
     *
     * @param ctx the context
     * @param transactionId the identifier of the pending transaction
     * @return the action to perform
     */
    protected HttpAction acceptWalletResponse(final CallContext ctx, final String transactionId) {
        val transaction = findTransaction(transactionId);
        val response = ctx.webContext().getRequestParameter(RESPONSE)
            .orElseThrow(() -> new OpenId4VpException("no response posted by the wallet"));
        transaction.setRawResponse(response);
        transaction.setStatus(VpTransaction.Status.RESPONSE_RECEIVED);
        client.getConfiguration().getTransactionStore().set(transactionId, transaction);
        LOGGER.debug("the wallet posted its response for the transaction: {} ({} bytes)", transactionId, response.length());
        // TODO: answer the redirect_uri holding the response code, so that the wallet can hand the browser back
        return new OkAction("{}");
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
        if (transaction.getRawResponse() == null) {
            LOGGER.debug("the wallet has not answered the transaction yet: {}", transactionId);
            return Optional.empty();
        }
        // a transaction is used once
        store.remove(transactionId);
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, null);
        LOGGER.debug("the browser comes back with the response of the transaction: {}", transactionId);
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
