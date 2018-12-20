package org.pac4j.core.logout;

import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.context.WebContext;

/**
 * No {@link RedirectionAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutActionBuilder implements LogoutActionBuilder {

    public static final NoLogoutActionBuilder INSTANCE = new NoLogoutActionBuilder();

    @Override
    public RedirectionAction getLogoutAction(final WebContext context, final UserProfile currentProfile, final String targetUrl) {
        return null;
    }
}
