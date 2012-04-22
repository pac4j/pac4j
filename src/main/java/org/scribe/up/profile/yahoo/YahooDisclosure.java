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
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Yahoo disclosure.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooDisclosure extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -647313418790889785L;
    
    private String acceptance;
    
    private String name;
    
    private Date seen;
    
    private String version;
    
    public YahooDisclosure(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.acceptance = Converters.stringConverter.convertFromJson(json, "acceptance");
        this.name = Converters.stringConverter.convertFromJson(json, "name");
        this.seen = YahooConverters.dateConverter.convertFromJson(json, "seen");
        this.version = Converters.stringConverter.convertFromJson(json, "version");
    }
    
    public String getAcceptance() {
        return acceptance;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getSeen() {
        return seen;
    }
    
    public String getVersion() {
        return version;
    }
}
