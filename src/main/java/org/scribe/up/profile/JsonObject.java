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

import org.codehaus.jackson.JsonNode;

/**
 * This class is an object which can build from JSON.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class JsonObject {
    
    protected String json = "";
    
    public JsonObject(Object json) {
        buildFrom(json);
    }
    
    protected final void buildFrom(Object json) {
        if (json != null) {
            if (json instanceof String) {
                String s = (String) json;
                this.json = s;
                buildFromJson(JsonHelper.getFirstNode(s));
            } else if (json instanceof JsonNode) {
                JsonNode jsonNode = (JsonNode) json;
                this.json = jsonNode.toString();
                buildFromJson(jsonNode);
            } else {
                throw new IllegalArgumentException(json + " not supported");
            }
        }
    }
    
    protected abstract void buildFromJson(JsonNode json);
    
    @Override
    public String toString() {
        return json;
    }
}
