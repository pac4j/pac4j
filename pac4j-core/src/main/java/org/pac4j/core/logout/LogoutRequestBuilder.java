package org.pac4j.core.logout;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * Return a logout request to perform.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface LogoutRequestBuilder<U extends CommonProfile> {

    /**
     * Get the request to perform the logout at the identity provider level
     * (given the current web context and authenticated user profile).
     *
     * @param context the web context
     * @param profile the current profile
     * @return the logout request
     */
    LogoutRequest getLogoutRequest(WebContext context, U profile);
}
