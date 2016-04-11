package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

import java.util.*;

/**
 * This class is a generic way to manage the current user profile(s), i.e. the one(s) of the current authenticated user.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ProfileManager<U extends CommonProfile> {

    protected final WebContext context;

    public ProfileManager(final WebContext context) {
        this.context = context;
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     *
     * @param readFromSession if the user profile must be read from session
     * @return the user profile
     */
    public Optional<U> get(final boolean readFromSession) {
        final LinkedHashMap<String, U> allProfiles = retrieveAll(readFromSession);
        if (allProfiles.size() == 0) {
            return Optional.empty();
        } else {
            U profile = null;
            final Iterator<U> profiles = allProfiles.values().iterator();
            while (profiles.hasNext()) {
                final U nextProfile = profiles.next();
                if (profile == null || profile instanceof AnonymousProfile) {
                    profile = nextProfile;
                }
            }
            return Optional.of(profile);
        }
    }

    /**
     * Retrieve all user profiles.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the user profiles.
     */
    public List<U> getAll(final boolean readFromSession) {
        final LinkedHashMap<String, U> profiles = retrieveAll(readFromSession);
        final List<U> listProfiles = new ArrayList<>();
        for (final Map.Entry<String, U> entry : profiles.entrySet()) {
            listProfiles.add(entry.getValue());
        }
        return Collections.unmodifiableList(listProfiles);
    }

    private LinkedHashMap<String, U> retrieveAll(final boolean readFromSession) {
        LinkedHashMap<String, U> profiles = new LinkedHashMap<>();
        final Object objSession = this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        if (objSession != null && objSession instanceof LinkedHashMap) {
            profiles = (LinkedHashMap<String, U>) objSession;
        }
        if (readFromSession) {
            final Object objRequest = this.context.getSessionAttribute(Pac4jConstants.USER_PROFILES);
            if (objRequest != null && objRequest instanceof LinkedHashMap) {
                profiles.putAll((LinkedHashMap<String, U>) objRequest);
            }
        }
        return profiles;
    }

    /**
     * Remove the current user profile(s).
     *
     * @param removeFromSession if the user profile(s) must be removed from session
     */
    public void remove(final boolean removeFromSession) {
        if (removeFromSession) {
            this.context.setSessionAttribute(Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, U>());
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, U>());
    }

    /**
     * Save the given user profile (replace the current one if multi profiles are not supported, add it otherwise).
     *
     * @param saveInSession if the user profile must be saved in session
     * @param profile a given user profile
     * @param multiProfile whether multiple profiles are supported
     */
    public void save(final boolean saveInSession, final U profile, final boolean multiProfile) {
        final LinkedHashMap<String, U> profiles;

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
     * Tests if the current user has a profile (meaning he is authenticated, except if the profile is an {@link AnonymousProfile}).
     *
     * @return whether the current user has a profile
     */
    public boolean hasProfile() {
        return get(true).isPresent();
    }
}
