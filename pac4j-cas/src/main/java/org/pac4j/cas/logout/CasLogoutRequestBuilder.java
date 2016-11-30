package org.pac4j.cas.logout;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.LogoutRequestBuilder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS logout request builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasLogoutRequestBuilder extends InitializableWebObject implements LogoutRequestBuilder<CommonProfile> {

    private static final Logger logger = LoggerFactory.getLogger(CasLogoutRequestBuilder.class);

    private final CasConfiguration configuration;

    public CasLogoutRequestBuilder(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init(context);
    }

    @Override
    public RedirectAction getLogoutRequest(final WebContext context, final CommonProfile profile) {
        init(context);

        final String logoutUrl = configuration.getCallbackUrlResolver().compute(configuration.getPrefixUrl() + "logout", context);
        logger.debug("logoutUrl: {}", logoutUrl);
        return RedirectAction.redirect(logoutUrl, configuration.isSupportFrontChannelLogout());
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration);
    }
}
