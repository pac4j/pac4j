package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.TemporaryRedirectAction;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Google logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class GoogleLogoutActionBuilder implements LogoutActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GoogleLogoutActionBuilder.class);

    @Override
    public RedirectionAction getLogoutAction(final WebContext context, final UserProfile currentProfile, final String targetUrl) {

        final String redirectUrl = "https://accounts.google.com/Logout";
        logger.debug("redirectUrl: {}", redirectUrl);
        return new TemporaryRedirectAction(redirectUrl);
    }
}
