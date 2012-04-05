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

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.converter.DateConverter;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -4398476232898674635L;
    
    private transient final static JsonObjectConverter facebookObjectConverter = new JsonObjectConverter(
                                                                                                         FacebookObject.class);
    private transient final static DateConverter dateConverter = new DateConverter("yyyy-MM");
    
    private FacebookObject employer;
    
    private FacebookObject location;
    
    private FacebookObject position;
    
    private String description;
    
    private Date startDate;
    
    private Date endDate;
    
    public FacebookWork(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.employer = (FacebookObject) facebookObjectConverter.convertFromJson(json, "employer");
        this.location = (FacebookObject) facebookObjectConverter.convertFromJson(json, "location");
        this.position = (FacebookObject) facebookObjectConverter.convertFromJson(json, "position");
        this.description = Converters.stringConverter.convertFromJson(json, "description");
        this.startDate = dateConverter.convertFromJson(json, "start_date");
        this.endDate = dateConverter.convertFromJson(json, "end_date");
    }
    
    public FacebookObject getEmployer() {
        return employer;
    }
    
    public FacebookObject getLocation() {
        return location;
    }
    
    public FacebookObject getPosition() {
        return position;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
}
