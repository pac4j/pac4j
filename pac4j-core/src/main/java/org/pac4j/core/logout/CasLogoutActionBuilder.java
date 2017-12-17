package org.pac4j.core.logout;

import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

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
        if (isNotBlank(serverLogoutUrl)) {
            assertNotBlank("postLogoutUrlParameter", postLogoutUrlParameter);
        }
        this.serverLogoutUrl = serverLogoutUrl;
        this.postLogoutUrlParameter = postLogoutUrlParameter;
    }

    @Override
    public Optional<RedirectAction> getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        if (isBlank(serverLogoutUrl)) {
            return Optional.empty();
        }

        String redirectUrl = serverLogoutUrl;
        if (isNotBlank(targetUrl)) {
            redirectUrl = addParameter(redirectUrl, postLogoutUrlParameter, targetUrl);
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        return Optional.of(RedirectAction.redirect(redirectUrl));
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "serverLogoutUrl", serverLogoutUrl, "postLogoutUrlParameter", postLogoutUrlParameter);
    }
}
