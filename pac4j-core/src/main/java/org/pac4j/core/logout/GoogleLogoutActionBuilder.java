package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Google logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class GoogleLogoutActionBuilder implements LogoutActionBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GoogleLogoutActionBuilder.class);

    @Override
    public Optional<RedirectionAction> getLogoutAction(final WebContext context, final UserProfile currentProfile,
                                                       final String targetUrl) {

        final String redirectUrl = "https://accounts.google.com/Logout";
        logger.debug("redirectUrl: {}", redirectUrl);
        return Optional.of(new FoundAction(redirectUrl));
    }
}
