package org.pac4j.cas.redirect;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public RedirectionAction redirect(final WebContext context) {
        final String computeLoginUrl = configuration.computeFinalLoginUrl(context);
        final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
        final String redirectionUrl = CommonUtils.constructRedirectUrl(computeLoginUrl, CasConfiguration.SERVICE_PARAMETER,
                computedCallbackUrl, configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl: {}", redirectionUrl);
        return new FoundAction(redirectionUrl);
    }
}
