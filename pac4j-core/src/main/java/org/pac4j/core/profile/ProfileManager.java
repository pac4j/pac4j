package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

/**
 * <p>This class is a generic way to manage the current user profile, i.e. the one of the current authenticated user.</p>
 * <p>It may be partially re-implemented for specific needs / frameworks.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ProfileManager<U extends UserProfile> {

    protected final WebContext context;

    public ProfileManager(final WebContext context) {
        this.context = context;
    }

    /**
     * Retrieve the current user profile (from request first and then from the session if not found and requested).
     *
     * @param readFromSession if the user profile must be read from session
     * @return the user profile
     */
    public U get(final boolean readFromSession) {
        U profile = null;
        final Object objSession = this.context.getRequestAttribute(Pac4jConstants.USER_PROFILE);
        if (objSession != null && objSession instanceof UserProfile) {
            profile = (U) objSession;
        }
        if (profile == null && readFromSession) {
            final Object objRequest = this.context.getSessionAttribute(Pac4jConstants.USER_PROFILE);
            if (objRequest != null && objRequest instanceof UserProfile) {
                profile = (U) objRequest;
            }
        }
        return profile;
    }

    /**
     * Remove the current user profile.
     *
     * @param removeFromSession if the user profile must be removed from session
     */
    public void remove(final boolean removeFromSession) {
        if (removeFromSession) {
            this.context.setSessionAttribute(Pac4jConstants.USER_PROFILE, "");
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILE, "");
    }

    /**
     * Save the given user profile as the current one.
     *
     * @param saveInSession if the user profile must be saved in session
     * @param profile a given user profile
     */
    public void save(final boolean saveInSession, final U profile) {
        if (saveInSession) {
            this.context.setSessionAttribute(Pac4jConstants.USER_PROFILE, profile);
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILE, profile);
    }

    /**
     * Perform a logout by removing the current user profile from the session as well.
     */
    public void logout() {
        remove(true);
    }

    /**
     * Tests if the current is authenticated (meaning a user profile exists).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        return get(true) != null;
    }
}
