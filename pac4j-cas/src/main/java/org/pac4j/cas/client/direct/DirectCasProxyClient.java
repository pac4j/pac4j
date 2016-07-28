package org.pac4j.cas.client.direct;

import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.credentials.extractor.TokenCredentialsExtractor;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the direct client to authenticate users based on CAS proxy tickets.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DirectCasProxyClient extends DirectClientV2<TokenCredentials, CasProfile> {

    private CasConfiguration configuration;

    private String serviceUrl;

    public DirectCasProxyClient() { }

    public DirectCasProxyClient(final CasConfiguration casConfiguration, final String serviceUrl) {
        this.configuration = casConfiguration;
        this.serviceUrl = serviceUrl;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        CommonHelper.assertNotBlank("serviceUrl", this.serviceUrl);
        // must be a CAS proxy protocol
        final CasProtocol protocol = configuration.getProtocol();
        CommonHelper.assertTrue(protocol == CasProtocol.CAS20_PROXY || protocol == CasProtocol.CAS30_PROXY, "The DirectCasProxyClient must be configured with a CAS proxy protocol (CAS20_PROXY or CAS30_PROXY)");

        configuration.init(context);
        setCredentialsExtractor(new ParameterExtractor(CasConfiguration.TICKET_PARAMETER, true, false, getName()));
        super.setAuthenticator(new CasAuthenticator(configuration, this.serviceUrl));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());

        super.internalInit(context);
        assertCredentialsExtractorTypes(TokenCredentialsExtractor.class);
        assertAuthenticatorTypes(CasAuthenticator.class);
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CasConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration, "serviceUrl", serviceUrl);
    }
}
