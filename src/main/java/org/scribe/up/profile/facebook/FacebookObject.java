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
package org.scribe.up.profile.facebook;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;

/**
 * This class represents a common Facebook object (id + name).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookObject {
    
    private String id;
    
    private String name;
    
    public FacebookObject(JsonNode json) {
        this.id = JsonHelper.getTextValue(json, "id");
        this.name = JsonHelper.getTextValue(json, "name");
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return "FacebookObject[id:" + id + ",name:" + name + "]";
    }
}
