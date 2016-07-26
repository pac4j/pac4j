package org.pac4j.cas.client;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.logout.CasSingleSignOutHandler;
import org.pac4j.cas.logout.LogoutHandler;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the client to authenticate users on a CAS server for a web application in a stateful way: when trying to access a protected area,
 * the user will be redirected to the CAS server for login and then back to the application (on the callback endpoint) and finally to the originally requested url.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>In a J2E context, the {@link LogoutHandler} will be a {@link CasSingleSignOutHandler}. For other environment, it must be explicitly defined to handle CAS logout requests.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined and set to the configuration. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasClient extends IndirectClient<CasCredentials, CasProfile> {

    protected static final Logger logger = LoggerFactory.getLogger(CasClient.class);

    protected static final String SERVICE_PARAMETER = "service";

    private CasConfiguration configuration = new CasConfiguration();

    public CasClient() { }

    public CasClient(final CasConfiguration casConfiguration) {
        setConfiguration(casConfiguration);
    }

    @Deprecated
    public CasClient(final String casLoginUrl) {
        configuration.setLoginUrl(casLoginUrl);
    }

    @Deprecated
    public CasClient(final String casLoginUrl, final CasProtocol casProtocol) {
        configuration.setLoginUrl(casLoginUrl);
        configuration.setProtocol(casProtocol);
    }

    @Deprecated
    public CasClient(final String casLoginUrl, final String casPrefixUrl) {
        configuration.setLoginUrl(casLoginUrl);
        configuration.setPrefixUrl(casPrefixUrl);
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        final String redirectionUrl = CommonUtils.constructRedirectUrl(configuration.getLoginUrl(), SERVICE_PARAMETER,
                computeFinalCallbackUrl(context), configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl : {}", redirectionUrl);
        return RedirectAction.redirect(redirectionUrl);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);

        if (configuration.getCallbackUrlResolver() == null) {
            configuration.setCallbackUrlResolver(this.getCallbackUrlResolver());
        }
        configuration.init(context);

        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());
    }

    @Override
    protected CasCredentials retrieveCredentials(final WebContext context) throws HttpAction {

        // like the SingleSignOutFilter from the Apereo CAS client:
        if (configuration.getLogoutHandler().isTokenRequest(context)) {
            final String ticket = context.getRequestParameter(CasConfiguration.SERVICE_TICKET_PARAMETER);
            configuration.getLogoutHandler().recordSession(context, ticket);
            final CasCredentials casCredentials = new CasCredentials(ticket, getName());
            logger.debug("casCredentials: {}", casCredentials);
            return casCredentials;
        }

        if (configuration.getLogoutHandler().isLogoutRequest(context)) {
            configuration.getLogoutHandler().destroySession(context);
            final String message = "logout request: no credential returned";
            logger.debug(message);
            throw HttpAction.ok(message, context);
        }

        /*if (configuration.isGateway()) {
            logger.info("No credential found in this gateway round-trip");
            return null;
        }
        final String message = "No ticket or logout request";
        throw new CredentialsException(message);*/
        return null;
    }

    @Override
    protected CasProfile retrieveUserProfile(final CasCredentials credentials, final WebContext context) throws HttpAction {
        final String ticket = credentials.getServiceTicket();
        try {
            final Assertion assertion = configuration.getTicketValidator().validate(ticket, computeFinalCallbackUrl(context));
            final AttributePrincipal principal = assertion.getPrincipal();
            logger.debug("principal: {}", principal);
            final CasProfile casProfile;
            if (configuration.getProxyReceptor() != null) {
                casProfile = new CasProxyProfile();
                ((CasProxyProfile) casProfile).setPrincipal(principal);
            } else {
                casProfile = new CasProfile();
            }
            casProfile.setId(principal.getName());
            casProfile.addAttributes(principal.getAttributes());
            logger.debug("casProfile: {}", casProfile);
            return casProfile;
        } catch (final TicketValidationException e) {
            String message = "cannot validate CAS ticket: " + ticket;
            throw new TechnicalException(message, e);
        }
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Deprecated
    public String getCasLoginUrl() {
        return configuration.getLoginUrl();
    }

    @Deprecated
    public void setCasLoginUrl(final String casLoginUrl) {
        configuration.setLoginUrl(casLoginUrl);
    }

    @Deprecated
    public String getCasPrefixUrl() {
        return configuration.getPrefixUrl();
    }

    @Deprecated
    public void setCasPrefixUrl(final String casPrefixUrl) {
        configuration.setPrefixUrl(casPrefixUrl);
    }

    @Deprecated
    public CasProtocol getCasProtocol() {
        return configuration.getProtocol();
    }

    @Deprecated
    public void setCasProtocol(final CasProtocol casProtocol) {
        configuration.setProtocol(casProtocol);
    }

    @Deprecated
    public boolean isRenew() {
        return configuration.isRenew();
    }

    @Deprecated
    public void setRenew(final boolean renew) {
        configuration.setRenew(renew);
    }

    @Deprecated
    public boolean isGateway() {
        return configuration.isGateway();
    }

    @Deprecated
    public void setGateway(final boolean gateway) {
        configuration.setGateway(gateway);
    }

    @Deprecated
    public LogoutHandler getLogoutHandler() {
        return configuration.getLogoutHandler();
    }

    @Deprecated
    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        configuration.setLogoutHandler(logoutHandler);
    }

    @Deprecated
    public boolean isAcceptAnyProxy() {
        return configuration.isAcceptAnyProxy();
    }

    @Deprecated
    public void setAcceptAnyProxy(final boolean acceptAnyProxy) {
        configuration.setAcceptAnyProxy(acceptAnyProxy);
    }

    @Deprecated
    public ProxyList getAllowedProxyChains() {
        return configuration.getAllowedProxyChains();
    }

    @Deprecated
    public void setAllowedProxyChains(final ProxyList allowedProxyChains) {
        configuration.setAllowedProxyChains(allowedProxyChains);
    }

    @Deprecated
    public CasProxyReceptor getCasProxyReceptor() {
        return configuration.getProxyReceptor();
    }

    @Deprecated
    public void setCasProxyReceptor(final CasProxyReceptor casProxyReceptor) {
        configuration.setProxyReceptor(casProxyReceptor);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "configuration", this.configuration);
    }
}
