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
 * This class represents a Facebook event.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookEvent extends JsonObject {
    
    private static final long serialVersionUID = 1790651609769453424L;
    
    private String id;
    
    private String name;
    
    private Date startTime;
    
    private Date endTime;
    
    private String location;
    
    private String rsvpStatus;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.startTime = (Date) JsonHelper.convert(FacebookConverters.eventDateConverter, json, "start_time");
        this.endTime = (Date) JsonHelper.convert(FacebookConverters.eventDateConverter, json, "end_time");
        this.location = (String) JsonHelper.convert(Converters.stringConverter, json, "location");
        this.rsvpStatus = (String) JsonHelper.convert(Converters.stringConverter, json, "rsvp_status");
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getRsvpStatus() {
        return this.rsvpStatus;
    }
}
