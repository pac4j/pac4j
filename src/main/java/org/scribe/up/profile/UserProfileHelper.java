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

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper with some basic methods to parse profile response or build the user profile.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class UserProfileHelper {
    
    protected static final Logger logger = LoggerFactory.getLogger(UserProfileHelper.class);
    
    protected ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Return the text between the two strings specified. Return null if no string is found.
     * 
     * @param text
     * @param s1
     * @param s2
     * @return the text between the two strings specified in input
     */
    public String substringBetween(String text, String s1, String s2) {
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
     * Add identifier extracted from JSON response to the user profile.
     * 
     * @param userProfile
     * @param json
     * @param attributeName
     */
    public void addIdentifier(UserProfile userProfile, JsonNode json, String attributeName) {
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
    public void addIdentifier(UserProfile userProfile, String id) {
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
    public void addAttribute(UserProfile userProfile, JsonNode json, String attributeName) {
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
    public void addAttribute(UserProfile userProfile, JsonNode json, String attributeName,
                             AttributeConverter<Object> converter) {
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
    public void addAttribute(UserProfile userProfile, String attributeName, Object attribute) {
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
    public void addAttribute(UserProfile userProfile, String attributeName, Object attribute,
                             AttributeConverter<Object> converter) {
        if (converter != null) {
            attribute = converter.convert(attribute);
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
     * Return the first node of a JSON response.
     * 
     * @param text
     * @return the first node of the JSON response or null if exception is thrown
     */
    public JsonNode getFirstJsonNode(String text) {
        try {
            return mapper.readValue(text, JsonNode.class);
        } catch (JsonParseException e) {
            logger.error("JsonParseException", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
        return null;
    }
}
