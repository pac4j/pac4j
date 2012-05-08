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

/**
 * This class represents a Facebook photo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookPhoto extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 3874957568929857021L;
    
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
    
    public FacebookPhoto(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.id = (String) Converters.stringConverter.convertFromJson(json, "id");
        this.from = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "from");
        this.name = (String) Converters.stringConverter.convertFromJson(json, "name");
        this.link = (String) Converters.stringConverter.convertFromJson(json, "link");
        this.coverPhoto = (String) Converters.stringConverter.convertFromJson(json, "cover_photo");
        this.privacy = (String) Converters.stringConverter.convertFromJson(json, "privacy");
        this.count = (Integer) Converters.integerConverter.convertFromJson(json, "count");
        this.type = (String) Converters.stringConverter.convertFromJson(json, "type");
        this.createdTime = (Date) Converters.dateConverter.convertFromJson(json, "created_time");
        this.updatedTime = (Date) Converters.dateConverter.convertFromJson(json, "updated_time");
        this.canUpload = (Boolean) Converters.booleanConverter.convertFromJson(json, "can_upload");
    }
    
    public String getId() {
        return id;
    }
    
    public FacebookObject getFrom() {
        return from;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLink() {
        return link;
    }
    
    public String getCoverPhoto() {
        return coverPhoto;
    }
    
    public String getPrivacy() {
        return privacy;
    }
    
    public int getCount() {
        return count != null ? count : 0;
    }
    
    public boolean isCountDefined() {
        return count != null;
    }
    
    public String getType() {
        return type;
    }
    
    public Date getCreatedTime() {
        return createdTime;
    }
    
    public Date getUpdatedTime() {
        return updatedTime;
    }
    
    public boolean isCanUpload() {
        return canUpload != null ? canUpload : false;
    }
    
    public boolean isCanUploadDefined() {
        return canUpload != null;
    }
}
