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
 * (objects).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 590794176557010470L;
    
    protected transient static AttributesDefinition definition;
    
    protected transient static String providerType;
    
    protected static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
    
    protected String id;
    
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    
    public UserProfile() {
    }
    
    public UserProfile(String id) {
        setId(id);
    }
    
    public UserProfile(String id, Map<String, Object> attributes) {
        setId(id);
        addAttributes(attributes);
    }
    
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
    
    public void addAttributes(Map<String, Object> attributes) {
        for (String key : attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
    }
    
    public void setId(String id) {
        if (id != null) {
            if (id.startsWith(providerType + "#")) {
                id = id.substring(providerType.length() + 1);
            }
            logger.debug("identifier : {}", id);
            this.id = id;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
    
    public static AttributesDefinition getAttributesDefinition() {
        return definition;
    }
    
    protected boolean getSafeBoolean(Boolean b) {
        return b != null ? b : false;
    }
    
    protected int getSafeInteger(Integer i) {
        return i != null ? i : 0;
    }
    
    @Override
    public String toString() {
        return "UserProfile(id:" + id + ",attributes:" + attributes + ")";
    }
}
