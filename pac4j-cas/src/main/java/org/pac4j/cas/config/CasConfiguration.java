package org.pac4j.cas.config;

import lombok.*;
import lombok.experimental.Accessors;
import org.apereo.cas.client.ssl.HttpURLConnectionFactory;
import org.apereo.cas.client.util.PrivateKeyUtils;
import org.apereo.cas.client.validation.*;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.store.ProxyGrantingTicketStore;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.logout.handler.DefaultSessionLogoutHandler;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.core.util.CommonHelper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.Serial;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;

/**
 * CAS configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@With
@AllArgsConstructor
public class CasConfiguration extends BaseClientConfiguration {

    /** Constant <code>TICKET_PARAMETER="ticket"</code> */
    public static final String TICKET_PARAMETER = "ticket";

    /** Constant <code>SERVICE_PARAMETER="service"</code> */
    public static final String SERVICE_PARAMETER = "service";

    /** Constant <code>LOGOUT_REQUEST_PARAMETER="logoutRequest"</code> */
    public final static String LOGOUT_REQUEST_PARAMETER = "logoutRequest";

    /** Constant <code>SESSION_INDEX_TAG="SessionIndex"</code> */
    public final static String SESSION_INDEX_TAG = "SessionIndex";

    /** Constant <code>RELAY_STATE_PARAMETER="RelayState"</code> */
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

    private SessionLogoutHandler sessionLogoutHandler;

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

    private HostnameVerifier hostnameVerifier;

    private SSLSocketFactory sslSocketFactory;

    /**
     * <p>Constructor for CasConfiguration.</p>
     */
    public CasConfiguration() {}

    /**
     * <p>Constructor for CasConfiguration.</p>
     *
     * @param loginUrl a {@link String} object
     */
    public CasConfiguration(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * <p>Constructor for CasConfiguration.</p>
     *
     * @param loginUrl a {@link String} object
     * @param protocol a {@link CasProtocol} object
     */
    public CasConfiguration(final String loginUrl, final CasProtocol protocol) {
        this.loginUrl = loginUrl;
        this.protocol = protocol;
    }

    /**
     * <p>Constructor for CasConfiguration.</p>
     *
     * @param loginUrl a {@link String} object
     * @param prefixUrl a {@link String} object
     */
    public CasConfiguration(final String loginUrl, final String prefixUrl) {
        this.loginUrl = loginUrl;
        this.prefixUrl = prefixUrl;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
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
            algo = Objects.requireNonNullElse(privateKeyAlgorithm, "RSA");
            this.privateKey = PrivateKeyUtils.createKey(privateKeyPath, algo);
        }
    }

    /**
     * <p>initializeClientConfiguration.</p>
     */
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

    /**
     * <p>initializeLogoutHandler.</p>
     */
    protected void initializeLogoutHandler() {
        if (this.sessionLogoutHandler == null) {
            this.sessionLogoutHandler = new DefaultSessionLogoutHandler();
        }
    }

    /**
     * <p>retrieveTicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
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

    /**
     * <p>buildSAMLTicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildSAMLTicketValidator(final WebContext context) {
        val saml11TicketValidator = new Saml11TicketValidator(computeFinalPrefixUrl(context));
        saml11TicketValidator.setTolerance(getTimeTolerance());
        saml11TicketValidator.setEncoding(this.encoding);
        saml11TicketValidator.setRenew(this.renew);
        getHttpURLConnectionFactory().ifPresent(saml11TicketValidator::setURLConnectionFactory);
        return saml11TicketValidator;
    }

    /**
     * <p>addPrivateKey.</p>
     *
     * @param validator a {@link Cas20ServiceTicketValidator} object
     */
    protected void addPrivateKey(final Cas20ServiceTicketValidator validator) {
        if (this.privateKey != null) {
            validator.setPrivateKey(this.privateKey);
        }
    }

    /**
     * <p>buildCas30ProxyTicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildCas30ProxyTicketValidator(final WebContext context) {
        val cas30ProxyTicketValidator = new Cas30ProxyTicketValidator(computeFinalPrefixUrl(context));
        cas30ProxyTicketValidator.setEncoding(this.encoding);
        cas30ProxyTicketValidator.setRenew(this.renew);
        cas30ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas30ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas30ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ProxyTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas30ProxyTicketValidator);
        getHttpURLConnectionFactory().ifPresent(cas30ProxyTicketValidator::setURLConnectionFactory);
        return cas30ProxyTicketValidator;
    }

    /**
     * <p>buildCas30TicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildCas30TicketValidator(final WebContext context) {
        val cas30ServiceTicketValidator = new Cas30ServiceTicketValidator(computeFinalPrefixUrl(context));
        cas30ServiceTicketValidator.setEncoding(this.encoding);
        cas30ServiceTicketValidator.setRenew(this.renew);
        if (this.proxyReceptor != null) {
            cas30ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas30ServiceTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas30ServiceTicketValidator);
        getHttpURLConnectionFactory().ifPresent(cas30ServiceTicketValidator::setURLConnectionFactory);
        return cas30ServiceTicketValidator;
    }

    /**
     * <p>buildCas20ProxyTicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildCas20ProxyTicketValidator(final WebContext context) {
        val cas20ProxyTicketValidator = new Cas20ProxyTicketValidator(computeFinalPrefixUrl(context));
        cas20ProxyTicketValidator.setEncoding(this.encoding);
        cas20ProxyTicketValidator.setRenew(this.renew);
        cas20ProxyTicketValidator.setAcceptAnyProxy(this.acceptAnyProxy);
        cas20ProxyTicketValidator.setAllowedProxyChains(this.allowedProxyChains);
        if (this.proxyReceptor != null) {
            cas20ProxyTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ProxyTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas20ProxyTicketValidator);
        getHttpURLConnectionFactory().ifPresent(cas20ProxyTicketValidator::setURLConnectionFactory);
        return cas20ProxyTicketValidator;
    }

    /**
     * <p>buildCas20TicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildCas20TicketValidator(final WebContext context) {
        val cas20ServiceTicketValidator = new Cas20ServiceTicketValidator(computeFinalPrefixUrl(context));
        cas20ServiceTicketValidator.setEncoding(this.encoding);
        cas20ServiceTicketValidator.setRenew(this.renew);
        if (this.proxyReceptor != null) {
            cas20ServiceTicketValidator.setProxyCallbackUrl(this.proxyReceptor.computeFinalCallbackUrl(context));
            cas20ServiceTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStore(this.proxyReceptor.getStore()));
        }
        addPrivateKey(cas20ServiceTicketValidator);
        getHttpURLConnectionFactory().ifPresent(cas20ServiceTicketValidator::setURLConnectionFactory);
        return cas20ServiceTicketValidator;
    }

    /**
     * <p>buildCas10TicketValidator.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link TicketValidator} object
     */
    protected TicketValidator buildCas10TicketValidator(final WebContext context) {
        val cas10TicketValidator = new Cas10TicketValidator(computeFinalPrefixUrl(context));
        cas10TicketValidator.setEncoding(this.encoding);
        cas10TicketValidator.setRenew(this.renew);
        getHttpURLConnectionFactory().ifPresent(cas10TicketValidator::setURLConnectionFactory);
        return cas10TicketValidator;
    }

    /**
     * <p>computeFinalLoginUrl.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link String} object
     */
    public String computeFinalLoginUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.loginUrl, context);
    }

    /**
     * <p>computeFinalPrefixUrl.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link String} object
     */
    public String computeFinalPrefixUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.prefixUrl, context);
    }

    /**
     * <p>setAllowedProxies.</p>
     *
     * @param allowedProxies a {@link List} object
     */
    public void setAllowedProxies(final Iterable<String> allowedProxies) {
        final List<String[]> proxyChains = new ArrayList<>();
        for (final String allowedProxyChain : allowedProxies) {
            final String[] proxyChain = new String[1];
            proxyChain[0] = allowedProxyChain;
            proxyChains.add(proxyChain);
        }
        this.allowedProxyChains = new ProxyList(proxyChains);
    }

    /**
     * <p>findSessionLogoutHandler.</p>
     *
     * @return a {@link SessionLogoutHandler} object
     */
    public SessionLogoutHandler findSessionLogoutHandler() {
        init();

        return sessionLogoutHandler;
    }

    /**
     * <p>computeFinalRestUrl.</p>
     *
     * @param context a {@link WebContext} object
     * @return a {@link String} object
     */
    public String computeFinalRestUrl(final WebContext context) {
        init();

        return urlResolver.compute(this.restUrl, context);
    }

    private Optional<HttpURLConnectionFactory> getHttpURLConnectionFactory() {
        if (this.sslSocketFactory == null && this.hostnameVerifier == null) {
            return Optional.empty();
        }
        HttpURLConnectionFactory factory = new HttpURLConnectionFactory() {
            @Serial
            private static final long serialVersionUID = 7296708420276819683L;

            @Override
            public HttpURLConnection buildHttpURLConnection(URLConnection conn) {
                if (conn instanceof HttpsURLConnection httpsConnection) {
                    if (getSslSocketFactory() != null) {
                        httpsConnection.setSSLSocketFactory(getSslSocketFactory());
                    }
                    if (getHostnameVerifier() != null) {
                        httpsConnection.setHostnameVerifier(getHostnameVerifier());
                    }
                }
                return (HttpURLConnection) conn;
            }
        };
        return Optional.of(factory);
    }
}
