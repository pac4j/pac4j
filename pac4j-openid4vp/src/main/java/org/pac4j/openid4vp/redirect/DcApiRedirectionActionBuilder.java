package org.pac4j.openid4vp.redirect;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.openid4vp.client.OpenId4VpClient;

import java.util.Map;
import java.util.Optional;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.REQUEST;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.SESSION_TRANSACTION_ID;

/**
 * Hands the request object to the page, which passes it to the digital credentials API of the browser.
 *
 * <p>Nothing is redirected: the browser never leaves, so the request object is returned as the JSON the API
 * expects, for the page to place in its {@code navigator.credentials.get} call. The signed request object
 * goes in the {@code request} member, which is the shape of the {@code openid4vp-v1-signed} protocol.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Slf4j
public class DcApiRedirectionActionBuilder extends OpenId4VpRedirectionActionBuilder {

    /**
     * <p>Constructor for DcApiRedirectionActionBuilder.</p>
     *
     * @param client a {@link OpenId4VpClient} object
     */
    public DcApiRedirectionActionBuilder(final OpenId4VpClient client) {
        super(client);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val configuration = client.getConfiguration();
        val transaction = createTransaction(ctx);
        configuration.getTransactionStore().set(transaction.getId(), transaction);
        ctx.sessionStore().set(ctx.webContext(), SESSION_TRANSACTION_ID, transaction.getId());

        val requestObject = client.getRequestObjectBuilder().build(ctx, transaction);
        LOGGER.debug("transaction {} opened, handing the request object over to the page", transaction.getId());
        LOGGER.trace("request object of the transaction {}: {}", transaction.getId(), requestObject);

        ctx.webContext().setResponseContentType("application/json");
        return Optional.of(new OkAction(JSONObjectUtils.toJSONString(Map.of(REQUEST, requestObject))));
    }
}
