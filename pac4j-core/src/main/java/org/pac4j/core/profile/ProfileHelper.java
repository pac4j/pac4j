/*
  Copyright 2012 - 2014 Jerome Leleu

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

    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id
     * @param clazz
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
     * @param typedId
     * @param attributes
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
                        } else if ("Saml2Profile".equals(className)) {
                            completeName = "org.pac4j.saml.profile.Saml2Profile";
                        } else if ("HttpProfile".equals(className)) {
                            completeName = "org.pac4j.http.profile.HttpProfile";
                        } else if ("MyOpenIdProfile".equals(className)) {
                            completeName = "org.pac4j.openid.profile.myopenid.MyOpenIdProfile";
                        } else if ("GoogleOpenIdProfile".equals(className)) {
                            completeName = "org.pac4j.openid.profile.google.GoogleOpenIdProfile";
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
     * Set whether the input data should be stored in object to be restored for CAS serialization when toString() is called. Save memory
     * also.
     * 
     * @param keepRawData
     */
    public static void setKeepRawData(final boolean keepRawData) {
        RawDataObject.setKeepRawData(keepRawData);
    }
}
