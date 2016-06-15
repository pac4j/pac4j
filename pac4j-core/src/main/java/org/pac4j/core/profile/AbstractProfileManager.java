package org.pac4j.core.profile;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;

import java.util.*;

/**
 * Abstract profile manager.
 *
 * @author Jerome Leleu
 * @since 1.9.1
 */
public abstract class AbstractProfileManager<U extends CommonProfile> {

    protected final WebContext context;

    protected AbstractProfileManager(final WebContext context) {
        this.context = context;
    }

    /**
     * Retrieve the first user profile if it exists, ignoring any {@link AnonymousProfile} if possible.
     *
     * @param readFromSession if the user profile must be read from session
     * @return the user profile
     */
    protected Optional<U> retrieve(final boolean readFromSession) {
        final LinkedHashMap<String, U> allProfiles = retrieveAllAsLinkedMap(readFromSession);
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
    protected List<U> retrieveAll(final boolean readFromSession) {
        final LinkedHashMap<String, U> profiles = retrieveAllAsLinkedMap(readFromSession);
        final List<U> listProfiles = new ArrayList<>();
        for (final Map.Entry<String, U> entry : profiles.entrySet()) {
            listProfiles.add(entry.getValue());
        }
        return Collections.unmodifiableList(listProfiles);
    }

    /**
     * Retrieve all user profiles as a linked hash map.
     *
     * @param readFromSession  if the user profiles must be read from session
     * @return the user profiles.
     */
    protected LinkedHashMap<String, U> retrieveAllAsLinkedMap(final boolean readFromSession) {
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
     * Tests if the current user is authenticated (meaning a user profile exists which is not an {@link AnonymousProfile}).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        final Optional<U> profile = retrieve(true);
        return profile.isPresent() && !(profile.get() instanceof AnonymousProfile);
    }
}
