package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Optional;

/**
 * Generate the authorization roles and permissions for this user profile.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public interface AuthorizationGenerator<U extends CommonProfile> {

    /**
     * Generate the authorization information from and for the user profile.
     *
     * @param context the web context
     * @param profile the user profile for which to generate the authorization information.
     * @return the updated profile or a new one
     */
    Optional<U> generate(WebContext context, U profile);
}
