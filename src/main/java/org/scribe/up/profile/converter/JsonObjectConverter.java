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

import java.lang.reflect.Constructor;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a JSON (String or JsonNode) into an JSON object.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class JsonObjectConverter extends BaseConverter<JsonObject> {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectConverter.class);
    
    private final Class<? extends JsonObject> clazz;
    
    public JsonObjectConverter(final Class<? extends JsonObject> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public JsonObject convert(final Object attribute) {
        if (attribute != null && (attribute instanceof String || attribute instanceof JsonNode)) {
            try {
                final Constructor<? extends JsonObject> constructor = this.clazz.getDeclaredConstructor();
                final JsonObject jsonObject = constructor.newInstance();
                jsonObject.buildFrom(attribute);
                return jsonObject;
            } catch (final Exception e) {
                logger.error("Cannot build instance", e);
            }
        }
        return null;
    }
}
