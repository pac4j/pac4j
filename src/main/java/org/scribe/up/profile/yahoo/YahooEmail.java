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
 * This class represents an email for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooEmail {
    
    private int id;
    
    private boolean primary;
    
    private String handle;
    
    private String type;
    
    public YahooEmail(JsonNode json) {
        if (json != null) {
            this.id = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json, "id"), new Integer(0));
            this.primary = (Boolean) ObjectHelper.getDefaultIfNull(JsonHelper.getBooleanValue(json, "primary"),
                                                                   Boolean.FALSE);
            this.handle = JsonHelper.getTextValue(json, "handle");
            this.type = JsonHelper.getTextValue(json, "type");
        }
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isPrimary() {
        return primary;
    }
    
    public String getHandle() {
        return handle;
    }
    
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "YahooEmail(id:" + id + ",primary:" + primary + ",handle:" + handle + ",type:" + type + ")";
    }
}
