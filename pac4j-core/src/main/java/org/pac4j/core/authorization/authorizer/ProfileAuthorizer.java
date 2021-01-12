package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Authorizer which is valid if one of the profiles is authorized or all the profiles are authorized.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class ProfileAuthorizer implements Authorizer {

    /**
     * If all profiles are authorized.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profiles the user profiles
     * @return whether all profiles are authorized
     */
    public boolean isAllAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        for (final UserProfile profile : profiles) {
            if (!isProfileAuthorized(context, sessionStore, profile)) {
                return handleError(context, sessionStore);
            }
        }
        return true;
    }

    /**
     * If any of the profiles is authorized.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profiles the user profiles
     * @return whether any of the profiles is authorized
     */
    public boolean isAnyAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        for (final UserProfile profile : profiles) {
            if (isProfileAuthorized(context, sessionStore, profile)) {
                return true;
            }
        }
        return handleError(context, sessionStore);
    }

    /**
     * Whether a specific profile is authorized.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profile the user profile
     * @return whether a specific profile is authorized
     */
    protected abstract boolean isProfileAuthorized(WebContext context, SessionStore sessionStore, UserProfile profile);

    /**
     * Handle the error.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @return <code>false</code>
     */
    protected boolean handleError(final WebContext context, final SessionStore sessionStore) {
        return false;
    }
}
