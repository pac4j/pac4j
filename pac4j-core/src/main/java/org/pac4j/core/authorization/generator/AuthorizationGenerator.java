package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * Generate the authorization roles for this user profile.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
@FunctionalInterface
public interface AuthorizationGenerator {

    /**
     * Generate the authorization information from and for the user profile.
     *
     * @param ctx the current context
     * @param profile the user profile for which to generate the authorization information.
     * @return the updated profile or a new one (optional)
     */
    Optional<UserProfile> generate(CallContext ctx, UserProfile profile);
}
