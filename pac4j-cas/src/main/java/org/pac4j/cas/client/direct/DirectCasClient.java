package org.pac4j.cas.client.direct;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the direct client to authenticate users on a CAS server for a web application in a stateless way: when trying to access
 * a protected area, the user will be redirected to the CAS server for login and then back directly to this originally requested url.</p>
 *
 * <p>You should generally use the {@link org.pac4j.cas.client.CasClient} instead (this one is very specific and was designed for OAuth /
 * OpenID Connect implementations in the CAS server.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>As no session is meant to be created, this client does not handle CAS logout requests.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined in the configuration (the corresponding "callback filter" must be
 * enabled) and set to the CAS configuration of this client. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DirectCasClient extends DirectClient<TokenCredentials, CommonProfile> {

    private CasConfiguration configuration;

    private UrlResolver urlResolver = new DefaultUrlResolver();

    private CallbackUrlResolver callbackUrlResolver = new NoParameterCallbackUrlResolver();

    public DirectCasClient() { }

    public DirectCasClient(final CasConfiguration casConfiguration) {
        this.configuration = casConfiguration;
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("urlResolver", this.urlResolver);
        CommonHelper.assertNotNull("callbackUrlResolver", this.callbackUrlResolver);
        CommonHelper.assertNotNull("configuration", this.configuration);
        CommonHelper.assertTrue(!configuration.isGateway(), "the DirectCasClient can not support gateway to avoid infinite loops");

        defaultCredentialsExtractor(new ParameterExtractor(CasConfiguration.TICKET_PARAMETER, true, false));
        // only a fake one for the initialization as we will build a new one with the current url for each request
        super.defaultAuthenticator(new CasAuthenticator(configuration, getName(), urlResolver, callbackUrlResolver, "fake"));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());
    }

    @Override
    protected TokenCredentials retrieveCredentials(final WebContext context) {
        init();
        try {
            String callbackUrl = callbackUrlResolver.compute(urlResolver, context.getFullRequestURL(), getName(), context);
            final String loginUrl = configuration.computeFinalLoginUrl(context);

            final TokenCredentials credentials = getCredentialsExtractor().extract(context);
            if (credentials == null) {
                // redirect to the login page
                final String redirectionUrl = CommonUtils.constructRedirectUrl(loginUrl, CasConfiguration.SERVICE_PARAMETER,
                        callbackUrl, configuration.isRenew(), false);
                logger.debug("redirectionUrl: {}", redirectionUrl);
                throw HttpAction.redirect(context, redirectionUrl);
            }

            // clean url from ticket parameter
            callbackUrl = CommonHelper.substringBefore(callbackUrl, "?" + CasConfiguration.TICKET_PARAMETER + "=");
            callbackUrl = CommonHelper.substringBefore(callbackUrl, "&" + CasConfiguration.TICKET_PARAMETER + "=");
            final CasAuthenticator casAuthenticator =
                new CasAuthenticator(configuration, getName(), urlResolver, callbackUrlResolver, callbackUrl);
            casAuthenticator.init();
            casAuthenticator.validate(credentials, context);

            return credentials;
        } catch (CredentialsException e) {
            logger.error("Failed to retrieve or validate CAS credentials", e);
            return null;
        }
    }

    @Override
    protected void defaultAuthenticator(final Authenticator authenticator) {
        throw new TechnicalException("You can not set an Authenticator for the DirectCasClient at startup. A new CasAuthenticator is "
            + "automatically created for each request");
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    public UrlResolver getUrlResolver() {
        return urlResolver;
    }

    public void setUrlResolver(final UrlResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        this.callbackUrlResolver = callbackUrlResolver;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators(), "configuration", this.configuration, "urlResolver", this.urlResolver,
            "callbackUrlResolver", this.callbackUrlResolver);
    }
}
