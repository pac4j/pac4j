/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class converts a JSON node (or string) into an object.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverter<T extends Object> implements AttributeConverter<T> {

    private final Class<T> clazz;

    public JsonConverter(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public T convert(final Object attribute) {
        if (attribute != null) {
            if (attribute.getClass().isAssignableFrom(clazz)) {
                return (T) attribute;
            } else if (attribute instanceof String) {
                final JsonNode node = JsonHelper.getFirstNode((String) attribute);
                return JsonHelper.getAsType(node, clazz);
            } else if (attribute instanceof JsonNode) {
                final T ret = JsonHelper.getAsType((JsonNode) attribute, clazz);
                return ret;
            }
        }
        return null;
    }
}
