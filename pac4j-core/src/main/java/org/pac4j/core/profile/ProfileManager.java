package org.pac4j.core.profile;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;

import java.util.*;

/**
 * This class is a generic way to manage the current user profile(s), i.e. the one(s) of the current authenticated user.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ProfileManager<U extends CommonProfile> {

    private final Authorizer<U> IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer<U>();

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
        return ProfileHelper.flatIntoOneProfile(allProfiles);
    }

    /**
     * Retrieve all user profiles.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the user profiles
     */
    public List<U> getAll(final boolean readFromSession) {
        final LinkedHashMap<String, U> profiles = retrieveAll(readFromSession);
        return ProfileHelper.flatIntoAProfileList(profiles);
    }

    /**
     * Retrieve the map of profiles from the session or the request.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the map of profiles
     */
    protected LinkedHashMap<String, U> retrieveAll(final boolean readFromSession) {
        final LinkedHashMap<String, U> profiles = new LinkedHashMap<>();
        final Object request = this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        if (request != null) {
            if  (request instanceof LinkedHashMap) {
                profiles.putAll((LinkedHashMap<String, U>) request);
            }
            if (request instanceof CommonProfile) {
                profiles.put(retrieveClientName((U) request), (U) request);
            }
        }
        if (readFromSession) {
            final Object sessionAttribute = this.context.getSessionAttribute(Pac4jConstants.USER_PROFILES);
            if  (sessionAttribute instanceof LinkedHashMap) {
                profiles.putAll((LinkedHashMap<String, U>) sessionAttribute);
            }
            if (sessionAttribute instanceof CommonProfile) {
                profiles.put(retrieveClientName((U) sessionAttribute), (U) sessionAttribute);
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

        final String clientName = retrieveClientName(profile);
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

    protected String retrieveClientName(final U profile) {
        String clientName = profile.getClientName();
        if (clientName == null) {
            clientName = "DEFAULT";
        }
        return clientName;
    }

    /**
     * Perform a logout by removing the current user profile(s).
     */
    public void logout() {
        remove(true);
    }

    /**
     * Tests if the current user is authenticated (meaning a user profile exists which is not an {@link AnonymousProfile}).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        try {
            return IS_AUTHENTICATED_AUTHORIZER.isAuthorized(null, getAll(true));
        } catch (final HttpAction e) {
            throw new TechnicalException(e);
        }
    }
}
