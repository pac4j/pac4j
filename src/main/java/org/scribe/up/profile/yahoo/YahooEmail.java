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
 * This class represents a Yahoo email.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooEmail extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 5391584886778738641L;
    
    private Integer id;
    
    private Boolean primary;
    
    private String handle;
    
    private String type;
    
    public YahooEmail(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.id = Converters.integerConverter.convertFromJson(json, "id");
        this.primary = Converters.booleanConverter.convertFromJson(json, "primary");
        this.handle = Converters.stringConverter.convertFromJson(json, "handle");
        this.type = Converters.stringConverter.convertFromJson(json, "type");
    }
    
    public int getId() {
        return getSafeInt(id);
    }
    
    public boolean isIdDefined() {
        return id != null;
    }
    
    public boolean isPrimary() {
        return getSafeBoolean(primary);
    }
    
    public boolean isPrimaryDefined() {
        return primary != null;
    }
    
    public String getHandle() {
        return handle;
    }
    
    public String getType() {
        return type;
    }
}
