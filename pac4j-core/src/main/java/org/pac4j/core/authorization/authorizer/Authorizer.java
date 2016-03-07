package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

/**
 * Checks if an access is authorized.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface Authorizer<U extends UserProfile> {

    /**
     * Checks if the user profile is authorized for the current web context.
     *
     * @param context the web context
     * @param profile the user profile
     * @return if the access is authorized
     */
    boolean isAuthorized(WebContext context, U profile);
}
