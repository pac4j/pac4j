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
package org.scribe.up.profile.converter;

import java.util.List;

import org.scribe.up.profile.JsonList;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class converts a JSON (String or JsonNode) into a list of objects.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings({
    "unchecked", "rawtypes"
})
public final class JsonListConverter extends BaseConverter<JsonList> {
    
    private final Class<? extends Object> clazz;
    
    public JsonListConverter(final Class<? extends Object> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public JsonList convert(final Object attribute) {
        if (attribute != null
            && (attribute instanceof String || attribute instanceof JsonNode || attribute instanceof List<?>)) {
            return new JsonList(attribute, clazz);
        }
        return null;
    }
}
