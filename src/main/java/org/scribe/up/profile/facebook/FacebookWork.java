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
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork extends JsonObject {
    
    private static final long serialVersionUID = 8481151870430190829L;
    
    private FacebookObject employer;
    
    private FacebookObject location;
    
    private FacebookObject position;
    
    private String description;
    
    private Date startDate;
    
    private Date endDate;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.employer = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "employer");
        this.location = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "location");
        this.position = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "position");
        this.description = Converters.stringConverter.convertFromJson(json, "description");
        this.startDate = FacebookConverters.workDateConverter.convertFromJson(json, "start_date");
        this.endDate = FacebookConverters.workDateConverter.convertFromJson(json, "end_date");
    }
    
    public FacebookObject getEmployer() {
        return this.employer;
    }
    
    public FacebookObject getLocation() {
        return this.location;
    }
    
    public FacebookObject getPosition() {
        return this.position;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public Date getEndDate() {
        return this.endDate;
    }
}
