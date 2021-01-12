package org.pac4j.core.logout;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.context.WebContext;

import java.util.Optional;

/**
 * No {@link RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutActionBuilder implements LogoutActionBuilder {

    public static final NoLogoutActionBuilder INSTANCE = new NoLogoutActionBuilder();

    @Override
    public Optional<RedirectionAction> getLogoutAction(final WebContext context, final SessionStore sessionStore,
                                                       final UserProfile currentProfile, final String targetUrl) {
        return Optional.empty();
    }
}
