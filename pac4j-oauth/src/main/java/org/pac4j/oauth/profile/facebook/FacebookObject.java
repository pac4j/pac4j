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
package org.pac4j.oauth.profile.facebook;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a common Facebook object (id + name).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookObject extends JsonObject {
    
    private static final long serialVersionUID = 7393867411970930893L;
    
    private String id;
    
    private String name;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
}
