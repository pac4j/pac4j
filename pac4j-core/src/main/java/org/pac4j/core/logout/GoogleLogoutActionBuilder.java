package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Google logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class GoogleLogoutActionBuilder<U extends CommonProfile> implements LogoutActionBuilder<U> {

    private static final Logger logger = LoggerFactory.getLogger(GoogleLogoutActionBuilder.class);

    @Override
    public Optional<RedirectAction> getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        final String redirectUrl = "https://accounts.google.com/Logout";
        logger.debug("redirectUrl: {}", redirectUrl);
        return Optional.of(RedirectAction.redirect(redirectUrl));
    }
}
