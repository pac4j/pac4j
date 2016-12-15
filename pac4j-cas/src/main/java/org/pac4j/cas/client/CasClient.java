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
        super.internalInit(context);

        CommonHelper.assertNotNull("configuration", configuration);
        configuration.setCallbackUrlResolver(this.getCallbackUrlResolver());
        configuration.init(context);

        setRedirectActionBuilder(ctx -> {
            final String loginUrl = configuration.getCallbackUrlResolver().compute(configuration.getLoginUrl(), ctx);
            final String redirectionUrl = CommonUtils.constructRedirectUrl(loginUrl, CasConfiguration.SERVICE_PARAMETER,
                    computeFinalCallbackUrl(ctx), configuration.isRenew(), configuration.isGateway());
            logger.debug("redirectionUrl: {}", redirectionUrl);
            return RedirectAction.redirect(redirectionUrl);
        });
        setCredentialsExtractor(new TicketAndLogoutRequestExtractor(configuration, getName()));
        setAuthenticator(new CasAuthenticator(configuration, callbackUrl));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    @Deprecated
    public void setCasLoginUrl(final String casLoginUrl) {
        configuration.setLoginUrl(casLoginUrl);
    }

    @Deprecated
    public void setCasPrefixUrl(final String casPrefixUrl) {
        configuration.setPrefixUrl(casPrefixUrl);
    }

    @Deprecated
    public void setCasProtocol(final CasProtocol casProtocol) {
        configuration.setProtocol(casProtocol);
    }

    @Deprecated
    public void setRenew(final boolean renew) {
        configuration.setRenew(renew);
    }

    @Deprecated
    public void setGateway(final boolean gateway) {
        configuration.setGateway(gateway);
    }

    @Deprecated
    public void setLogoutHandler(final CasLogoutHandler logoutHandler) {
        configuration.setLogoutHandler(logoutHandler);
    }

    @Deprecated
    public void setAcceptAnyProxy(final boolean acceptAnyProxy) {
        configuration.setAcceptAnyProxy(acceptAnyProxy);
    }

    @Deprecated
    public void setAllowedProxyChains(final ProxyList allowedProxyChains) {
        configuration.setAllowedProxyChains(allowedProxyChains);
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
