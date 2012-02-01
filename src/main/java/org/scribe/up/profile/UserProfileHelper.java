/*
  Copyright 2012 Jérôme Leleu

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
package org.scribe.up.profile;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper with some basic methods to parse profile response or build the user profile.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class UserProfileHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileHelper.class);
    
    /**
     * Return the text between the two strings specified. Return null if no string is found.
     * 
     * @param text
     * @param s1
     * @param s2
     * @return
     */
    public static String extractString(String text, String s1, String s2) {
        if (text != null && s1 != null && s2 != null) {
            int begin = text.indexOf(s1);
            if (begin >= 0) {
                int end = text.indexOf(s2, begin);
                if (end >= 0) {
                    String extract = text.substring(begin + s1.length(), end);
                    logger.debug("String extracted between {} and {} in {} : {}", new Object[] {
                        s1, s2, text, extract
                    });
                    return extract;
                }
            }
        }
        return null;
    }
    
    /**
     * Add identifier extracted from JSON response to user profile. Fail without exception.
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     */
    public static void addIdentifier(UserProfile userProfile, JSONObject json, String attributeName) {
        try {
            String userId = null;
            Object id = json.get(attributeName);
            if (id instanceof Integer) {
                userId = "" + id;
            } else {
                userId = (String) id;
            }
            logger.debug("userId : {}", userId);
            userProfile.setId(userId);
        } catch (JSONException e) {
            logger.warn("JSON exception", e);
        } catch (RuntimeException e) {
            logger.warn("Runtime exception", e);
        }
    }
    
    /**
     * Add attribute extracted from JSON response to user profile. Fail without exception.
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     */
    public static void addAttribute(UserProfile userProfile, JSONObject json, String attributeName) {
        try {
            Object value = json.get(attributeName);
            logger.debug("key : {} / value : {}", attributeName, value);
            userProfile.addAttribute(attributeName, value);
        } catch (JSONException e) {
            logger.warn("JSON exception", e);
        } catch (RuntimeException e) {
            logger.warn("Runtime exception", e);
        }
    }
}
