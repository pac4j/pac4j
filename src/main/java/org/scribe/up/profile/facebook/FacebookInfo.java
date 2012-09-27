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

import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Facebook info (id + name + category + created_time).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookInfo extends JsonObject {
    
    private static final long serialVersionUID = 4185233961478612831L;
    
    private String id;
    
    private String category;
    
    private String name;
    
    private Date createdTime;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.category = Converters.stringConverter.convertFromJson(json, "category");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.createdTime = Converters.dateConverter.convertFromJson(json, "created_time");
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Date getCreatedTime() {
        return this.createdTime;
    }
}
