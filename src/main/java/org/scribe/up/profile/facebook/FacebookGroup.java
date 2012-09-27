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

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Facebook group.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookGroup extends JsonObject {
    
    private static final long serialVersionUID = -7950171511046910707L;
    
    private Integer version;
    
    private String name;
    
    private String id;
    
    private Boolean administrator;
    
    private Integer bookmarkOrder;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.version = Converters.integerConverter.convertFromJson(json, "version");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.administrator = Converters.booleanConverter.convertFromJson(json, "administrator");
        this.bookmarkOrder = Converters.integerConverter.convertFromJson(json, "bookmark_order");
    }
    
    public int getVersion() {
        return getSafeInt(this.version);
    }
    
    public boolean isVersionDefined() {
        return this.version != null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
    
    public boolean isAdministrator() {
        return getSafeBoolean(this.administrator);
    }
    
    public boolean isAdministratorDefined() {
        return this.administrator != null;
    }
    
    public int getBookmarkOrder() {
        return getSafeInt(this.bookmarkOrder);
    }
    
    public boolean isBookmarkOrderDefined() {
        return this.bookmarkOrder != null;
    }
}
