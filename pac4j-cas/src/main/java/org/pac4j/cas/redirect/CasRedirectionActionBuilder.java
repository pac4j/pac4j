package org.pac4j.cas.redirect;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * CAS redirection action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasRedirectionActionBuilder implements RedirectionActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CasRedirectionActionBuilder.class);

    protected CasConfiguration configuration;

    protected CasClient client;

    public CasRedirectionActionBuilder(final CasConfiguration configuration, final CasClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context) {
        String computeLoginUrl = configuration.computeFinalLoginUrl(context);
        final String method = configuration.getMethod();
        if (method != null) {
            computeLoginUrl = CommonHelper.addParameter(computeLoginUrl, "method", method);
        }
        final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
        final String redirectionUrl = CommonUtils.constructRedirectUrl(computeLoginUrl, getServiceParameter(),
                computedCallbackUrl, configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl: {}", redirectionUrl);
        return Optional.of(RedirectionActionHelper.buildRedirectUrlAction(context, redirectionUrl));
    }

    private String getServiceParameter() {
        if (configuration.getProtocol() == CasProtocol.SAML) {
            return Protocol.SAML11.getServiceParameterName();
        } else {
            return CasConfiguration.SERVICE_PARAMETER;
        }
    }
}
