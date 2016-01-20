/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.profile;

import java.lang.reflect.Constructor;
import java.util.Map;

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

    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id user identifier
     * @param clazz profile class
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(final String id, final Class<? extends UserProfile> clazz) {
        if (id != null && clazz != null && id.startsWith(clazz.getSimpleName() + UserProfile.SEPARATOR)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Build a profile from a typed id and a map of attributes.
     * 
     * @param typedId typed identifier
     * @param attributes user attributes
     * @return the user profile built
     */
    public static UserProfile buildProfile(final String typedId, final Map<String, Object> attributes) {
        if (CommonHelper.isBlank(typedId)) {
            return null;
        }

        logger.info("Building user profile based on typedId {}", typedId);
        try {
            final String[] values = typedId.split(UserProfile.SEPARATOR);
            if (values.length >= 1) {
                final String className = values[0];
                final String completeName = determineProfileClassByName(className);
                return buildUserProfileByClassCompleteName(typedId, attributes, completeName);
            }
        } catch (final Exception e) {
            logger.error("Cannot build instance", e);
        }
        return null;
    }

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

    private static UserProfile buildUserProfileByClassCompleteName(final String typedId, final Map<String, Object> attributes,
                                                                   final String completeName)
            throws Exception {
        @SuppressWarnings("unchecked")
        final Constructor<? extends UserProfile> constructor = (Constructor<? extends UserProfile>) Class
                .forName(completeName).getDeclaredConstructor();
        final UserProfile userProfile = constructor.newInstance();
        userProfile.build(typedId, attributes);
        logger.debug("userProfile built : {}", userProfile);
        return userProfile;
    }
}
