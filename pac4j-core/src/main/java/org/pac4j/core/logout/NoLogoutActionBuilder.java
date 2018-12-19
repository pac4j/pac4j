package org.pac4j.core.logout;

import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * No {@link RedirectAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoLogoutActionBuilder implements LogoutActionBuilder {

    public static final NoLogoutActionBuilder INSTANCE = new NoLogoutActionBuilder();

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final CommonProfile currentProfile, final String targetUrl) {
        return null;
    }
}
