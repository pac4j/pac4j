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

import java.util.Date;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Facebook info (id + name + category + created_time).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookInfo extends JsonObject {
    
    private static final long serialVersionUID = -6023752317085418350L;
    
    private String id;
    
    private String category;
    
    private String name;
    
    private Date createdTime;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.category = (String) JsonHelper.convert(Converters.stringConverter, json, "category");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.createdTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "created_time");
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
