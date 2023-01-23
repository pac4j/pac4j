package org.pac4j.cas.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.Pac4jConstants;

import java.util.concurrent.TimeUnit;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This class is the CAS proxy receptor.</p>
 *
 * <p>The url of the proxy receptor is defined via the <code>setCallbackUrl(String)</code> method, it's the <code>proxyReceptorUrl</code>
 * concept of the Jasig CAS client.</p>
 *
 * <p>The proxy granting tickets and associations are stored by default in a {@link Store} class, which can be overridden by using the
 * <code>setStore(Store)</code> method.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public final class CasProxyReceptor extends IndirectClient {

    public static final String PARAM_PROXY_GRANTING_TICKET_IOU = "pgtIou";

    public static final String PARAM_PROXY_GRANTING_TICKET = "pgtId";

    private Store<String, String> store = new GuavaStore<>(1000, 1, TimeUnit.MINUTES);

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("store", this.store);

        setRedirectionActionBuilderIfUndefined(ctx
            -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
        setCredentialsExtractorIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            // like CommonUtils.readAndRespondToProxyReceptorRequest in CAS client
            val proxyGrantingTicketIou = webContext.getRequestParameter(PARAM_PROXY_GRANTING_TICKET_IOU);
            logger.debug("proxyGrantingTicketIou: {}", proxyGrantingTicketIou);
            val proxyGrantingTicket = webContext.getRequestParameter(PARAM_PROXY_GRANTING_TICKET);
            logger.debug("proxyGrantingTicket: {}", proxyGrantingTicket);

            if (!proxyGrantingTicket.isPresent() || !proxyGrantingTicketIou.isPresent()) {
                logger.warn("Missing proxyGrantingTicket or proxyGrantingTicketIou -> returns ok");
                throw new OkAction(Pac4jConstants.EMPTY_STRING);
            }

            this.store.set(proxyGrantingTicketIou.get(), proxyGrantingTicket.get());

            logger.debug("Found pgtIou and pgtId for CAS proxy receptor -> returns ok");
            throw new OkAction("<?xml version=\"1.0\"?>\n<casClient:proxySuccess xmlns:casClient=\"http://www.yale.edu/tp/casClient\" />");
        });
        setAuthenticatorIfUndefined((ctx, credentials)
            -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
    }
}
