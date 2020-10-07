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
public class ProfileManager<U extends UserProfile> {

    private final Authorizer IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final WebContext context;

    protected SessionStore sessionStore;

    protected Config config;

    public ProfileManager(final WebContext context) {
        CommonHelper.assertNotNull("context", context);
        this.context = context;
        this.sessionStore = context.getSessionStore();
    }

    public void setSessionStore(final SessionStore sessionStore) {
        CommonHelper.assertNotNull("sessionStore", sessionStore);
        this.sessionStore = sessionStore;
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     *
     * @return the user profile
     */
    public Optional<U> getProfile() {
        return get(true);
    }

    /**
     * Use {@link #getProfile()}.
     */
    @Deprecated
    public Optional<U> get(final boolean readFromSession) {
        final LinkedHashMap<String, U> allProfiles = retrieveAll(readFromSession);
        return ProfileHelper.flatIntoOneProfile(allProfiles.values());
    }

    /**
     * Retrieve all user profiles.
     *
     * @return the user profiles
     */
    public List<U> getProfiles() {
        return getAll(true);
    }

    /**
     * Use {@link #getProfiles()}.
     */
    @Deprecated
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
        this.context.getRequestAttribute(Pac4jConstants.USER_PROFILES)
            .ifPresent(requestAttribute -> profiles.putAll((Map<String, U>) requestAttribute));
        if (readFromSession) {
            this.sessionStore.get(this.context, Pac4jConstants.USER_PROFILES)
                .ifPresent(sessionAttribute -> profiles.putAll((Map<String, U>) sessionAttribute));
        }

        removeOrRenewExpiredProfiles(profiles, readFromSession);

        return profiles;
    }

    protected void removeOrRenewExpiredProfiles(final LinkedHashMap<String, U> profiles, final boolean readFromSession) {
        boolean profilesUpdated = false;
        for (final Map.Entry<String, U> entry : profiles.entrySet()) {
            final String key = entry.getKey();
            final U profile = entry.getValue();
            if (profile.isExpired()) {
                profilesUpdated = true;
                profiles.remove(key);
                if (config != null && profile.getClientName() != null) {
                    final Optional<Client> client = config.getClients().findClient(profile.getClientName());
                    if (client.isPresent()) {
                        try {
                            final Optional<UserProfile> newProfile = client.get().renewUserProfile(profile, context);
                            if (newProfile.isPresent()) {
                                profiles.put(key, (U) newProfile.get());
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
        final boolean sessionExists = context.getSessionStore().getSessionId(context, false).isPresent();
        remove(sessionExists);
    }

    /**
     * Use {@link #removeProfiles()}.
     */
    @Deprecated
    public void remove(final boolean removeFromSession) {
        if (removeFromSession) {
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, new LinkedHashMap<String, U>());
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

        saveAll(profiles, saveInSession);
    }

    protected String retrieveClientName(final U profile) {
        String clientName = profile.getClientName();
        if (clientName == null) {
            clientName = "DEFAULT";
        }
        return clientName;
    }

    protected void saveAll(LinkedHashMap<String, U> profiles, final boolean saveInSession) {
        if (saveInSession) {
            this.sessionStore.set(this.context, Pac4jConstants.USER_PROFILES, profiles);
        }
        this.context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
    }

    /**
     * Tests if the current user is authenticated (meaning a user profile exists which is not an {@link AnonymousProfile}).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        try {
            return IS_AUTHENTICATED_AUTHORIZER.isAuthorized(null, (List<UserProfile>) getProfiles());
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
