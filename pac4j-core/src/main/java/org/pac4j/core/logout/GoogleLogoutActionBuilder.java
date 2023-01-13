package org.pac4j.core.logout;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.HttpActionHelper;

import java.util.Optional;

/**
 * Google logout action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Slf4j
public class GoogleLogoutActionBuilder implements LogoutActionBuilder {

    @Override
    public Optional<RedirectionAction> getLogoutAction(final WebContext context, final SessionStore sessionStore,
                                                       final ProfileManagerFactory profileManagerFactory,
                                                       final UserProfile currentProfile, final String targetUrl) {

        val redirectUrl = "https://accounts.google.com/Logout";
        LOGGER.debug("redirectUrl: {}", redirectUrl);
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, redirectUrl));
    }
}
