package org.pac4j.cas.logout;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasLogoutActionBuilder extends InitializableWebObject implements LogoutActionBuilder<CommonProfile> {

    private static final Logger logger = LoggerFactory.getLogger(CasLogoutActionBuilder.class);

    private final CasConfiguration configuration;

    public CasLogoutActionBuilder(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.init(context);
    }

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final CommonProfile currentProfile, final String targetUrl) {
        init(context);

        String logoutUrl = configuration.getCallbackUrlResolver().compute(configuration.getPrefixUrl() + "logout", context);
        if (CommonHelper.isNotBlank(targetUrl)) {
            logoutUrl = CommonHelper.addParameter(logoutUrl, configuration.getPostLogoutUrlParameter(), targetUrl);
        }
        logger.debug("logoutUrl: {}", logoutUrl);
        return RedirectAction.redirect(logoutUrl);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration);
    }
}