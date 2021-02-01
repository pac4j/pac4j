package org.pac4j.cas.config;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jasig.cas.client.util.PrivateKeyUtils;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Cas30ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.store.ProxyGrantingTicketStore;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.CommonHelper;

/**
 * CAS configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasConfiguration extends BaseClientConfiguration {

    public static final String TICKET_PARAMETER = "ticket";

    public static final String SERVICE_PARAMETER = "service";

    public final static String LOGOUT_REQUEST_PARAMETER = "logoutRequest";

    public final static String SESSION_INDEX_TAG = "SessionIndex";

    public final static String RELAY_STATE_PARAMETER = "RelayState";

    private String encoding = StandardCharsets.UTF_8.name();

    private String loginUrl;

    private String prefixUrl;

    private String restUrl;

    private long timeTolerance = 1000L;

    private CasProtocol protocol = CasProtocol.CAS30;

    private boolean renew = false;

    private boolean gateway = false;

    private boolean acceptAnyProxy = false;

    private ProxyList allowedProxyChains = new ProxyList();

    private LogoutHandler logoutHandler;

    private TicketValidator defaultTicketValidator;

    private CasProxyReceptor proxyReceptor;

    private UrlResolver urlResolver;

    private String postLogoutUrlParameter = SERVICE_PARAMETER;

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    private String method;

    private String privateKeyPath;

    private String privateKeyAlgorithm;

    private PrivateKey privateKey;

    public CasConfiguration() {}

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
    protected void internalInit() {
        if (CommonHelper.isBlank(this.loginUrl) && CommonHelper.isBlank(this.prefixUrl) && CommonHelper.isBlank(this.restUrl)) {
            throw new TechnicalException("loginUrl, prefixUrl and restUrl cannot be all blank");
        }
        if (urlResolver == null) {
            urlResolver = new DefaultUrlResolver();
        }

        initializeClientConfiguration();

        initializeLogoutHandler();

        if (privateKeyPath != null) {
            final String algo;
            if (privateKeyAlgorithm != null) {
                algo = privateKeyAlgorithm;
            } else {
                algo = "RSA";
            }
            this.privateKey = PrivateKeyUtils.createKey(privateKeyPath, algo);
        }
    }

    protected void initializeClientConfiguration() {
        if (this.prefixUrl != null && !this.prefixUrl.endsWith("/")) {
            this.prefixUrl += "/";
        }
        if (CommonHelper.isBlank(this.prefixUrl)) {
            this.prefixUrl = this.loginUrl.replaceFirst("/login$", "/");
        } else if (CommonHelper.isBlank(this.loginUrl)) {
            this.loginUrl = this.prefixUrl + "login";
        }
        if (CommonHelper.isBlank(restUrl)) {
            restUrl = prefixUrl;
            if (!restUrl.endsWith("/")) {
                restUrl += "/";
            }
            restUrl += "v1/tickets";
        }
    }

    protected void initializeLogoutHandler() {
        if (this.logoutHandler == null) {
            this.logoutHandler = new DefaultLogoutHandler();
        }
    }

    public TicketValidator retrieveTicketValidator(final WebContext context) {
        if (this.defaultTicketValidator != null) {
            return this.defaultTicketValidator;
        } else {
            if (this.protocol == CasProtocol.CAS10) {
                return buildCas10TicketValidator(context);
            } else if (this.protocol == CasProtocol.CAS20) {
                return buildCas20TicketValidator(context);
            } else if (this.protocol == CasProtocol.CAS20_PROXY) {
                return buildCas20ProxyTicketValidator(context);
            } else if (this.protocol == CasProtocol.CAS30) {
                return buildCas30TicketValidator(context);
            } else if (this.protocol == CasProtocol.CAS30_PROXY) {
                return buildCas30ProxyTicketValidator(context);
            } else if (this.protocol == CasProtocol.SAML) {
                return buildSAMLTicketValidator(context);
            } else {
                throw new TechnicalException("Unable to initialize the TicketValidator for protocol: " + this.protocol);
            }
        }
    }

    protected TicketValidator buildSAMLTicketValidator(final WebContext context) {
        final Saml11TicketValidator saml11TicketValidator = new Saml11TicketValidator(computeFinalPrefixUrl(context));
        saml11TicketValidator.setTolerance(getTimeTolerance());
        saml11TicketValidator.setEncoding(this.encoding);
        saml11TicketValidator.setRenew(this.renew);
        return saml11TicketValidator;
    }

    protected void addPrivateKey(final Cas20ServiceTicketValidator validator) {
        if (this.privateKey != null) {
            validator.setPrivateKey(this.privateKey);
        }
    }

    protected TicketValidator buildCas30ProxyTicketValidator(final WebContext context) {
        final Cas30ProxyTicketValidator cas30ProxyTicketValidator = new Cas30ProxyTicketValidator(computeFinalPrefixUrl(context));
        cas30ProxyTicketValidator.setEncoding(this.encoding);
        cas30ProxyTicketValidator.setRenew(this.renew);
        cas30ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas30ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas30ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ProxyTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas30ProxyTicketValidator);
        return cas30ProxyTicketValidator;
    }

    protected TicketValidator buildCas30TicketValidator(final WebContext context) {
        final Cas30ServiceTicketValidator cas30ServiceTicketValidator = new Cas30ServiceTicketValidator(computeFinalPrefixUrl(context));
        cas30ServiceTicketValidator.setEncoding(this.encoding);
        cas30ServiceTicketValidator.setRenew(this.renew);
        if (this.proxyReceptor != null) {
            cas30ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ServiceTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas30ServiceTicketValidator);
        return cas30ServiceTicketValidator;
    }

    protected TicketValidator buildCas20ProxyTicketValidator(final WebContext context) {
        final Cas20ProxyTicketValidator cas20ProxyTicketValidator = new Cas20ProxyTicketValidator(computeFinalPrefixUrl(context));
        cas20ProxyTicketValidator.setEncoding(this.encoding);
        cas20ProxyTicketValidator.setRenew(this.renew);
        cas20ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas20ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas20ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ProxyTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas20ProxyTicketValidator);
        return cas20ProxyTicketValidator;
    }

    protected TicketValidator buildCas20TicketValidator(final WebContext context) {
        final Cas20ServiceTicketValidator cas20ServiceTicketValidator = new Cas20ServiceTicketValidator(computeFinalPrefixUrl(context));
        cas20ServiceTicketValidator.setEncoding(this.encoding);
        cas20ServiceTicketValidator.setRenew(this.renew);
        if (this.proxyReceptor != null) {
            cas20ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ServiceTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas20ServiceTicketValidator);
        return cas20ServiceTicketValidator;
    }

    protected TicketValidator buildCas10TicketValidator(final WebContext context) {
        final Cas10TicketValidator cas10TicketValidator = new Cas10TicketValidator(computeFinalPrefixUrl(context));
        cas10TicketValidator.setEncoding(this.encoding);
        cas10TicketValidator.setRenew(this.renew);
        return cas10TicketValidator;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String computeFinalLoginUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.loginUrl, context);
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

    public String computeFinalPrefixUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.prefixUrl, context);
    }

    public void setPrefixUrl(final String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(final Map<String, String> customParams) {
        this.customParams = customParams;
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

    public void setAllowedProxies(List<String> allowedProxies) {
        List<String[]> proxyChains = new ArrayList<>();
        for (String allowedProxyChain : allowedProxies) {
            String[] proxyChain = new String[1];
            proxyChain[0] = allowedProxyChain;
            proxyChains.add(proxyChain);
        }
        this.allowedProxyChains = new ProxyList(proxyChains);
    }

    public LogoutHandler getLogoutHandler() {
        return logoutHandler;
    }

    public LogoutHandler findLogoutHandler() {
        init();

        return logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public TicketValidator getDefaultTicketValidator() {
        return defaultTicketValidator;
    }

    public void setDefaultTicketValidator(final TicketValidator defaultTicketValidator) {
        this.defaultTicketValidator = defaultTicketValidator;
    }

    public CasProxyReceptor getProxyReceptor() {
        return proxyReceptor;
    }

    public void setProxyReceptor(final CasProxyReceptor proxyReceptor) {
        this.proxyReceptor = proxyReceptor;
    }

    public String getPostLogoutUrlParameter() {
        return postLogoutUrlParameter;
    }

    public void setPostLogoutUrlParameter(final String postLogoutUrlParameter) {
        this.postLogoutUrlParameter = postLogoutUrlParameter;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(final String restUrl) {
        this.restUrl = restUrl;
    }

    public String computeFinalRestUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.restUrl, context);
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    public void addCustomParam(final String name, final String value) {
        this.customParams.put(name, value);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(final String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyAlgorithm() {
        return privateKeyAlgorithm;
    }

    public void setPrivateKeyAlgorithm(final String privateKeyAlgorithm) {
        this.privateKeyAlgorithm = privateKeyAlgorithm;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "loginUrl", this.loginUrl, "prefixUrl", this.prefixUrl, "restUrl", this.restUrl,
                "protocol", this.protocol, "renew", this.renew, "gateway", this.gateway, "encoding", this.encoding,
                "logoutHandler", this.logoutHandler, "acceptAnyProxy", this.acceptAnyProxy, "allowedProxyChains", this.allowedProxyChains,
                "proxyReceptor", this.proxyReceptor, "timeTolerance", this.timeTolerance, "postLogoutUrlParameter",
                this.postLogoutUrlParameter, "defaultTicketValidator", this.defaultTicketValidator, "urlResolver", this.urlResolver,
                "method", this.method, "privateKeyPath", this.privateKeyPath, "privateKeyAlgorithm", this.privateKeyAlgorithm);
    }
}
