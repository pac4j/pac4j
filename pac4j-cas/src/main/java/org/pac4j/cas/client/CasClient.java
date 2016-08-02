package org.pac4j.cas.client;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.ProxyList;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.cas.credentials.extractor.TicketAndLogoutRequestExtractor;
import org.pac4j.cas.logout.CasLogoutHandler;
import org.pac4j.cas.logout.CasSingleSignOutHandler;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.IndirectClientV2;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.TokenCredentialsExtractor;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the client to authenticate users on a CAS server for a web application in a stateful way: when trying to access a protected area,
 * the user will be redirected to the CAS server for login and then back to the application (on the callback endpoint) and finally to the originally requested url.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>In a J2E context, the {@link CasLogoutHandler} will be a {@link CasSingleSignOutHandler}. For other environment, it must be explicitly defined to handle CAS logout requests.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined in the configuration (the corresponding "callback filter" must be enabled)
 * and set to the CAS configuration of this client. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasClient extends IndirectClientV2<TokenCredentials, CasProfile> {

    private CasConfiguration configuration = new CasConfiguration();

    public CasClient() { }

    public CasClient(final CasConfiguration casConfiguration) {
        setConfiguration(casConfiguration);
    }

    /**
     * Use {@link #CasClient(CasConfiguration)} instead.
     *
     * @param casLoginUrl the CAS login url
     */
    @Deprecated
    public CasClient(final String casLoginUrl) {
        configuration.setLoginUrl(casLoginUrl);
    }

    /**
     * Use {@link #CasClient(CasConfiguration)} instead.
     *
     * @param casLoginUrl the CAS login url
     * @param casProtocol the CAS protocol
     */
    @Deprecated
    public CasClient(final String casLoginUrl, final CasProtocol casProtocol) {
        configuration.setLoginUrl(casLoginUrl);
        configuration.setProtocol(casProtocol);
    }

    /**
     * Use {@link #CasClient(CasConfiguration)} instead.
     *
     * @param casLoginUrl the CAS login url
     * @param casPrefixUrl the CAS server prefix url
     */
    @Deprecated
    public CasClient(final String casLoginUrl, final String casPrefixUrl) {
        configuration.setLoginUrl(casLoginUrl);
        configuration.setPrefixUrl(casPrefixUrl);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);

        configuration.setCallbackUrlResolver(this.getCallbackUrlResolver());
        configuration.init(context);
        setRedirectActionBuilder(ctx -> {
            final String loginUrl = configuration.getCallbackUrlResolver().compute(configuration.getLoginUrl(), context);
            final String redirectionUrl = CommonUtils.constructRedirectUrl(loginUrl, CasConfiguration.SERVICE_PARAMETER,
                    computeFinalCallbackUrl(context), configuration.isRenew(), configuration.isGateway());
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return RedirectAction.redirect(redirectionUrl);
        });
        setCredentialsExtractor(new TicketAndLogoutRequestExtractor(configuration, getName()));
        setAuthenticator(new CasAuthenticator(configuration, callbackUrl));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());

        super.internalInit(context);
        assertCredentialsExtractorTypes(TokenCredentialsExtractor.class);
        assertAuthenticatorTypes(CasAuthenticator.class);
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
    public CasLogoutHandler getLogoutHandler() {
        return configuration.getLogoutHandler();
    }

    @Deprecated
    public void setLogoutHandler(final CasLogoutHandler logoutHandler) {
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
