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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Facebook group.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookGroup extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -6991638200716861367L;
    
    private Integer version;
    
    private String name;
    
    private String id;
    
    private Boolean administrator;
    
    private Integer bookmarkOrder;
    
    public FacebookGroup(final Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.version = Converters.integerConverter.convertFromJson(json, "version");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.administrator = Converters.booleanConverter.convertFromJson(json, "administrator");
        this.bookmarkOrder = Converters.integerConverter.convertFromJson(json, "bookmark_order");
    }
    
    public int getVersion() {
        return getSafeInt(version);
    }
    
    public boolean isVersionDefined() {
        return version != null;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isAdministrator() {
        return getSafeBoolean(administrator);
    }
    
    public boolean isAdministratorDefined() {
        return administrator != null;
    }
    
    public int getBookmarkOrder() {
        return getSafeInt(bookmarkOrder);
    }
    
    public boolean isBookmarkOrderDefined() {
        return bookmarkOrder != null;
    }
}
