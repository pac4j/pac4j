package org.pac4j.core.profile;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id user identifier
     * @param clazz profile class
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(final String id, final Class<? extends CommonProfile> clazz) {
        if (id != null && clazz != null) {
            boolean typedId = id.startsWith(clazz.getName() + CommonProfile.SEPARATOR);
            boolean oldTypedId = id.startsWith(clazz.getSimpleName() + CommonProfile.SEPARATOR);
            return typedId || oldTypedId;
        }
        return false;
    }

    /**
     * Build a profile from a typed id and a map of attributes.
     * 
     * @param typedId typed identifier
     * @param attributes user attributes
     * @return the user profile built
     */
    public static CommonProfile buildProfile(final String typedId, final Map<String, Object> attributes) {
        if (CommonHelper.isBlank(typedId)) {
            return null;
        }

        logger.info("Building user profile based on typedId {}", typedId);
        try {
            final String[] values = typedId.split(CommonProfile.SEPARATOR);
            if (values != null && values.length >= 1) {
                final String className = values[0];
                if (CommonHelper.isNotBlank(className)) {
                    final String completeName;
                    if (className.indexOf(".") >=0 ) {
                        completeName = className;
                    } else {
                        logger.warn("Typed identifier starting with only a simple class name (without package name) are deprecated and will be removed in future versions. See profile#getOldTypedId() versus profile#getTypedId()");
                        completeName = determineProfileClassByName(className);
                    }
                    return buildUserProfileByClassCompleteName(typedId, attributes, completeName);
                }
            }
        } catch (final Exception e) {
            logger.error("Cannot build instance", e);
        }
        return null;
    }

    @Deprecated
    private static String determineProfileClassByName(final String className) {
        final String completeName;
        if ("CasProfile".equals(className) || "CasProxyProfile".equals(className)) {
            completeName = "org.pac4j.cas.profile." + className;
        } else if ("HttpTGTProfile".equals(className)) {
            completeName = "org.pac4j.cas.profile.HttpTGTProfile";
        } else if ("SAML2Profile".equals(className)) {
            completeName = "org.pac4j.saml.profile.SAML2Profile";
        } else if ("HttpProfile".equals(className)) {
            completeName = "org.pac4j.http.profile.HttpProfile";
        } else if ("OidcProfile".equals(className)) {
            completeName = "org.pac4j.oidc.profile.OidcProfile";
        } else if ("LdapProfile".equals(className)) {
            completeName = "org.pac4j.ldap.profile.LdapProfile";
        } else if ("DbProfile".equals(className)) {
            completeName = "org.pac4j.sql.profile.DbProfile";
        } else if ("MongoProfile".equals(className)) {
            completeName = "org.pac4j.mongo.profile.MongoProfile";
        } else if ("YahooOpenIdProfile".equals(className)) {
            completeName = "org.pac4j.openid.profile.yahoo.YahooOpenIdProfile";
        } else if ("GaeUserServiceProfile".equals(className)) {
            completeName = "org.pac4j.gae.profile.GaeUserServiceProfile";
        } else if ("StormpathProfile".equals(className)) {
            completeName = "org.pac4j.stormpath.profile.StormpathProfile";
        } else if ("JwtProfile".equals(className)) {
            completeName = "org.pac4j.jwt.profile.JwtProfile";
        } else {
            final String packageName = className.substring(0, className.length() - 7).toLowerCase();
            completeName = "org.pac4j.oauth.profile." + packageName + '.' + className;
        }
        return completeName;
    }

    public static CommonProfile buildUserProfileByClassCompleteName(final String typedId, final Map<String, Object> attributes,
                                                                   final String completeName) throws Exception {
        final Constructor<? extends CommonProfile> constructor = getConstructor(completeName);
        final CommonProfile userProfile = constructor.newInstance();
        userProfile.build(typedId, attributes);
        logger.debug("userProfile built: {}", userProfile);
        return userProfile;
    }

    @SuppressWarnings("unchecked")
    private static Constructor<? extends CommonProfile> getConstructor(final String name) throws Exception{
        Constructor<? extends CommonProfile> constructor = constructorsCache.get(name);
        if (constructor == null) {
            synchronized (constructorsCache) {
                constructor = constructorsCache.get(name);
                if (constructor == null) {
                    constructor = (Constructor<? extends CommonProfile>) Class.forName(name).getDeclaredConstructor();
                    constructorsCache.put(name, constructor);
                }
            }
        }
        return constructor;
    }
}
