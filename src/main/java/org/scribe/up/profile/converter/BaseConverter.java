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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;

/**
 * This class defines a base converter which can convert from JSON.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class BaseConverter<T> implements AttributeConverter<T> {
    
    public abstract T convert(Object attribute);
    
    public T convertFromJson(JsonNode json, String name) {
        return convert(JsonHelper.get(json, name));
    }
}
