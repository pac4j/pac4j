package org.pac4j.core.logout;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.context.WebContext;

/**
 * The {@link RedirectAction} for logout.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface LogoutActionBuilder {

    /**
     * Return the {@link RedirectAction} for logout.
     *
     * @param context the web context
     * @param currentProfile the current profile
     * @param targetUrl the target URL after logout
     * @return the redirection
     */
    RedirectAction getLogoutAction(WebContext context, UserProfile currentProfile, String targetUrl);
}
