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

import java.io.Serializable;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents an image for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooImage extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -575900983247925283L;
    
    private String imageUrl;
    
    private Integer width;
    
    private Integer height;
    
    private String size;
    
    public YahooImage(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.imageUrl = Converters.stringConverter.convertFromJson(json, "imageUrl");
        this.width = Converters.integerConverter.convertFromJson(json, "width");
        this.height = Converters.integerConverter.convertFromJson(json, "height");
        this.size = Converters.stringConverter.convertFromJson(json, "size");
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public int getWidth() {
        return width != null ? width : 0;
    }
    
    public boolean isWidthDefined() {
        return width != null;
    }
    
    public int getHeight() {
        return height != null ? height : 0;
    }
    
    public boolean isHeightDefined() {
        return height != null;
    }
    
    public String getSize() {
        return size;
    }
}
