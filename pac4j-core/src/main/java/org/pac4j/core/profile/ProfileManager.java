package org.pac4j.core.profile;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class is a generic way to manage the current user profile(s), i.e. the one(s) of the current authenticated user.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ProfileManager {

    private final Authorizer<UserProfile> IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final WebContext context;

    protected final SessionStore sessionStore;

    protected Config config;

    public ProfileManager(final WebContext context) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
        this.sessionStore = context.getSessionStore();
    }

    public ProfileManager(final WebContext context, final SessionStore sessionStore) {
        CommonHelper.assertNotNull("context", context);
        CommonHelper.assertNotNull("sessionStore", sessionStore);
        this.context = context;
        this.sessionStore = sessionStore;
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     *
     * @param readFromSession if the user profile must be read from session
     * @return the user profile
     */
    public Optional<UserProfile> get(final boolean readFromSession) {
        final LinkedHashMap<String, UserProfile> allProfiles = retrieveAll(readFromSession);
        return ProfileHelper.flatIntoOneProfile(allProfiles.values());
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     * From the session or not, depending on the behavior which occured in the {@link org.pac4j.core.engine.DefaultSecurityLogic}.
     *
     * @param readFromSessionDefault if the user profile must be read from session otherwise
     * @return the user profile
     */
    public Optional<UserProfile> getLikeDefaultSecurityLogic(final boolean readFromSessionDefault) {
        return get(retrieveLoadProfilesFromSession(readFromSessionDefault));
    }

    /**
     * Compute whether we must read the user profiles from the session.
     * (depending on the {@link org.pac4j.core.engine.DefaultSecurityLogic} behavior).
     *
     * @param readFromSessionDefault if the user profile(s) must be read from session otherwise
     * @return whether we must read the user profiles from the session
     */
    protected boolean retrieveLoadProfilesFromSession(final boolean readFromSessionDefault) {
        final Optional<Boolean> loadProfilesFromSession = context.getRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION);
        if (loadProfilesFromSession.isPresent()) {
            return loadProfilesFromSession.get();
        } else {
            return readFromSessionDefault;
        }
    }

    /**
     * Retrieve all user profiles.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the user profiles
     */
    public List<UserProfile> getAll(final boolean readFromSession) {
        final LinkedHashMap<String, UserProfile> profiles = retrieveAll(readFromSession);
        return ProfileHelper.flatIntoAProfileList(profiles);
    }

    /**
     * Retrieve all user profiles.
     * From the session or not, depending on the behavior which occured in the {@link org.pac4j.core.engine.DefaultSecurityLogic}.
     *
     * @param readFromSessionDefault if the user profiles must be read from session otherwise
     * @return the user profiles
     */
    public List<UserProfile> getAllLikeDefaultSecurityLogic(final boolean readFromSessionDefault) {
        return getAll(retrieveLoadProfilesFromSession(readFromSessionDefault));
    }

    /**
     * Retrieve the map of profiles from the session or the request.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the map of profiles
     */
    protected LinkedHashMap<String, UserProfile> retrieveAll(final boolean readFromSession) {
        final LinkedHashMap<String, UserProfile> profiles = new LinkedHashMap<>();
        this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES)
            .ifPresent(request -> {
                if  (request instanceof LinkedHashMap) {
                    profiles.putAll((LinkedHashMap<String, UserProfile>) request);
                }
                if (request instanceof CommonProfile) {
                    profiles.put(retrieveClientName((UserProfile) request), (UserProfile) request);
                }
            });
        if (readFromSession) {
            this.sessionStore.get(this.context, Pac4jConstants.USER_PROFILES)
                .ifPresent(sessionAttribute -> {
                    if (sessionAttribute instanceof LinkedHashMap) {
                        profiles.putAll((LinkedHashMap<String, UserProfile>) sessionAttribute);
                    }
                    if (sessionAttribute instanceof CommonProfile) {
                        profiles.put(retrieveClientName((UserProfile) sessionAttribute), (UserProfile) sessionAttribute);
                    }
                });
        }

        removeOrRenewExpiredProfiles(profiles, readFromSession);

        return profiles;
    }

    protected void removeOrRenewExpiredProfiles(final LinkedHashMap<String, UserProfile> profiles, final boolean readFromSession) {
        boolean profilesUpdated = false;
        for (final Map.Entry<String, UserProfile> entry : profiles.entrySet()) {
            final String key = entry.getKey();
            final UserProfile profile = entry.getValue();
            if (profile.isExpired()) {
                profilesUpdated = true;
                profiles.remove(key);
                if (config != null && profile.getClientName() != null) {
                    final Optional<Client> client = config.getClients().findClient(profile.getClientName());
                    if (client.isPresent()) {
                        try {
                            final Optional<UserProfile> newProfile = client.get().renewUserProfile(profile, context);
                            if (newProfile.isPresent()) {
                                profiles.put(key, newProfile.get());
                            }
                        } catch (final RuntimeException e) {
                            logger.error("Unable to renew the user profile for key: {}", key, e);
                        }
                    }
                }
            }
        }
        if (profilesUpdated) {
            saveAll(profiles, readFromSession);
        }
    }

    /**
     * Remove the current user profile(s).
     *
     * @param removeFromSession if the user profile(s) must be removed from session
     */
    public void remove(final boolean removeFromSession) {
        if (removeFromSession) {
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, UserProfile>());
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

        final String clientName = retrieveClientName(profile);
        if (multiProfile) {
            profiles = retrieveAll(saveInSession);
            profiles.remove(clientName);
        } else {
            profiles = new LinkedHashMap<>();
        }
        profiles.put(clientName, profile);

        saveAll(profiles, saveInSession);
    }

    protected String retrieveClientName(final UserProfile profile) {
        String clientName = profile.getClientName();
        if (clientName == null) {
            clientName = "DEFAULT";
        }
        return clientName;
    }

    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        if (saveInSession) {
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, profiles);
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
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

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }
}
