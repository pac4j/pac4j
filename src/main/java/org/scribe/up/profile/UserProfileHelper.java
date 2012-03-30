/*
  Copyright 2012 Jerome Leleu

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.converter.AttributeConverter;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.profile.yahoo.YahooAddress;
import org.scribe.up.profile.yahoo.YahooDisclosure;
import org.scribe.up.profile.yahoo.YahooEmail;
import org.scribe.up.profile.yahoo.YahooInterest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper with some basic methods to parse profile response or build the user profile.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class UserProfileHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileHelper.class);
    
    private UserProfileHelper() {
    }
    
    /**
     * Add identifier extracted from JSON response to the user profile.
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     */
    public static void addIdentifier(UserProfile userProfile, JsonNode json, String attributeName) {
        String userId = null;
        if (json != null) {
            JsonNode id = json.get(attributeName);
            if (id != null) {
                if (id.isNumber()) {
                    userId = id.getNumberValue().toString();
                } else if (id.isTextual()) {
                    userId = id.getTextValue();
                }
                addIdentifier(userProfile, userId);
            }
        }
    }
    
    /**
     * Add identifier to the user profile.
     * 
     * @param userProfile
     * @param id
     */
    public static void addIdentifier(UserProfile userProfile, String id) {
        logger.debug("id : {}", id);
        userProfile.setId(id);
    }
    
    /**
     * Add attribute extracted from JSON response to the user profile (without attribute conversion).
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     */
    public static void addAttribute(UserProfile userProfile, JsonNode json, String attributeName) {
        addAttribute(userProfile, json, attributeName, null);
    }
    
    /**
     * Add attribute extracted from JSON response to the user profile.
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     * @param converter
     */
    public static void addAttribute(UserProfile userProfile, JsonNode json, String attributeName,
                                    AttributeConverter<? extends Object> converter) {
        if (json != null) {
            JsonNode value = json.get(attributeName);
            if (value != null) {
                Object attribute = null;
                if (value.isNumber()) {
                    attribute = value.getNumberValue();
                } else if (value.isBoolean()) {
                    attribute = value.getBooleanValue();
                } else if (value.isTextual()) {
                    attribute = value.getTextValue();
                }
                addAttribute(userProfile, attributeName, attribute, converter);
            }
        }
    }
    
    /**
     * Add attribute to the user profile (without attribute conversion).
     * 
     * @param userProfile
     * @param attributeName
     * @param attribute
     */
    public static void addAttribute(UserProfile userProfile, String attributeName, Object attribute) {
        addAttribute(userProfile, attributeName, attribute, null);
    }
    
    /**
     * Add attribute to the user profile.
     * 
     * @param userProfile
     * @param attributeName
     * @param attribute
     * @param converter
     */
    public static void addAttribute(UserProfile userProfile, String attributeName, Object attribute,
                                    AttributeConverter<? extends Object> converter) {
        if (converter != null && attribute != null) {
            attribute = converter.convert(attribute.toString());
        }
        if (attribute == null) {
            logger.debug("key : {} / attribute : {}", attributeName, attribute);
        } else {
            logger.debug("key : {} / attribute : {} / {}", new Object[] {
                attributeName, attribute, attribute.getClass()
            });
        }
        userProfile.addAttribute(attributeName, attribute);
    }
    
    /**
     * Return a list of specific profile object, which could be of type : {@link org.scribe.up.profile.facebook.FacebookObject},
     * {@link org.scribe.up.profile.facebook.FacebookEducation}, String, {@link org.scribe.up.profile.facebook.FacebookWork},
     * {@link org.scribe.up.profile.google.GoogleObject}, {@link org.scribe.up.profile.yahoo.YahooAddress},
     * {@link org.scribe.up.profile.yahoo.YahooDisclosure}, {@link org.scribe.up.profile.yahoo.YahooEmail} or
     * {@link org.scribe.up.profile.yahoo.YahooInterest}.
     * 
     * @param json
     * @param clazz
     * @return a list of specific profile object
     */
    public static List<? extends Object> getListObject(JsonNode json, Class<? extends Object> clazz) {
        List<Object> list = new ArrayList<Object>();
        if (json != null) {
            Iterator<JsonNode> jsonIterator = json.getElements();
            while (jsonIterator.hasNext()) {
                JsonNode node = jsonIterator.next();
                // google
                if (clazz == GoogleObject.class) {
                    list.add(new GoogleObject(node));
                    // facebook
                    /*                } else if (clazz == FacebookObject.class) {
                                        list.add(new FacebookObject(node));
                                    } else if (clazz == FacebookEducation.class) {
                                        list.add(new FacebookEducation(node.toString()));
                                    } else if (clazz == FacebookWork.class) {
                                        list.add(new FacebookWork(node));*/
                    // yahoo
                } else if (clazz == YahooAddress.class) {
                    list.add(new YahooAddress(node));
                } else if (clazz == YahooDisclosure.class) {
                    list.add(new YahooDisclosure(node));
                } else if (clazz == YahooEmail.class) {
                    list.add(new YahooEmail(node));
                } else if (clazz == YahooInterest.class) {
                    list.add(new YahooInterest(node));
                    //
                } else if (clazz == String.class) {
                    list.add(node.getTextValue());
                }
            }
        }
        return list;
    }
}
