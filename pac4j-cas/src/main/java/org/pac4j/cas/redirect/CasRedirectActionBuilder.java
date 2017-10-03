package org.pac4j.cas.redirect;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS redirect action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasRedirectActionBuilder implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CasRedirectActionBuilder.class);

    protected CasConfiguration configuration;

    protected CasClient client;

    public CasRedirectActionBuilder(final CasConfiguration configuration, final CasClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public RedirectAction redirect(final WebContext context) {
        final String computeLoginUrl = configuration.computeFinalLoginUrl(context);
        final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
        final String redirectionUrl = CommonUtils.constructRedirectUrl(computeLoginUrl, CasConfiguration.SERVICE_PARAMETER,
                computedCallbackUrl, configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl: {}", redirectionUrl);
        return RedirectAction.redirect(redirectionUrl);
    }
}
