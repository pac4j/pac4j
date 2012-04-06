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
    
    private static final long serialVersionUID = 5403360229247469734L;
    
    protected static final Logger logger = LoggerFactory.getLogger(UserProfile.class);
    
    protected String id;
    
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    
    public UserProfile() {
    }
    
    public UserProfile(Object id) {
        setId(id);
    }
    
    public UserProfile(Object id, Map<String, Object> attributes) {
        setId(id);
        addAttributes(attributes);
    }
    
    protected AttributesDefinition getAttributesDefinition() {
        return null;
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
    
    protected String getProviderType() {
        return null;
    }
    
    public void setId(Object id) {
        if (id != null) {
            String sId = id.toString();
            String providerType = getProviderType();
            if (providerType != null && sId.startsWith(providerType + "#")) {
                sId = sId.substring(providerType.length() + 1);
            }
            logger.debug("identifier : {}", sId);
            this.id = sId;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
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
