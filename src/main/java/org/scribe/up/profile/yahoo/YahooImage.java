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
package org.scribe.up.profile.yahoo;

import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Yahoo image.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooImage extends JsonObject {
    
    private static final long serialVersionUID = -5215170177257029195L;
    
    private String imageUrl;
    
    private Integer width;
    
    private Integer height;
    
    private String size;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.imageUrl = Converters.stringConverter.convertFromJson(json, "imageUrl");
        this.width = Converters.integerConverter.convertFromJson(json, "width");
        this.height = Converters.integerConverter.convertFromJson(json, "height");
        this.size = Converters.stringConverter.convertFromJson(json, "size");
    }
    
    public String getImageUrl() {
        return this.imageUrl;
    }
    
    public Integer getWidth() {
        return this.width;
    }
    
    public Integer getHeight() {
        return this.height;
    }
    
    public String getSize() {
        return this.size;
    }
}
