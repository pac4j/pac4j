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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.converter.AttributeConverter;
import org.scribe.up.profile.converter.BooleanConverter;
import org.scribe.up.profile.converter.ColorConverter;
import org.scribe.up.profile.converter.IntegerConverter;
import org.scribe.up.profile.converter.LocaleConverter;
import org.scribe.up.profile.converter.StringConverter;

/**
 * This class is the definition of the attributes.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class AttributesDefinition {
    
    protected List<String> attributes = new ArrayList<String>();
    
    protected Map<String, AttributeConverter<? extends Object>> converters = new HashMap<String, AttributeConverter<? extends Object>>();
    
    // default converters
    protected static LocaleConverter localeConverter = new LocaleConverter();
    protected static StringConverter stringConverter = new StringConverter();
    protected static BooleanConverter booleanConverter = new BooleanConverter();
    protected static IntegerConverter integerConverter = new IntegerConverter();
    protected static ColorConverter colorConverter = new ColorConverter();
    
    public List<String> getAttributes() {
        return attributes;
    }
    
    public Object convert(String name, Object value) {
        AttributeConverter<? extends Object> converter = converters.get(name);
        if (converter != null) {
            return converter.convert(value);
        } else {
            return value;
        }
    }
    
    public Object convert(JsonNode json, String name) {
        return convert(name, JsonHelper.get(json, name));
    }
}
