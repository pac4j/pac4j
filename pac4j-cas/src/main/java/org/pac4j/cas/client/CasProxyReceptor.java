/*
  Copyright 2012 - 2014 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.cas.client;

import java.util.Timer;
import java.util.TimerTask;

import org.jasig.cas.client.proxy.CleanUpTimerTask;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Protocol;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the CAS proxy receptor.
 * <p />
 * The url of the proxy receptor must be defined through the {@link #setCallbackUrl(String)} method, it's the <code>proxyReceptorUrl</code>
 * concept of the Jasig CAS client.
 * <p />
 * The proxy granting tickets and associations are stored by default in a {@link ProxyGrantingTicketStorageImpl} class, which can be
 * overriden by using the {@link #setProxyGrantingTicketStorage(ProxyGrantingTicketStorage)} method.
 * <p />
 * By default, the tickets and associations are cleaned every minute. The <code>millisBetweenCleanUps</code> property can be defined through
 * the {@link #setMillisBetweenCleanUps(int)} method (0 means no cleanup, greater than 0 means a cleanup every
 * <code>millisBetweenCleanUps</code> milli-seconds).
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasProxyReceptor extends BaseClient<CasCredentials, CasProfile> {
    
    private static final Logger logger = LoggerFactory.getLogger(CasProxyReceptor.class);
    
    private ProxyGrantingTicketStorage proxyGrantingTicketStorage = new ProxyGrantingTicketStorageImpl();
    
    public static final String PARAM_PROXY_GRANTING_TICKET_IOU = "pgtIou";
    
    public static final String PARAM_PROXY_GRANTING_TICKET = "pgtId";
    
    private int millisBetweenCleanUps = 60000;
    
    private Timer timer;
    
    private TimerTask timerTask;
    
    @Override
    protected BaseClient<CasCredentials, CasProfile> newClient() {
        final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
        casProxyReceptor.setProxyGrantingTicketStorage(this.proxyGrantingTicketStorage);
        casProxyReceptor.setMillisBetweenCleanUps(this.millisBetweenCleanUps);
        return casProxyReceptor;
    }
    
    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
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
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected CasCredentials retrieveCredentials(final WebContext context) throws
        RequiresHttpAction {
        
        // like CommonUtils.readAndRespondToProxyReceptorRequest in CAS client
        final String proxyGrantingTicketIou = context.getRequestParameter(PARAM_PROXY_GRANTING_TICKET_IOU);
        logger.debug("proxyGrantingTicketIou : {}", proxyGrantingTicketIou);
        final String proxyGrantingTicket = context.getRequestParameter(PARAM_PROXY_GRANTING_TICKET);
        logger.debug("proxyGrantingTicket : {}", proxyGrantingTicket);
        
        if (CommonUtils.isBlank(proxyGrantingTicket) || CommonUtils.isBlank(proxyGrantingTicketIou)) {
            context.writeResponseContent("");
            final String message = "Missing proxyGrantingTicket or proxyGrantingTicketIou";
            logger.error(message);
            throw RequiresHttpAction.ok(message, context);
        }
        
        this.proxyGrantingTicketStorage.save(proxyGrantingTicketIou, proxyGrantingTicket);
        
        context.writeResponseContent("<?xml version=\"1.0\"?>");
        context.writeResponseContent("<casClient:proxySuccess xmlns:casClient=\"http://www.yale.edu/tp/casClient\" />");
        
        final String message = "No credential for CAS proxy receptor -> returns ok";
        logger.debug(message);
        throw RequiresHttpAction.ok(message, context);
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
                                     this.proxyGrantingTicketStorage, "millisBetweenCleanUps",
                                     this.millisBetweenCleanUps);
    }
    
    /**
     * {@inheritDoc}
     */
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        throw new TechnicalException("Not supported by the CAS proxy receptor");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected CasProfile retrieveUserProfile(final CasCredentials credentials, final WebContext context) {
        throw new TechnicalException("Not supported by the CAS proxy receptor");
    }
    
    @Override
    protected boolean isDirectRedirection() {
        return true;
    }
    
    @Override
    public Protocol getProtocol() {
        return Protocol.CAS;
    }
}
