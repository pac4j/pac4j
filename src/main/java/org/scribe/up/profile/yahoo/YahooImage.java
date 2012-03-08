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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.util.ObjectHelper;

/**
 * This class represents an image for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooImage {
    
    private String imageUrl;
    
    private int width;
    
    private int height;
    
    private String size;
    
    public YahooImage(JsonNode json) {
        if (json != null) {
            this.imageUrl = JsonHelper.getTextValue(json, "imageUrl");
            this.width = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json, "width"),
                                                                 new Integer(0));
            this.height = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json, "height"),
                                                                  new Integer(0));
            this.size = JsonHelper.getTextValue(json, "size");
        }
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return "YahooImage(imageUrl:" + imageUrl + ",width:" + width + ",height:" + height + ",size:" + size + ")";
    }
}
