package org.pac4j.core.profile;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileManager.class);

    private final Authorizer IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final WebContext context;

    protected SessionStore sessionStore;

    protected Config config;

    public ProfileManager(final WebContext context, final SessionStore sessionStore) {
        CommonHelper.assertNotNull("context", context);
        CommonHelper.assertNotNull("sessionStore", sessionStore);
        this.context = context;
        this.sessionStore = sessionStore;
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     *
     * @return the user profile
     */
    public Optional<UserProfile> getProfile() {
        final var allProfiles = retrieveAll(true);
        return ProfileHelper.flatIntoOneProfile(allProfiles.values());
    }

    public <U extends UserProfile> Optional<U> getProfile(final Class<U> clazz) {
        return (Optional<U>) getProfile();
    }

    /**
     * Retrieve all user profiles.
     *
     * @return the user profiles
     */
    public List<UserProfile> getProfiles() {
        final var profiles = retrieveAll(true);
        return ProfileHelper.flatIntoAProfileList(profiles);
    }

    /**
     * Retrieve the map of profiles from the session or the request.
     *
     * @param readFromSession if the user profiles must be read from session
     * @return the map of profiles
     */
    protected LinkedHashMap<String, UserProfile> retrieveAll(final boolean readFromSession) {
        final var profiles = new LinkedHashMap<String, UserProfile>();
        this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES)
            .ifPresent(requestAttribute -> {
                LOGGER.debug("Retrieved profiles (request): {}", requestAttribute);
                profiles.putAll((Map<String, UserProfile>) requestAttribute);
            });
        if (readFromSession) {
            this.sessionStore.get(this.context, Pac4jConstants.USER_PROFILES)
                .ifPresent(sessionAttribute -> {
                    LOGGER.debug("Retrieved profiles (session): {}", sessionAttribute);
                    profiles.putAll((Map<String, UserProfile>) sessionAttribute);
                });
        }

        removeOrRenewExpiredProfiles(profiles, readFromSession);

        return profiles;
    }

    protected void removeOrRenewExpiredProfiles(final LinkedHashMap<String, UserProfile> profiles, final boolean readFromSession) {
        var profilesUpdated = false;
        for (final var entry : profiles.entrySet()) {
            final var key = entry.getKey();
            final var profile = entry.getValue();
            if (profile.isExpired()) {
                LOGGER.debug("Expired profile: {}", profile);
                profilesUpdated = true;
                profiles.remove(key);
                if (config != null && profile.getClientName() != null) {
                    final var client = config.getClients().findClient(profile.getClientName());
                    if (client.isPresent()) {
                        try {
                            final var newProfile = client.get().renewUserProfile(profile, context, sessionStore);
                            if (newProfile.isPresent()) {
                                LOGGER.debug("Renewed by profile: {}", newProfile);
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
     */
    public void removeProfiles() {
        final var sessionExists = sessionStore.getSessionId(context, false).isPresent();
        if (sessionExists) {
            LOGGER.debug("Removing profiles from session");
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, UserProfile>());
        }
        LOGGER.debug("Removing profiles from request");
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

        final var clientName = retrieveClientName(profile);
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
        var clientName = profile.getClientName();
        if (clientName == null) {
            clientName = "DEFAULT";
        }
        return clientName;
    }

    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        if (saveInSession) {
            LOGGER.debug("Saving profiles (session): {}", profiles);
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, profiles);
        }
        LOGGER.debug("Saving profiles (request): {}", profiles);
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
    }

    /**
     * Tests if the current user is authenticated (meaning a user profile exists which is not an {@link AnonymousProfile}).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        try {
            return IS_AUTHENTICATED_AUTHORIZER.isAuthorized(context, sessionStore, getProfiles());
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
