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
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.from = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "from");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.link = (String) JsonHelper.convert(Converters.stringConverter, json, "link");
        this.coverPhoto = (String) JsonHelper.convert(Converters.stringConverter, json, "cover_photo");
        this.privacy = (String) JsonHelper.convert(Converters.stringConverter, json, "privacy");
        this.count = (Integer) JsonHelper.convert(Converters.integerConverter, json, "count");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
        this.createdTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "created_time");
        this.updatedTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "updated_time");
        this.canUpload = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "can_upload");
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
