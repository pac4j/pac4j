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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the user profile retrieved from an OAuth provider after authentication : it's an identifier (string) and attributes
 * (objects). The attributes definition and the associated provider type are null (generic profile), they must be defined in subclasses.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 3240837328687495139L;
    
    protected transient static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
    
    protected String id;
    
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    
    public transient static final String SEPARATOR = "#";
    
    /**
     * Build an empty profile.
     */
    public UserProfile() {
    }
    
    /**
     * Build a profile from a user identifier.
     * 
     * @param id
     */
    public UserProfile(Object id) {
        setId(id);
    }
    
    /**
     * Build a profile from user identifier and attributes.
     * 
     * @param id
     * @param attributes
     */
    public UserProfile(Object id, Map<String, Object> attributes) {
        setId(id);
        addAttributes(attributes);
    }
    
    /**
     * Return the attributes definition for this user profile. Null for this (generic) user profile.
     * 
     * @return the attributes definition
     */
    protected AttributesDefinition getAttributesDefinition() {
        return null;
    }
    
    /**
     * Add an attribute and perform conversion if necessary.
     * 
     * @param key
     * @param value
     */
    public void addAttribute(String key, Object value) {
        if (value != null) {
            AttributesDefinition definition = getAttributesDefinition();
            // no attributes definition -> no conversion
            if (definition == null) {
                logger.debug("no conversion => key : {} / value : {} / {}", new Object[] {
                    key, value, value.getClass()
                });
                attributes.put(key, value);
            } else {
                value = definition.convert(key, value);
                if (value != null) {
                    logger.debug("converted to => key : {} / value : {} / {}", new Object[] {
                        key, value, value.getClass()
                    });
                    attributes.put(key, value);
                }
            }
        }
    }
    
    /**
     * Add attributes.
     * 
     * @param attributes
     */
    public void addAttributes(Map<String, Object> attributes) {
        for (String key : attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
    }
    
    /**
     * Return the associated provider type for this user profile. Null for this (generic) user profile.
     * 
     * @return the associated provider type
     */
    protected String getProviderType() {
        return null;
    }
    
    /**
     * Set the identifier and convert it if necessary.
     * 
     * @param id
     */
    public void setId(Object id) {
        if (id != null) {
            String sId = id.toString();
            String providerType = getProviderType();
            if (providerType != null && sId.startsWith(providerType + SEPARATOR)) {
                sId = sId.substring(providerType.length() + SEPARATOR.length());
            }
            logger.debug("identifier : {}", sId);
            this.id = sId;
        }
    }
    
    /**
     * Get the user identifier.
     * 
     * @return the user identifier
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the user identifier with a prefix which is the provider type.
     * 
     * @return the typed user identifier
     */
    public String getTypedId() {
        String providerType = getProviderType();
        if (providerType != null) {
            return providerType + SEPARATOR + id;
        } else {
            return id;
        }
    }
    
    /**
     * Get attributes as immutable map.
     * 
     * @return the immutable attributes
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    
    /**
     * Return the primitive value or false if null.
     * 
     * @param b
     * @return the primitive value or false if null
     */
    protected boolean getSafeBoolean(Boolean b) {
        return b != null ? b : false;
    }
    
    /**
     * Return the primitive value or 0 if null.
     * 
     * @param i
     * @return the primitive value or 0 if null
     */
    protected int getSafeInteger(Integer i) {
        return i != null ? i : 0;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{id:" + id + ",attributes:" + attributes + "}";
    }
}
