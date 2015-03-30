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
 * This class represents a Facebook picture.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookPicture extends JsonObject {
    
    private static final long serialVersionUID = -797546775636792491L;
    
    private String url;
    
    private Boolean isSilhouette;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.url = (String) JsonHelper.convert(Converters.stringConverter, json, "url");
        this.isSilhouette = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "is_silhouette");
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public Boolean getIsSilhouette() {
        return this.isSilhouette;
    }
}
