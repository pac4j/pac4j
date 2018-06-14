package org.pac4j.core.profile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper for profiles.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ProfileHelper {

    private static final Logger logger = LoggerFactory.getLogger(ProfileHelper.class);

    private static final Map<String, Constructor<? extends CommonProfile>> constructorsCache = new ConcurrentHashMap<>();

    private ProfileHelper() {}

    private static InternalAttributeHandler internalAttributeHandler = new InternalAttributeHandler();

    /**
     * Indicate if the user identifier matches this kind of profile.
     *
     * @param id user identifier
     * @param clazz profile class
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(final String id, final Class<? extends CommonProfile> clazz) {
        if (id != null && clazz != null) {
            return id.startsWith(clazz.getName() + CommonProfile.SEPARATOR);
        }
        return false;
    }

    /**
     * Restore or build a profile.
     *
     * @param profileDefinition the profile definition
     * @param typedId the typed identifier
     * @param profileAttributes The profile attributes. May be {@code null}.
     * @param authenticationAttributes The authentication attributes. May be {@code null}.
     * @param parameters additional parameters for the profile definition
     * @return the restored or built profile
     */
    public static CommonProfile restoreOrBuildProfile(final ProfileDefinition<? extends CommonProfile> profileDefinition,
            final String typedId, final Map<String, Object> profileAttributes, final Map<String, Object> authenticationAttributes,
            final Object... parameters) {
        if (CommonHelper.isBlank(typedId)) {
            return null;
        }

        logger.info("Building user profile based on typedId: {}", typedId);
        final CommonProfile profile;
        if (typedId.contains(CommonProfile.SEPARATOR)) {
            final String className = CommonHelper.substringBefore(typedId, CommonProfile.SEPARATOR);
            try {
                profile = buildUserProfileByClassCompleteName(className);
            } catch (final TechnicalException e) {
                logger.error("Cannot build instance for class name: {}", className, e);
                return null;
            }
            profile.addAttributes(profileAttributes);
            profile.addAuthenticationAttributes(authenticationAttributes);
        } else {
            profile = profileDefinition.newProfile(parameters);
            profileDefinition.convertAndAdd(profile, profileAttributes, authenticationAttributes);
        }
        profile.setId(ProfileHelper.sanitizeIdentifier(profile, typedId));
        return profile;
    }

    /**
     * Build a profile by its class name.
     *
     * @param completeName the class name
     * @return the built user profile
     */
    public static CommonProfile buildUserProfileByClassCompleteName(final String completeName) {
        try {
            final Constructor<? extends CommonProfile> constructor = getConstructor(completeName);
            return constructor.newInstance();
        } catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
                 | InstantiationException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the constructor of the class.
     *
     * @param name the name of the class
     * @return the constructor
     * @throws ClassNotFoundException class not found
     * @throws NoSuchMethodException method not found
     */
    @SuppressWarnings("unchecked")
    private static Constructor<? extends CommonProfile> getConstructor(final String name)
        throws ClassNotFoundException, NoSuchMethodException {
        Constructor<? extends CommonProfile> constructor = constructorsCache.get(name);
        if (constructor == null) {
            synchronized (constructorsCache) {
                constructor = constructorsCache.get(name);
                if (constructor == null) {
                    ClassLoader tccl = Thread.currentThread().getContextClassLoader();

                    if (tccl == null) {
                        constructor = (Constructor<? extends CommonProfile>) Class.forName(name).getDeclaredConstructor();
                    } else {
                        constructor = (Constructor<? extends CommonProfile>) Class.forName(name, true, tccl).getDeclaredConstructor();
                    }
                    constructorsCache.put(name, constructor);
                }
            }
        }
        return constructor;
    }

    /**
     * Flat the list of profiles into a single optional profile (skip any anonymous profile unless it's the only one).
     *
     * @param profiles the list of profiles
     * @param <U> the kind of profile
     * @return the (optional) profile
     */
    public static <U extends CommonProfile> Optional<U> flatIntoOneProfile(final Collection<U> profiles) {
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
    public static <U extends CommonProfile> List<U> flatIntoAProfileList(final Map<String, U> profiles) {
        return new ArrayList<>(profiles.values());
    }

    /**
     * Sanitize into a string identifier.
     *
     * @param profile the user profile
     * @param id the identifier object
     * @return the sanitized identifier
     */
    public static String sanitizeIdentifier(final UserProfile profile, final Object id) {
        if (id != null) {
            String sId = id.toString();
            if (profile != null) {
                final String type = profile.getClass().getName() + UserProfile.SEPARATOR;
                if (sId.startsWith(type)) {
                    sId = sId.substring(type.length());
                }
            }
            return sId;
        }
        return null;
    }

    public static InternalAttributeHandler getInternalAttributeHandler() {
        return internalAttributeHandler;
    }

    public static void setInternalAttributeHandler(final InternalAttributeHandler internalAttributeHandler) {
        ProfileHelper.internalAttributeHandler = internalAttributeHandler;
    }
}
