package org.pac4j.core.profile.creator;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;

/**
 * This interface is responsible to create a {@link UserProfile} from a {@link Credentials}.
 * Return <code>null</code> if no profile can be found / created.
 * 
 * @author Jerome Leleu
 * @since 1.7.0
 */
public interface ProfileCreator<C extends Credentials, U extends UserProfile> {

    /**
     * Create a profile from a credentials.
     *
     * @param credentials the given credentials.
     * @return the created profile
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    U create(C credentials) throws RequiresHttpAction;
}
