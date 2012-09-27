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
 * This class represents a Facebook event.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookEvent extends JsonObject {
    
    private static final long serialVersionUID = -8787722859073899424L;
    
    private String id;
    
    private String name;
    
    private Date startTime;
    
    private Date endTime;
    
    private String location;
    
    private String rsvpStatus;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.startTime = FacebookConverters.eventDateConverter.convertFromJson(json, "start_time");
        this.endTime = FacebookConverters.eventDateConverter.convertFromJson(json, "end_time");
        this.location = Converters.stringConverter.convertFromJson(json, "location");
        this.rsvpStatus = Converters.stringConverter.convertFromJson(json, "rsvp_status");
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
