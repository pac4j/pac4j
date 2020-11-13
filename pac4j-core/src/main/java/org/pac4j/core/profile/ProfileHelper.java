package org.pac4j.core.profile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.substringBefore;

/**
 * This class is an helper for profiles.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ProfileHelper {

    private static List<String> profileClassPrefixes = Arrays.asList("org.pac4j.");

    private ProfileHelper() {}

    /**
     * Indicate if the user identifier matches this kind of profile.
     *
     * @param id user identifier
     * @param clazz profile class
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(final String id, final Class<? extends UserProfile> clazz) {
        return id != null && clazz != null && id.startsWith(clazz.getName() + Pac4jConstants.TYPED_ID_SEPARATOR);
    }

    /**
     * Build a profile by its class name.
     *
     * @param completeName the class name
     * @return the built user profile
     */
    public static UserProfile buildUserProfileByClassCompleteName(final String completeName) {
        try {
            final Constructor constructor = CommonHelper.getConstructor(completeName);
            return (UserProfile) constructor.newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
                 | InstantiationException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Flat the list of profiles into a single optional profile (skip any anonymous profile unless it's the only one).
     *
     * @param profiles the list of profiles
     * @param <U> the kind of profile
     * @return the (optional) profile
     */
    public static <U extends UserProfile> Optional<U> flatIntoOneProfile(final Collection<U> profiles) {
        final Optional<U> profile = profiles.stream().filter(p -> p != null && !(p instanceof AnonymousProfile)).findFirst();
        if (profile.isPresent()) {
            return profile;
        } else {
            return profiles.stream().filter(p -> p != null).findFirst();
        }
    }

    /**
     * Flat the map of profiles into a list of profiles.
     *
     * @param profiles the map of profiles
     * @param <U> the kind of profile
     * @return the list of profiles
     */
    public static <U extends UserProfile> List<U> flatIntoAProfileList(final Map<String, U> profiles) {
        return new ArrayList<>(profiles.values());
    }

    /**
     * Sanitize into a string identifier.
     *
     * @param id the identifier object
     * @return the sanitized identifier
     */
    public static String sanitizeIdentifier(final Object id) {
        if (id != null) {
            String sId = id.toString();
            if (sId.contains(Pac4jConstants.TYPED_ID_SEPARATOR)) {
                final String profileClass = substringBefore(sId, Pac4jConstants.TYPED_ID_SEPARATOR);
                for (final String profileClassPrefix : getProfileClassPrefixes()) {
                    if (profileClass.startsWith(profileClassPrefix)) {
                        return sId.substring(profileClass.length() + 1);
                    }
                }
            }
            return sId;
        }
        return null;
    }

    public static List<String> getProfileClassPrefixes() {
        return profileClassPrefixes;
    }

    public static void setProfileClassPrefixes(final List<String> profileClassPrefixes) {
        CommonHelper.assertNotNull("profileClassPrefixes", profileClassPrefixes);
        ProfileHelper.profileClassPrefixes = profileClassPrefixes;
    }
}
