package org.pac4j.core.logout;

import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CasLogoutActionBuilder<U extends CommonProfile> implements LogoutActionBuilder<U> {

    private static final Logger logger = LoggerFactory.getLogger(CasLogoutActionBuilder.class);

    private final String serverLogoutUrl;

    private final String postLogoutUrlParameter;

    public CasLogoutActionBuilder(final String serverLogoutUrl, final String postLogoutUrlParameter) {
        if (CommonHelper.isNotBlank(serverLogoutUrl)) {
            CommonHelper.assertNotBlank("postLogoutUrlParameter", postLogoutUrlParameter);
        }
        this.serverLogoutUrl = serverLogoutUrl;
        this.postLogoutUrlParameter = postLogoutUrlParameter;
    }

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        if (CommonHelper.isBlank(serverLogoutUrl)) {
            return null;
        }

        String redirectUrl = serverLogoutUrl;
        if (CommonHelper.isNotBlank(targetUrl)) {
            redirectUrl = CommonHelper.addParameter(redirectUrl, postLogoutUrlParameter, targetUrl);
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        return RedirectAction.redirect(redirectUrl);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "serverLogoutUrl", serverLogoutUrl, "postLogoutUrlParameter", postLogoutUrlParameter);
    }
}
