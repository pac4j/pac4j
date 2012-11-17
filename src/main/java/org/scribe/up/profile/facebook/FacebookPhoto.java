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

import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Facebook photo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookPhoto extends JsonObject {
    
    private static final long serialVersionUID = -1230468571423177489L;
    
    private String id;
    
    private FacebookObject from;
    
    private String name;
    
    private String link;
    
    private String coverPhoto;
    
    private String privacy;
    
    private Integer count;
    
    private String type;
    
    private Date createdTime;
    
    private Date updatedTime;
    
    private Boolean canUpload;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.from = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "from");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.link = Converters.stringConverter.convertFromJson(json, "link");
        this.coverPhoto = Converters.stringConverter.convertFromJson(json, "cover_photo");
        this.privacy = Converters.stringConverter.convertFromJson(json, "privacy");
        this.count = Converters.integerConverter.convertFromJson(json, "count");
        this.type = Converters.stringConverter.convertFromJson(json, "type");
        this.createdTime = Converters.dateConverter.convertFromJson(json, "created_time");
        this.updatedTime = Converters.dateConverter.convertFromJson(json, "updated_time");
        this.canUpload = Converters.booleanConverter.convertFromJson(json, "can_upload");
    }
    
    public String getId() {
        return this.id;
    }
    
    public FacebookObject getFrom() {
        return this.from;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLink() {
        return this.link;
    }
    
    public String getCoverPhoto() {
        return this.coverPhoto;
    }
    
    public String getPrivacy() {
        return this.privacy;
    }
    
    public Integer getCount() {
        return this.count;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Date getCreatedTime() {
        return this.createdTime;
    }
    
    public Date getUpdatedTime() {
        return this.updatedTime;
    }
    
    public Boolean getCanUpload() {
        return this.canUpload;
    }
}
