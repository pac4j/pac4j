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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pac4j.core.profile.converter.AttributeConverter;

/**
 * This class is the definition of the attributes of a profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class AttributesDefinition {
    
    protected List<String> allAttributesNames = new ArrayList<String>();
    
    protected List<String> principalAttributesNames = new ArrayList<String>();
    
    protected List<String> otherAttributesNames = new ArrayList<String>();
    
    protected Map<String, AttributeConverter<? extends Object>> attributesConverters = new HashMap<String, AttributeConverter<? extends Object>>();
    
    /**
     * Return all the attributes names.
     * 
     * @return all the attributes names
     */
    public List<String> getAllAttributes() {
        return this.allAttributesNames;
    }
    
    /**
     * Return the principal attributes names.
     * 
     * @return the principal attributes names
     */
    public List<String> getPrincipalAttributes() {
        return this.principalAttributesNames;
    }
    
    /**
     * Return the other attributes names.
     * 
     * @return the other attributes names
     */
    public List<String> getOtherAttributes() {
        return this.otherAttributesNames;
    }
    
    /**
     * Add an attribute as a primary one and its converter to this attributes definition.
     * 
     * @param name
     * @param converter
     */
    protected void addAttribute(final String name, final AttributeConverter<? extends Object> converter) {
        addAttribute(name, converter, true);
    }
    
    /**
     * Add an attribute, its primary aspect and its converter to this attributes definition.
     * 
     * @param name
     * @param converter
     * @param principal
     */
    protected void addAttribute(final String name, final AttributeConverter<? extends Object> converter,
                                final boolean principal) {
        this.allAttributesNames.add(name);
        this.attributesConverters.put(name, converter);
        if (principal) {
            this.principalAttributesNames.add(name);
        } else {
            this.otherAttributesNames.add(name);
        }
    }
    
    /**
     * Convert an attribute into the right type. If no converter exists for this attribute name, the attribute is ignored and null is
     * returned.
     * 
     * @param name
     * @param value
     * @return the converted attribute or null if no converter exists for this attribute name
     */
    public Object convert(final String name, final Object value) {
        AttributeConverter<? extends Object> converter = this.attributesConverters.get(name);
        if (converter != null && value != null) {
            return converter.convert(value);
        } else {
            return null;
        }
    }
}
