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
import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.JsonObject;

/**
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 2705703533936729650L;
    
    private transient static final AttributesDefinition definition = new FacebookWorkDefinition();
    
    private FacebookObject employer;
    
    private FacebookObject location;
    
    private FacebookObject position;
    
    private String description;
    
    private Date startDate;
    
    private Date endDate;
    
    public FacebookWork() {
    }
    
    public FacebookWork(JsonNode json) {
        buildFrom(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.employer = (FacebookObject) definition.convert(json, FacebookWorkDefinition.EMPLOYER);
        this.location = (FacebookObject) definition.convert(json, FacebookWorkDefinition.LOCATION);
        this.position = (FacebookObject) definition.convert(json, FacebookWorkDefinition.POSITION);
        this.description = (String) definition.convert(json, FacebookWorkDefinition.DESCRIPTION);
        this.startDate = (Date) definition.convert(json, FacebookWorkDefinition.START_DATE);
        this.endDate = (Date) definition.convert(json, FacebookWorkDefinition.END_DATE);
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
