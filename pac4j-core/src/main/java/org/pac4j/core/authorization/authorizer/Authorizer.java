package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * Checks if an access is authorized.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface Authorizer<U extends CommonProfile> {

    /**
     * Checks if the user profiles and / or the current web context are authorized.
     *
     * @param context the web context
     * @param profiles the user profiles
     * @return if the access is authorized
     */
    boolean isAuthorized(WebContext context, List<U> profiles);
}
