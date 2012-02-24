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
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.JsonHelper;

/**
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookWork {
    
    protected static DateConverter dateConverter = new DateConverter("yyyy-MM");
    
    protected FacebookObject employer;
    
    protected FacebookObject location;
    
    protected FacebookObject position;
    
    protected String description;
    
    protected Date startDate;
    
    protected Date endDate;
    
    public FacebookWork(JsonNode json) {
        if (json != null) {
            this.employer = new FacebookObject(json.get("employer"));
            this.location = new FacebookObject(json.get("location"));
            this.position = new FacebookObject(json.get("position"));
            this.description = JsonHelper.getTextValue(json, "description");
            this.startDate = dateConverter.convert(JsonHelper.getTextValue(json, "start_date"));
            this.endDate = dateConverter.convert(JsonHelper.getTextValue(json, "end_date"));
        }
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
    
    public String toString() {
        return "FacebookWork[employer:" + employer + ",location:" + location + ",position:" + position
               + ",description:" + description + ",start_date:" + startDate + ",end_date:" + endDate + "]";
    }
}
