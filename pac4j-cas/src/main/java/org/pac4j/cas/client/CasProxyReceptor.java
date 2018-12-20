package org.pac4j.cas.client;

import java.util.concurrent.TimeUnit;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the CAS proxy receptor.</p>
 *
 * <p>The url of the proxy receptor is defined via the {@link #setCallbackUrl(String)} method, it's the <code>proxyReceptorUrl</code>
 * concept of the Jasig CAS client.</p>
 *
 * <p>The proxy granting tickets and associations are stored by default in a {@link Store} class, which can be overridden by using the
 * {@link #setStore(Store)} method.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasProxyReceptor extends IndirectClient<TokenCredentials> {

    public static final String PARAM_PROXY_GRANTING_TICKET_IOU = "pgtIou";

    public static final String PARAM_PROXY_GRANTING_TICKET = "pgtId";

    private static final Logger logger = LoggerFactory.getLogger(CasProxyReceptor.class);

    private Store<String, String> store = new GuavaStore<>(1000, 1, TimeUnit.MINUTES);

    @Override
    protected void clientInit() {
        assertNotNull("store", this.store);

        defaultRedirectionActionBuilder(ctx -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
        defaultCredentialsExtractor(ctx -> {
            // like CommonUtils.readAndRespondToProxyReceptorRequest in CAS client
            final String proxyGrantingTicketIou = ctx.getRequestParameter(PARAM_PROXY_GRANTING_TICKET_IOU);
            logger.debug("proxyGrantingTicketIou: {}", proxyGrantingTicketIou);
            final String proxyGrantingTicket = ctx.getRequestParameter(PARAM_PROXY_GRANTING_TICKET);
            logger.debug("proxyGrantingTicket: {}", proxyGrantingTicket);

            if (isBlank(proxyGrantingTicket) || isBlank(proxyGrantingTicketIou)) {
                logger.warn("Missing proxyGrantingTicket or proxyGrantingTicketIou -> returns ok");
                throw new OkAction("");
            }

            this.store.set(proxyGrantingTicketIou, proxyGrantingTicket);

            ctx.writeResponseContent("<?xml version=\"1.0\"?>");
            ctx.writeResponseContent("<casClient:proxySuccess xmlns:casClient=\"http://www.yale.edu/tp/casClient\" />");

            logger.debug("Found pgtIou and pgtId for CAS proxy receptor -> returns ok");
            throw new OkAction("");
        });
        defaultAuthenticator((credentials, ctx) -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
    }

    public Store<String, String> getStore() {
        return store;
    }

    public void setStore(final Store<String, String> store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "callbackUrl", this.callbackUrl, "store", this.store);
    }
}
