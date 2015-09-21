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

    private static boolean enforceProfileDefinition = false;

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
        if (typedId != null) {
            final String[] values = typedId.split("#");
            if (values != null && values.length == 2) {
                final String className = values[0];
                if (className != null) {
                    try {
                        String completeName;
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
                        } else {
                            final String packageName = className.substring(0, className.length() - 7).toLowerCase();
                            completeName = "org.pac4j.oauth.profile." + packageName + "." + className;
                        }
                        @SuppressWarnings("unchecked")
                        final Constructor<? extends UserProfile> constructor = (Constructor<? extends UserProfile>) Class
                                .forName(completeName).getDeclaredConstructor();
                        final UserProfile userProfile = constructor.newInstance();
                        userProfile.build(typedId, attributes);
                        logger.debug("userProfile built : {}", userProfile);
                        return userProfile;
                    } catch (final Exception e) {
                        logger.error("Cannot build instance", e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Set whether the input data should be stored in object to be restored for CAS serialization when toString() is called.
     * Save memory if <code>false</code>.
     * 
     * @param keepRawData should we keep the raw data (for CAS)
     */
    public static void setKeepRawData(final boolean keepRawData) {
        RawDataObject.setKeepRawData(keepRawData);
    }

    public static boolean isEnforceProfileDefinition() {
        return enforceProfileDefinition;
    }

    /**
     * Set whether the profile definition (= attributes definition) should be enforced (= undefined attributes are ignored).
     * <code>false</code> since version 1.8. It was <code>true</code> before.
     *
     * @param enforceProfileDefinition whether the profile definition should be enforced
     * @since 1.8.0
     */
    public static void setEnforceProfileDefinition(boolean enforceProfileDefinition) {
        ProfileHelper.enforceProfileDefinition = enforceProfileDefinition;
    }
}
