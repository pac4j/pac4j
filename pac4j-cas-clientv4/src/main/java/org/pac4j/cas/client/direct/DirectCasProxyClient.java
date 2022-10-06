package org.pac4j.cas.client.direct;

import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the direct client to authenticate users based on CAS proxy tickets.</p>
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
public class DirectCasProxyClient extends DirectClient {

    private CasConfiguration configuration;

    private UrlResolver urlResolver = new DefaultUrlResolver();

    private CallbackUrlResolver callbackUrlResolver = new NoParameterCallbackUrlResolver();

    private String serviceUrl;

    public DirectCasProxyClient() { }

    public DirectCasProxyClient(final CasConfiguration casConfiguration, final String serviceUrl) {
        this.configuration = casConfiguration;
        this.serviceUrl = serviceUrl;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("serviceUrl", this.serviceUrl);
        assertNotNull("configuration", this.configuration);
        // must be a CAS proxy protocol
        final var protocol = configuration.getProtocol();
        assertTrue(protocol == CasProtocol.CAS20_PROXY || protocol == CasProtocol.CAS30_PROXY,
            "The DirectCasProxyClient must be configured with a CAS proxy protocol (CAS20_PROXY or CAS30_PROXY)");

        defaultCredentialsExtractor(new ParameterExtractor(CasConfiguration.TICKET_PARAMETER, true, false));
        defaultAuthenticator(new CasAuthenticator(configuration, getName(), urlResolver, callbackUrlResolver, this.serviceUrl));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator());
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
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
        return toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators(), "configuration", this.configuration,
            "callbackUrlResolver", callbackUrlResolver, "serviceUrl", serviceUrl, "urlResolver", this.urlResolver);
    }
}
