/*
  Copyright 2012 - 2015 pac4j organization

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

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.logout.CasSingleSignOutHandler;
import org.pac4j.cas.logout.LogoutHandler;
import org.pac4j.cas.logout.NoLogoutHandler;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the client to authenticate users on a CAS server.</p>
 * <p>The CAS service url is defined by the callback url which must be defined on the services back office of the CAS server.</p>
 * <p>The CAS server login url is defined by the {@link #setCasLoginUrl(String)} method and the CAS server prefix url is settable by the
 * {@link #setCasPrefixUrl(String)} method. If the CAS prefix url is not defined, the CAS prefix url is computed from the CAS server login
 * url and vice versa.</p>
 * <p>The CAS protocol is defined by the {@link #setCasProtocol(CasProtocol)} method and {@link CasProtocol} enumeration. It can be :</p>
 * <ul>
 * <li>CAS 1.0</li>
 * <li>CAS 2.0 : service tickets only (by default)</li>
 * <li>CAS 2.0 : service &amp; proxy tickets. In this case, it's possible to define if any proxy is accepted by using the
 * {@link #setAcceptAnyProxy(boolean)} method or the list of accepted proxies by using the {@link #setAllowedProxyChains(ProxyList)} method.
 * </li>
 * <li>SAML.</li>
 * </ul>
 * <p>For the CAS round-trip :</p>
 * <ul>
 * <li>the <code>renew</code> parameter can be set by using the {@link #setRenew(boolean)} method</li>
 * <li>the <code>gateway</code> parameter can be set by using the {@link #setGateway(boolean)} method.</li>
 * </ul>
 * <p>This client handles CAS logout calls from the CAS server, using the {@link LogoutHandler} interface. It's defined by default as the
 * {@link NoLogoutHandler} class, which does not perform the logout. Though, in J2E context, it can be defined to the
 * {@link CasSingleSignOutHandler} class by using the {@link #setLogoutHandler(LogoutHandler)} method. It must be used in association with
 * the CAS client listener : {@link SingleSignOutHttpSessionListener}.</p>
 * <p>To require a proxy granting ticket, the {@link CasProxyReceptor} class must be used and referenced in this class through the
 * {@link #setCasProxyReceptor(CasProxyReceptor)} method.</p>
 * <p>It returns a {@link org.pac4j.cas.profile.CasProfile} or a {@link org.pac4j.cas.profile.CasProxyProfile} if the
 * <code>casProxyReceptor</code> is defined (this CAS client acts as a proxy).</p>
 * 
 * @see org.pac4j.cas.profile.CasProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasClient extends IndirectClient<CasCredentials, CasProfile> {

    protected static final Logger logger = LoggerFactory.getLogger(CasClient.class);

    public enum CasProtocol {
        CAS10, CAS20, CAS20_PROXY, CAS30, CAS30_PROXY, SAML
    }

    protected static final String SERVICE_PARAMETER = "service";

    public static final String SERVICE_TICKET_PARAMETER = "ticket";

    protected LogoutHandler logoutHandler = new NoLogoutHandler();

    protected TicketValidator ticketValidator;

    protected String encoding = "UTF-8";

    protected String casLoginUrl;

    protected String casPrefixUrl;
    
    protected long timeTolerance = 1000L;

    protected CasProtocol casProtocol = CasProtocol.CAS30;

    protected boolean renew = false;

    protected boolean gateway = false;

    protected boolean acceptAnyProxy = false;

    protected ProxyList allowedProxyChains = new ProxyList();

    protected CasProxyReceptor casProxyReceptor;

    /**
     * Get the redirection url.
     * 
     * @param context the web context
     * @return the redirection url
     */
    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        final String contextualCasLoginUrl = prependHostToUrlIfNotPresent(this.casLoginUrl, context);
        final String contextualCallbackUrl = getContextualCallbackUrl(context);

        final String redirectionUrl = CommonUtils.constructRedirectUrl(contextualCasLoginUrl, SERVICE_PARAMETER,
                contextualCallbackUrl, this.renew, this.gateway);
        logger.debug("redirectionUrl : {}", redirectionUrl);
        return RedirectAction.redirect(redirectionUrl);
    }

    @Override
    protected IndirectClient<CasCredentials, CasProfile> newClient() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(this.callbackUrl);
        casClient.setCasLoginUrl(this.casLoginUrl);
        casClient.setCasPrefixUrl(this.casPrefixUrl);
        casClient.setCasProtocol(this.casProtocol);
        casClient.setRenew(this.renew);
        casClient.setGateway(this.gateway);
        casClient.setAcceptAnyProxy(this.acceptAnyProxy);
        casClient.setAllowedProxyChains(this.allowedProxyChains);
        casClient.setCasProxyReceptor(this.casProxyReceptor);
        return casClient;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("logoutHandler", this.logoutHandler);
        if (CommonHelper.isBlank(this.casLoginUrl) && CommonHelper.isBlank(this.casPrefixUrl)) {
            throw new TechnicalException("casLoginUrl and casPrefixUrl cannot be both blank");
        }

        initializeClientConfiguration();

        if (this.casProtocol == CasProtocol.CAS10) {
            initializeCas10Protocol();
        } else if (this.casProtocol == CasProtocol.CAS20) {
            initializeCas20Protocol();
        } else if (this.casProtocol == CasProtocol.CAS20_PROXY) {
            initializeCas20ProxyProtocol();
        } else if (this.casProtocol == CasProtocol.CAS30) {
            initializeCas30Protocol();
        } else if (this.casProtocol == CasProtocol.CAS30_PROXY) {
            initializeCas30ProxyProtocol();
        } else if (this.casProtocol == CasProtocol.SAML) {
            initializeSAMLProtocol();
        }
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<CasProfile>());
    }

    protected void initializeSAMLProtocol() {
        final Saml11TicketValidator saml11TicketValidator = new Saml11TicketValidator(this.casPrefixUrl);
        saml11TicketValidator.setTolerance(getTimeTolerance());
        saml11TicketValidator.setEncoding(this.encoding);
        this.ticketValidator = saml11TicketValidator;
    }

    protected void initializeCas30ProxyProtocol() {
        this.ticketValidator = new Cas30ProxyTicketValidator(this.casPrefixUrl);
        final Cas30ProxyTicketValidator cas30ProxyTicketValidator = (Cas30ProxyTicketValidator) this.ticketValidator;
        cas30ProxyTicketValidator.setEncoding(this.encoding);
        cas30ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas30ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.casProxyReceptor != null) {
            cas30ProxyTicketValidator.setProxyCallbackUrl(this.casProxyReceptor.getCallbackUrl());
            cas30ProxyTicketValidator.setProxyGrantingTicketStorage(this.casProxyReceptor
                    .getProxyGrantingTicketStorage());
        }
    }

    protected void initializeCas30Protocol() {
        this.ticketValidator = new Cas30ServiceTicketValidator(this.casPrefixUrl);
        final Cas30ServiceTicketValidator cas30ServiceTicketValidator = (Cas30ServiceTicketValidator) this.ticketValidator;
        cas30ServiceTicketValidator.setEncoding(this.encoding);
        if (this.casProxyReceptor != null) {
            cas30ServiceTicketValidator.setProxyCallbackUrl(this.casProxyReceptor.getCallbackUrl());
            cas30ServiceTicketValidator.setProxyGrantingTicketStorage(this.casProxyReceptor
                    .getProxyGrantingTicketStorage());
        }
    }

    protected void initializeCas20ProxyProtocol() {
        this.ticketValidator = new Cas20ProxyTicketValidator(this.casPrefixUrl);
        final Cas20ProxyTicketValidator cas20ProxyTicketValidator = (Cas20ProxyTicketValidator) this.ticketValidator;
        cas20ProxyTicketValidator.setEncoding(this.encoding);
        cas20ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas20ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.casProxyReceptor != null) {
            cas20ProxyTicketValidator.setProxyCallbackUrl(this.casProxyReceptor.getCallbackUrl());
            cas20ProxyTicketValidator.setProxyGrantingTicketStorage(this.casProxyReceptor
                    .getProxyGrantingTicketStorage());
        }
    }

    protected void initializeCas20Protocol() {
        this.ticketValidator = new Cas20ServiceTicketValidator(this.casPrefixUrl);
        final Cas20ServiceTicketValidator cas20ServiceTicketValidator = (Cas20ServiceTicketValidator) this.ticketValidator;
        cas20ServiceTicketValidator.setEncoding(this.encoding);
        if (this.casProxyReceptor != null) {
            cas20ServiceTicketValidator.setProxyCallbackUrl(this.casProxyReceptor.getCallbackUrl());
            cas20ServiceTicketValidator.setProxyGrantingTicketStorage(this.casProxyReceptor
                    .getProxyGrantingTicketStorage());
        }
    }

    protected void initializeCas10Protocol() {
        this.ticketValidator = new Cas10TicketValidator(this.casPrefixUrl);
        final Cas10TicketValidator cas10TicketValidator = (Cas10TicketValidator) this.ticketValidator;
        cas10TicketValidator.setEncoding(this.encoding);
    }

    protected void initializeClientConfiguration() {
        if (this.casPrefixUrl != null && !this.casPrefixUrl.endsWith("/")) {
            this.casPrefixUrl += "/";
        }
        if (CommonHelper.isBlank(this.casPrefixUrl)) {
            this.casPrefixUrl = this.casLoginUrl.replaceFirst("/login", "/");
        } else if (CommonHelper.isBlank(this.casLoginUrl)) {
            this.casLoginUrl = this.casPrefixUrl + "login";
        }
    }

    /**
     * Get the credentials from the web context.
     * 
     * @param context the web context
     * @return the credentials
     * @throws RequiresHttpAction requires an extra HTTP action
     */
    @Override
    protected CasCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        // like the SingleSignOutFilter from CAS client :
        if (this.logoutHandler.isTokenRequest(context)) {
            final String ticket = context.getRequestParameter(SERVICE_TICKET_PARAMETER);
            this.logoutHandler.recordSession(context, ticket);
            final CasCredentials casCredentials = new CasCredentials(ticket, getName());
            logger.debug("casCredentials : {}", casCredentials);
            return casCredentials;
        }

        if (this.logoutHandler.isLogoutRequest(context)) {
            this.logoutHandler.destroySession(context);
            final String message = "logout request : no credential returned";
            logger.debug(message);
            throw RequiresHttpAction.ok(message, context);
        }

        if (this.gateway) {
            logger.info("No credential found in this gateway round-trip");
            return null;
        }
        final String message = "No ticket or logout request";
        logger.error(message);
        throw new CredentialsException(message);

    }

    /**
     * Get the user profile from the credentials.
     * 
     * @param credentials the CAS credentials
     * @return the user profile
     */
    @Override
    protected CasProfile retrieveUserProfile(final CasCredentials credentials, final WebContext context) {
        final String ticket = credentials.getServiceTicket();
        try {
            final String contextualCallbackUrl = getContextualCallbackUrl(context);
            final Assertion assertion = this.ticketValidator.validate(ticket, contextualCallbackUrl);
            final AttributePrincipal principal = assertion.getPrincipal();
            logger.debug("principal : {}", principal);
            final CasProfile casProfile;
            if (this.casProxyReceptor != null) {
                casProfile = new CasProxyProfile();
            } else {
                casProfile = new CasProfile();
            }
            casProfile.setId(principal.getName());
            casProfile.addAttributes(principal.getAttributes());
            if (this.casProxyReceptor != null) {
                ((CasProxyProfile) casProfile).setPrincipal(principal);
            }
            logger.debug("casProfile : {}", casProfile);
            return casProfile;
        } catch (final TicketValidationException e) {
            logger.error("cannot validate CAS ticket : {} / {}", ticket, e);
            throw new TechnicalException(e);
        }
    }

    public String getCasLoginUrl() {
        return this.casLoginUrl;
    }

    public void setCasLoginUrl(final String casLoginUrl) {
        this.casLoginUrl = casLoginUrl;
    }

    public String getCasPrefixUrl() {
        return this.casPrefixUrl;
    }

    public void setCasPrefixUrl(final String casPrefixUrl) {
        this.casPrefixUrl = casPrefixUrl;
    }

    public CasProtocol getCasProtocol() {
        return this.casProtocol;
    }

    public void setCasProtocol(final CasProtocol casProtocol) {
        this.casProtocol = casProtocol;
    }

    public boolean isRenew() {
        return this.renew;
    }

    public void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public boolean isGateway() {
        return this.gateway;
    }

    public void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public LogoutHandler getLogoutHandler() {
        return this.logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public boolean isAcceptAnyProxy() {
        return this.acceptAnyProxy;
    }

    public void setAcceptAnyProxy(final boolean acceptAnyProxy) {
        this.acceptAnyProxy = acceptAnyProxy;
    }

    public ProxyList getAllowedProxyChains() {
        return this.allowedProxyChains;
    }

    public void setAllowedProxyChains(final ProxyList allowedProxyChains) {
        this.allowedProxyChains = allowedProxyChains;
    }

    public CasProxyReceptor getCasProxyReceptor() {
        return this.casProxyReceptor;
    }

    public void setCasProxyReceptor(final CasProxyReceptor casProxyReceptor) {
        this.casProxyReceptor = casProxyReceptor;
    }

    public long getTimeTolerance() {
		return timeTolerance;
	}

	public void setTimeTolerance(long timeTolerance) {
		this.timeTolerance = timeTolerance;
	}

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "casLoginUrl", this.casLoginUrl,
                "casPrefixUrl", this.casPrefixUrl, "casProtocol", this.casProtocol, "renew", this.renew, "gateway",
                this.gateway, "encoding", this.encoding, "logoutHandler", this.logoutHandler, "acceptAnyProxy", this.acceptAnyProxy,
                "allowedProxyChains", this.allowedProxyChains, "casProxyReceptor", this.casProxyReceptor);
    }

    @Override
    protected boolean isDirectRedirection() {
        return true;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.CAS_PROTOCOL;
    }
}
