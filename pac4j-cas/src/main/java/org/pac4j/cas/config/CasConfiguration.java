package org.pac4j.cas.config;

import org.jasig.cas.client.validation.*;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.logout.CasLogoutHandler;
import org.pac4j.cas.logout.CasSingleSignOutHandler;
import org.pac4j.cas.logout.NoLogoutHandler;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.CallbackUrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

/**
 * CAS configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasConfiguration extends InitializableWebObject {

    public static final String TICKET_PARAMETER = "ticket";

    public static final String SERVICE_PARAMETER = "service";

    public final static String LOGOUT_REQUEST_PARAMETER = "logoutRequest";

    public final static String SESSION_INDEX_TAG = "SessionIndex";

    public final static String RELAY_STATE_PARAMETER = "RelayState";

    private String encoding = HttpConstants.UTF8_ENCODING;

    private String loginUrl;

    private String prefixUrl;

    private long timeTolerance = 1000L;

    private CasProtocol protocol = CasProtocol.CAS30;

    private boolean renew = false;

    private boolean gateway = false;

    private boolean acceptAnyProxy = false;

    private ProxyList allowedProxyChains = new ProxyList();

    private CasLogoutHandler logoutHandler;

    private TicketValidator ticketValidator;

    private CasProxyReceptor proxyReceptor;

    private CallbackUrlResolver callbackUrlResolver;

    public CasConfiguration() { }

    public CasConfiguration(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public CasConfiguration(final String loginUrl, final CasProtocol protocol) {
        this.loginUrl = loginUrl;
        this.protocol = protocol;
    }

    public CasConfiguration(final String loginUrl, final String prefixUrl) {
        this.loginUrl = loginUrl;
        this.prefixUrl = prefixUrl;
    }

    @Override
    protected void internalInit(final WebContext context) {
        if (CommonHelper.isBlank(this.loginUrl) && CommonHelper.isBlank(this.prefixUrl)) {
            throw new TechnicalException("loginUrl and prefixUrl cannot be both blank");
        }

        initializeClientConfiguration(context);

        initializeLogoutHandler(context);

        if (this.protocol == CasProtocol.CAS10) {
            initializeCas10Protocol();
        } else if (this.protocol == CasProtocol.CAS20) {
            initializeCas20Protocol(context);
        } else if (this.protocol == CasProtocol.CAS20_PROXY) {
            initializeCas20ProxyProtocol(context);
        } else if (this.protocol == CasProtocol.CAS30) {
            initializeCas30Protocol(context);
        } else if (this.protocol == CasProtocol.CAS30_PROXY) {
            initializeCas30ProxyProtocol(context);
        } else if (this.protocol == CasProtocol.SAML) {
            initializeSAMLProtocol();
        }
    }

    protected void initializeClientConfiguration(final WebContext context) {
        if (this.prefixUrl != null && !this.prefixUrl.endsWith("/")) {
            this.prefixUrl += "/";
        }
        if (CommonHelper.isBlank(this.prefixUrl)) {
            this.prefixUrl = this.loginUrl.replaceFirst("/login$", "/");
        } else if (CommonHelper.isBlank(this.loginUrl)) {
            this.loginUrl = this.prefixUrl + "login";
        }
        if (callbackUrlResolver != null) {
            this.prefixUrl = callbackUrlResolver.compute(this.prefixUrl, context);
            this.loginUrl = callbackUrlResolver.compute(this.loginUrl, context);
        }
    }

    private void initializeLogoutHandler(final WebContext context) {
        if (this.logoutHandler == null) {
            if (context instanceof J2EContext) {
                this.logoutHandler = new CasSingleSignOutHandler();
            } else {
                this.logoutHandler = new NoLogoutHandler();
            }
        }
    }

    protected void initializeSAMLProtocol() {
        final Saml11TicketValidator saml11TicketValidator = new Saml11TicketValidator(this.prefixUrl);
        saml11TicketValidator.setTolerance(getTimeTolerance());
        saml11TicketValidator.setEncoding(this.encoding);
        setTicketValidator(saml11TicketValidator);
    }

    protected void initializeCas30ProxyProtocol(final WebContext context) {
        final Cas30ProxyTicketValidator cas30ProxyTicketValidator = new Cas30ProxyTicketValidator(this.prefixUrl);
        cas30ProxyTicketValidator.setEncoding(this.encoding);
        cas30ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas30ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas30ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ProxyTicketValidator.setProxyGrantingTicketStorage(this.proxyReceptor
                    .getProxyGrantingTicketStorage());
        }
        setTicketValidator(cas30ProxyTicketValidator);
    }

    protected void initializeCas30Protocol(final WebContext context) {
        final Cas30ServiceTicketValidator cas30ServiceTicketValidator = new Cas30ServiceTicketValidator(this.prefixUrl);
        cas30ServiceTicketValidator.setEncoding(this.encoding);
        if (this.proxyReceptor != null) {
            cas30ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ServiceTicketValidator.setProxyGrantingTicketStorage(this.proxyReceptor
                    .getProxyGrantingTicketStorage());
        }
        setTicketValidator(cas30ServiceTicketValidator);
    }

    protected void initializeCas20ProxyProtocol(final WebContext context) {
        final Cas20ProxyTicketValidator cas20ProxyTicketValidator = new Cas20ProxyTicketValidator(this.prefixUrl);
        cas20ProxyTicketValidator.setEncoding(this.encoding);
        cas20ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas20ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas20ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ProxyTicketValidator.setProxyGrantingTicketStorage(this.proxyReceptor
                    .getProxyGrantingTicketStorage());
        }
        setTicketValidator(cas20ProxyTicketValidator);
    }

    protected void initializeCas20Protocol(final WebContext context) {
        final Cas20ServiceTicketValidator cas20ServiceTicketValidator = new Cas20ServiceTicketValidator(this.prefixUrl);
        cas20ServiceTicketValidator.setEncoding(this.encoding);
        if (this.proxyReceptor != null) {
            cas20ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ServiceTicketValidator.setProxyGrantingTicketStorage(this.proxyReceptor
                    .getProxyGrantingTicketStorage());
        }
        setTicketValidator(cas20ServiceTicketValidator);
    }

    protected void initializeCas10Protocol() {
        final Cas10TicketValidator cas10TicketValidator = new Cas10TicketValidator(this.prefixUrl);
        cas10TicketValidator.setEncoding(this.encoding);
        setTicketValidator(cas10TicketValidator);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(final String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public long getTimeTolerance() {
        return timeTolerance;
    }

    public void setTimeTolerance(final long timeTolerance) {
        this.timeTolerance = timeTolerance;
    }

    public CasProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(final CasProtocol protocol) {
        this.protocol = protocol;
    }

    public boolean isRenew() {
        return renew;
    }

    public void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public boolean isGateway() {
        return gateway;
    }

    public void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public boolean isAcceptAnyProxy() {
        return acceptAnyProxy;
    }

    public void setAcceptAnyProxy(final boolean acceptAnyProxy) {
        this.acceptAnyProxy = acceptAnyProxy;
    }

    public ProxyList getAllowedProxyChains() {
        return allowedProxyChains;
    }

    public void setAllowedProxyChains(final ProxyList allowedProxyChains) {
        this.allowedProxyChains = allowedProxyChains;
    }

    public CasLogoutHandler getLogoutHandler() {
        return logoutHandler;
    }

    public void setLogoutHandler(final CasLogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public TicketValidator getTicketValidator() {
        return ticketValidator;
    }

    public void setTicketValidator(final TicketValidator ticketValidator) {
        if (this.ticketValidator == null) {
            this.ticketValidator = ticketValidator;
        }
    }

    public CasProxyReceptor getProxyReceptor() {
        return proxyReceptor;
    }

    public void setProxyReceptor(final CasProxyReceptor proxyReceptor) {
        this.proxyReceptor = proxyReceptor;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        if (this.callbackUrlResolver == null) {
            this.callbackUrlResolver = callbackUrlResolver;
        }
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "loginUrl", this.loginUrl, "prefixUrl", this.prefixUrl,
                "protocol", this.protocol, "renew", this.renew, "gateway", this.gateway, "encoding", this.encoding,
                "logoutHandler", this.logoutHandler, "acceptAnyProxy", this.acceptAnyProxy, "allowedProxyChains", this.allowedProxyChains,
                "proxyReceptor", this.proxyReceptor, "timeTolerance", this.timeTolerance);
    }
}
