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

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Facebook music data : song, musician or radio_station.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookMusicData extends JsonObject {
    
    private static final long serialVersionUID = 3242237840580051260L;
    
    private String id;
    
    private String url;
    
    private String type;
    
    private String title;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.url = (String) JsonHelper.convert(Converters.stringConverter, json, "url");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
        this.title = (String) JsonHelper.convert(Converters.stringConverter, json, "title");
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    @Override
    protected boolean isRootObject() {
        return false;
    }
}
