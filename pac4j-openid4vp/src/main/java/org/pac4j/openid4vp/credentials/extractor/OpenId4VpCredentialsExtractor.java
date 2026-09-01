package org.pac4j.openid4vp.credentials.extractor;

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

import java.util.Optional;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.REQUEST_OBJECT_CONTENT_TYPE;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.RESPONSE;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.SESSION_TRANSACTION_ID;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.VP_TRANSACTION_ID;

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
        // the wallet fetches the signed request object
        if (transactionId != null) {
            throw serveRequestObject(ctx, transactionId);
        }
        // the browser comes back: this is the only branch with a session
        return buildCredentials(ctx);
    }

    /**
     * <p>Serve the signed request object to the wallet.</p>
     *
     * @param ctx the context
     * @param transactionId the identifier of the pending transaction
     * @return the action to perform
     */
    protected HttpAction serveRequestObject(final CallContext ctx, final String transactionId) {
        val transaction = findTransaction(transactionId);
        transaction.setStatus(VpTransaction.Status.REQUEST_RETRIEVED);
        client.getConfiguration().getTransactionStore().set(transactionId, transaction);
        LOGGER.debug("the wallet fetches the request object of the transaction: {}", transactionId);
        ctx.webContext().setResponseContentType(REQUEST_OBJECT_CONTENT_TYPE);
        return new OkAction(transaction.getRequestObject());
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
