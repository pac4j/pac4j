package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

import java.util.*;

/**
 * <p>This class is a generic way to manage the current user profile(s), i.e. the one(s) of the current authenticated user.</p>
 * <p>It may be partially re-implemented for specific needs / frameworks.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ProfileManager {

    protected final WebContext context;

    public ProfileManager(final WebContext context) {
        this.context = context;
    }

    /**
     * Retrieve the first user profile if it exists.
     *
     * @param readFromSession if the user profile must be read from session
     * @return the user profile
     */
    public Optional<UserProfile> get(final boolean readFromSession) {
        final LinkedHashMap<String, UserProfile> profiles = retrieveAll(readFromSession);
        if (profiles.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(profiles.values().iterator().next());
        }
    }

    /**
     * Retrieve all user profiles.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the user profiles.
     */
    public List<UserProfile> getAll(final boolean readFromSession) {
        final LinkedHashMap<String, UserProfile> profiles = retrieveAll(readFromSession);
        if (profiles == null) {
            return Collections.unmodifiableList(new ArrayList<>());
        } else {
            final List<UserProfile> listProfiles = new ArrayList<>();
            for (final String key : profiles.keySet()) {
                listProfiles.add(profiles.get(key));
            }
            return Collections.unmodifiableList(listProfiles);
        }
    }

    private LinkedHashMap<String, UserProfile> retrieveAll(final boolean readFromSession) {
        LinkedHashMap<String, UserProfile> profiles = null;
        final Object objSession = this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        if (objSession != null && objSession instanceof LinkedHashMap) {
            profiles = (LinkedHashMap<String, UserProfile>) objSession;
        }
        if ((profiles == null || profiles.isEmpty()) && readFromSession) {
            final Object objRequest = this.context.getSessionAttribute(Pac4jConstants.USER_PROFILES);
            if (objRequest != null && objRequest instanceof LinkedHashMap) {
                profiles = (LinkedHashMap<String, UserProfile>) objRequest;
            }
        }
        if (profiles == null) {
            return new LinkedHashMap<>();
        } else {
            return profiles;
        }
    }

    /**
     * Remove the current user profile(s).
     *
     * @param removeFromSession if the user profile(s) must be removed from session
     */
    public void remove(final boolean removeFromSession) {
        if (removeFromSession) {
            this.context.setSessionAttribute(Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, UserProfile>());
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, UserProfile>());
    }

    /**
     * Save the given user profile (replace the current one if multi profiles are not supported, add it otherwise).
     *
     * @param saveInSession if the user profile must be saved in session
     * @param profile a given user profile
     * @param multiProfile whether multiple profiles are supported
     */
    public void save(final boolean saveInSession, final UserProfile profile, final boolean multiProfile) {
        final LinkedHashMap<String, UserProfile> profiles;

        String clientName = profile.getClientName();
        if (clientName == null) {
            clientName = "DEFAULT";
        }
        if (multiProfile) {
            profiles = retrieveAll(saveInSession);
            profiles.remove(clientName);
        } else {
            profiles = new LinkedHashMap<>();
        }
        profiles.put(clientName, profile);

        if (saveInSession) {
            this.context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
    }

    /**
     * Perform a logout by removing the current user profile(s) from the session as well.
     */
    public void logout() {
        remove(true);
    }

    /**
     * Tests if the current user is authenticated (meaning a user profile exists).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        return get(true).isPresent();
    }
}
