package org.pac4j.cas.redirect;

import org.jasig.cas.client.util.CommonUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS redirect action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasRedirectActionBuilder extends InitializableObject implements RedirectActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CasRedirectActionBuilder.class);

    protected final CasConfiguration configuration;

    private final CallbackUrlResolver callbackUrlResolver;

    private final String clientName;

    private final String callbackUrl;

    public CasRedirectActionBuilder(final CasConfiguration configuration, final CallbackUrlResolver callbackUrlResolver,
                                    final String clientName, final String callbackUrl) {
        this.configuration = configuration;
        this.callbackUrlResolver = callbackUrlResolver;
        this.clientName = clientName;
        this.callbackUrl = callbackUrl;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("callbackUrlResolver", callbackUrlResolver);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotBlank("clientName", clientName);
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init();
    }

    @Override
    public RedirectAction redirect(final WebContext context) {
        init();

        final String computeLoginUrl = configuration.computeFinalLoginUrl(context);
        final String computedCallbackUrl = callbackUrlResolver.compute(callbackUrl, clientName, context);
        final String redirectionUrl = CommonUtils.constructRedirectUrl(computeLoginUrl, CasConfiguration.SERVICE_PARAMETER,
                computedCallbackUrl, configuration.isRenew(), configuration.isGateway());
        logger.debug("redirectionUrl: {}", redirectionUrl);
        return RedirectAction.redirect(redirectionUrl);
    }
}
