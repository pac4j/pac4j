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
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a common Facebook object (id + name).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookObject extends JsonObject {
    
    private static final long serialVersionUID = -5018527030174357571L;
    
    private String id;
    
    private String name;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
}
