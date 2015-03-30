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
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork extends JsonObject {
    
    private static final long serialVersionUID = -5698634125512204910L;
    
    private FacebookObject employer;
    
    private FacebookObject location;
    
    private FacebookObject position;
    
    private String description;
    
    private Date startDate;
    
    private Date endDate;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.employer = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "employer");
        this.location = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "location");
        this.position = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "position");
        this.description = (String) JsonHelper.convert(Converters.stringConverter, json, "description");
        this.startDate = (Date) JsonHelper.convert(FacebookConverters.workDateConverter, json, "start_date");
        this.endDate = (Date) JsonHelper.convert(FacebookConverters.workDateConverter, json, "end_date");
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
