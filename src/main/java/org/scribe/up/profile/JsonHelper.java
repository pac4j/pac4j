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

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper to work with JSON.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class JsonHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);
    
    private static ObjectMapper mapper = new ObjectMapper();
    
    private JsonHelper() {
    }
    
    /**
     * Return the first node of a JSON response.
     * 
     * @param text
     * @return the first node of the JSON response or null if exception is thrown
     */
    public static JsonNode getFirstNode(String text) {
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
    
    /**
     * Get the text value of the JSON node with name.
     * 
     * @param json
     * @param name
     * @return the text value of the node.
     */
    public static String getTextValue(JsonNode json, String name) {
        if (json != null) {
            JsonNode node = json.get(name);
            if (node != null) {
                return node.getTextValue();
            }
        }
        return null;
    }
    
    /**
     * Get the number value of the JSON node with name.
     * 
     * @param json
     * @param name
     * @return the number value of the node.
     */
    public static Number getNumberValue(JsonNode json, String name) {
        if (json != null) {
            JsonNode node = json.get(name);
            if (node != null) {
                return node.getNumberValue();
            }
        }
        return null;
    }
    
    /**
     * Get the boolean value of the JSON node with name.
     * 
     * @param json
     * @param name
     * @return the boolean value of the node.
     */
    public static Boolean getBooleanValue(JsonNode json, String name) {
        if (json != null) {
            JsonNode node = json.get(name);
            if (node != null) {
                return node.getBooleanValue();
            }
        }
        return null;
    }
}
