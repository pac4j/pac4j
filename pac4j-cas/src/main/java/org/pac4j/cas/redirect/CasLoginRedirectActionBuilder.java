package org.pac4j.cas.redirect;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redirection for CAS login.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasLoginRedirectActionBuilder implements RedirectActionBuilder {

    private final static Logger logger = LoggerFactory.getLogger(CasLoginRedirectActionBuilder.class);

    protected static final String SERVICE_PARAMETER = "service";

    private final CasConfiguration configuration;

    private final String callbackUrl;

    public CasLoginRedirectActionBuilder(final CasConfiguration configuration, final String callbackUrl) {
        this.configuration = configuration;
        this.callbackUrl = callbackUrl;
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
    }

    public RedirectAction redirect(final WebContext context) throws HttpAction {
        final String redirectionUrl = CommonUtils.constructRedirectUrl(configuration.getLoginUrl(), SERVICE_PARAMETER,
                configuration.getCallbackUrlResolver().compute(callbackUrl, context), configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl: {}", redirectionUrl);
        return RedirectAction.redirect(redirectionUrl);
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "callbackUrl", callbackUrl);
    }
}
