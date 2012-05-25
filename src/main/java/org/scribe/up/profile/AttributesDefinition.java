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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scribe.up.profile.converter.AttributeConverter;
import org.scribe.up.profile.converter.Converters;

/**
 * This class is the definition of the attributes of a profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class AttributesDefinition {
    
    protected List<String> attributesNames = new ArrayList<String>();
    
    protected Map<String, AttributeConverter<? extends Object>> attributesConverters = new HashMap<String, AttributeConverter<? extends Object>>();
    
    protected Map<String, Boolean> attributesTypes = new HashMap<String, Boolean>();
    
    public transient static final String ACCESS_TOKEN = "access_token";
    
    public List<String> getAttributes() {
        return attributesNames;
    }
    
    /**
     * Default constructor which adds the access token by default.
     */
    public AttributesDefinition() {
        addAttribute(ACCESS_TOKEN, Converters.stringConverter, false);
    }
    
    /**
     * Add an attribute as a primary one and its converter to this attributes definition.
     * 
     * @param name
     * @param converter
     */
    protected void addAttribute(String name, AttributeConverter<? extends Object> converter) {
        addAttribute(name, converter, true);
    }
    
    /**
     * Add an attribute, its primary aspect and its converter to this attributes definition.
     * 
     * @param name
     * @param converter
     * @param primary
     */
    protected void addAttribute(String name, AttributeConverter<? extends Object> converter, boolean primary) {
        attributesNames.add(name);
        attributesConverters.put(name, converter);
        attributesTypes.put(name, primary);
    }
    
    /**
     * Return if the attribute is primary.
     * 
     * @param name
     * @return if the attribute is primary
     */
    public boolean isPrimary(String name) {
        Boolean b = attributesTypes.get(name);
        if (b == null) {
            return false;
        }
        return (boolean) b;
    }
    
    /**
     * Convert an attribute into the right type. If no converter exists for this attribute name, the attribute is ignored and null is
     * returned.
     * 
     * @param name
     * @param value
     * @return the converted attribute or null if no converter exists for this attribute name
     */
    public Object convert(String name, Object value) {
        AttributeConverter<? extends Object> converter = attributesConverters.get(name);
        if (converter != null && value != null) {
            return converter.convert(value);
        } else {
            return null;
        }
    }
}
