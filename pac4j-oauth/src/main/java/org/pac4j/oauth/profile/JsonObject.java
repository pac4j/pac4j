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
package org.pac4j.oauth.profile;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.RawDataObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is an object which can be built from JSON.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class JsonObject extends RawDataObject {
    
    private static final long serialVersionUID = 4261523645471006190L;
    
    /**
     * Build an object from JSON (String or JsonNode).
     * 
     * @param json json
     */
    public final void buildFrom(final Object json) {
        if (json != null) {
            if (json instanceof String) {
                final String s = (String) json;
                buildFromJson(JsonHelper.getFirstNode(s));
            } else if (json instanceof JsonNode) {
                final JsonNode jsonNode = (JsonNode) json;
                if (keepRawData && isRootObject()) {
                    this.data = jsonNode.toString();
                }
                buildFromJson(jsonNode);
            } else {
                throw new TechnicalException(json.getClass() + " not supported");
            }
        }
    }
    
    /**
     * Build an object from a JsonNode.
     * 
     * @param json json
     */
    protected abstract void buildFromJson(JsonNode json);
}
