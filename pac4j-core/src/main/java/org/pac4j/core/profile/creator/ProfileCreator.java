package org.pac4j.core.profile.creator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * This interface is responsible to create a {@link UserProfile} from a {@link Credentials}.
 * Return <code>Optional.empty()</code> if no profile can be found / created.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
public interface ProfileCreator {

    /**
     * Create a profile from a credentials.
     *
     * @param credentials the given credentials
     * @param context the web context
     * @param sessionStore the session store
     * @return the created profile (optional)
     */
    Optional<UserProfile> create(Credentials credentials, WebContext context, SessionStore sessionStore);
}
