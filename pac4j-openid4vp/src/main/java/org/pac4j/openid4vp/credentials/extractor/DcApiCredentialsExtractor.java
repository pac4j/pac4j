package org.pac4j.openid4vp.credentials.extractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;

import java.util.Optional;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.RESPONSE;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.SESSION_TRANSACTION_ID;

/**
 * Extracts a presentation returned through the digital credentials API.
 *
 * <p>A single leg, unlike the wallet invoked by a URL: the browser never leaves, so the answer comes back
 * from the page which received it from the API, on a request carrying the session. There is nothing to
 * dispatch and no transaction identifier to pass around in URLs.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class DcApiCredentialsExtractor implements CredentialsExtractor {

    private final OpenId4VpClient client;

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        val transactionId = sessionStore.get(webContext, SESSION_TRANSACTION_ID).map(Object::toString).orElse(null);
        if (transactionId == null) {
            LOGGER.debug("no pending OpenID4VP transaction in the session");
            return Optional.empty();
        }
        val response = webContext.getRequestParameter(RESPONSE).orElse(null);
        if (response == null) {
            LOGGER.debug("the page brings back no answer for the transaction: {}", transactionId);
            return Optional.empty();
        }
        val store = client.getConfiguration().getTransactionStore();
        val transaction = store.get(transactionId).orElse(null);
        if (transaction == null) {
            LOGGER.debug("the OpenID4VP transaction expired or was already consumed: {}", transactionId);
            sessionStore.set(webContext, SESSION_TRANSACTION_ID, null);
            return Optional.empty();
        }

        transaction.setRawResponse(response);
        // a transaction is used once
        store.remove(transactionId);
        sessionStore.set(webContext, SESSION_TRANSACTION_ID, null);
        LOGGER.debug("the page brings back the answer of the transaction: {} ({} bytes)", transactionId, response.length());
        return Optional.of(new VerifiablePresentationCredentials(transaction));
    }
}
