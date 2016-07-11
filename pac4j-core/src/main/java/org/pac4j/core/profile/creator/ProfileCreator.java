package org.pac4j.core.profile.creator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * This interface is responsible to create a {@link CommonProfile} from a {@link Credentials}.
 * Return <code>null</code> if no profile can be found / created.
 * 
 * @author Jerome Leleu
 * @since 1.7.0
 */
public interface ProfileCreator<C extends Credentials, U extends CommonProfile> {

    /**
     * Create a profile from a credentials.
     *
     * @param credentials the given credentials
     * @param context the web context
     * @return the created profile
     * @throws HttpAction whether an additional HTTP action is required
     */
    U create(C credentials, WebContext context) throws HttpAction;
}
