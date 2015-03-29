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
package org.pac4j.oauth.profile.yahoo;

import java.util.Date;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Yahoo disclosure.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooDisclosure extends JsonObject {
    
    private static final long serialVersionUID = 1592628531426071633L;
    
    private String acceptance;
    
    private String name;
    
    private Date seen;
    
    private String version;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.acceptance = (String) JsonHelper.convert(Converters.stringConverter, json, "acceptance");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.seen = (Date) JsonHelper.convert(YahooConverters.dateConverter, json, "seen");
        this.version = (String) JsonHelper.convert(Converters.stringConverter, json, "version");
    }
    
    public String getAcceptance() {
        return this.acceptance;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Date getSeen() {
        return this.seen;
    }
    
    public String getVersion() {
        return this.version;
    }
}
