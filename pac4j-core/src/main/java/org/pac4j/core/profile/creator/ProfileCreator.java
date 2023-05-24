package org.pac4j.core.profile.creator;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * This interface is responsible to create a {@link UserProfile}
 * from a {@link Credentials}.
 * Return <code>Optional.empty()</code> if no profile can be found / created.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@FunctionalInterface
public interface ProfileCreator {

    /**
     * Create a profile from a credentials.
     *
     * @param ctx the context
     * @param credentials the given credentials
     * @return the created profile (optional)
     */
    Optional<UserProfile> create(CallContext ctx, Credentials credentials);
}
