package org.pac4j.cas.client;

import java.util.Timer;
import java.util.TimerTask;

import org.jasig.cas.client.proxy.CleanUpTimerTask;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.IndirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the CAS proxy receptor.</p>
 * <p>The url of the proxy receptor must be defined through the {@link #setCallbackUrl(String)} method, it's the <code>proxyReceptorUrl</code>
 * concept of the Jasig CAS client.</p>
 * <p>The proxy granting tickets and associations are stored by default in a {@link ProxyGrantingTicketStorageImpl} class, which can be
 * overriden by using the {@link #setProxyGrantingTicketStorage(ProxyGrantingTicketStorage)} method.</p>
 * <p>By default, the tickets and associations are cleaned every minute. The <code>millisBetweenCleanUps</code> property can be defined through
 * the {@link #setMillisBetweenCleanUps(int)} method (0 means no cleanup, greater than 0 means a cleanup every
 * <code>millisBetweenCleanUps</code> milli-seconds).</p>
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasProxyReceptor extends IndirectClientV2<TokenCredentials, CasProfile> {
    
    private static final Logger logger = LoggerFactory.getLogger(CasProxyReceptor.class);
    
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage = new ProxyGrantingTicketStorageImpl();
    
    public static final String PARAM_PROXY_GRANTING_TICKET_IOU = "pgtIou";
    
    public static final String PARAM_PROXY_GRANTING_TICKET = "pgtId";
    
    private int millisBetweenCleanUps = 60000;
    
    private Timer timer;
    
    private TimerTask timerTask;

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);

        CommonHelper.assertNotNull("proxyGrantingTicketStorage", this.proxyGrantingTicketStorage);
        // timer to clean proxyGrantingTicketStorage
        if (this.millisBetweenCleanUps > 0) {
            if (this.timer == null) {
                this.timer = new Timer(true);
            }
            
            if (this.timerTask == null) {
                this.timerTask = new CleanUpTimerTask(this.proxyGrantingTicketStorage);
            }
            this.timer.schedule(this.timerTask, this.millisBetweenCleanUps, this.millisBetweenCleanUps);
        }

        setRedirectActionBuilder(ctx -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
        setCredentialsExtractor(ctx -> {
            // like CommonUtils.readAndRespondToProxyReceptorRequest in CAS client
            final String proxyGrantingTicketIou = ctx.getRequestParameter(PARAM_PROXY_GRANTING_TICKET_IOU);
            logger.debug("proxyGrantingTicketIou: {}", proxyGrantingTicketIou);
            final String proxyGrantingTicket = ctx.getRequestParameter(PARAM_PROXY_GRANTING_TICKET);
            logger.debug("proxyGrantingTicket: {}", proxyGrantingTicket);

            if (CommonUtils.isBlank(proxyGrantingTicket) || CommonUtils.isBlank(proxyGrantingTicketIou)) {
                ctx.writeResponseContent("");
                final String message = "Missing proxyGrantingTicket or proxyGrantingTicketIou";
                throw HttpAction.ok(message, ctx);
            }

            this.proxyGrantingTicketStorage.save(proxyGrantingTicketIou, proxyGrantingTicket);

            ctx.writeResponseContent("<?xml version=\"1.0\"?>");
            ctx.writeResponseContent("<casClient:proxySuccess xmlns:casClient=\"http://www.yale.edu/tp/casClient\" />");

            final String message = "No credential for CAS proxy receptor -> returns ok";
            logger.debug(message);
            throw HttpAction.ok(message, ctx);
        });
        setAuthenticator((credentials, ctx) -> { throw new TechnicalException("Not supported by the CAS proxy receptor"); });
    }

    public ProxyGrantingTicketStorage getProxyGrantingTicketStorage() {
        return this.proxyGrantingTicketStorage;
    }
    
    public void setProxyGrantingTicketStorage(final ProxyGrantingTicketStorage proxyGrantingTicketStorage) {
        this.proxyGrantingTicketStorage = proxyGrantingTicketStorage;
    }
    
    public int getMillisBetweenCleanUps() {
        return this.millisBetweenCleanUps;
    }
    
    public void setMillisBetweenCleanUps(final int millisBetweenCleanUps) {
        this.millisBetweenCleanUps = millisBetweenCleanUps;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "proxyGrantingTicketStorage",
                this.proxyGrantingTicketStorage, "millisBetweenCleanUps", this.millisBetweenCleanUps);
    }
}
