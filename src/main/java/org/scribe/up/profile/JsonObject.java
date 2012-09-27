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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is an object which can build from JSON.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class JsonObject extends SafeGetterObject implements Serializable {
    
    private static final long serialVersionUID = -8029160999433691578L;
    
    protected String json = "";
    
    /**
     * Whether the input JSON should be stored in object to be restored for CAS serialization when toString() is called. Save memory also.
     */
    private static boolean keepRawData = true;
    
    /**
     * Build an object from JSON (String or JsonNode).
     * 
     * @param json
     */
    public final void buildFrom(final Object json) {
        if (json != null) {
            if (json instanceof String) {
                final String s = (String) json;
                buildFromJson(JsonHelper.getFirstNode(s));
            } else if (json instanceof JsonNode) {
                final JsonNode jsonNode = (JsonNode) json;
                if (keepRawData) {
                    this.json = jsonNode.toString();
                }
                buildFromJson(jsonNode);
            } else {
                throw new IllegalArgumentException(json.getClass() + " not supported");
            }
        }
    }
    
    /**
     * Build an object from a JsonNode.
     * 
     * @param json
     */
    protected abstract void buildFromJson(JsonNode json);
    
    public static boolean isKeepRawData() {
        return keepRawData;
    }
    
    public static void setKeepRawData(final boolean keepRawData) {
        JsonObject.keepRawData = keepRawData;
    }
    
    @Override
    public String toString() {
        return this.json;
    }
}
