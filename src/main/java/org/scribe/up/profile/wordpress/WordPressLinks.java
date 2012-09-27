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
package org.scribe.up.profile.wordpress;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents the links in WordPress.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressLinks extends JsonObject {
    
    private static final long serialVersionUID = -8571476794317232555L;
    
    private String self;
    
    private String help;
    
    private String site;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.self = Converters.urlConverter.convertFromJson(json, "self");
        this.help = Converters.urlConverter.convertFromJson(json, "help");
        this.site = Converters.urlConverter.convertFromJson(json, "site");
    }
    
    public String getSelf() {
        return this.self;
    }
    
    public String getHelp() {
        return this.help;
    }
    
    public String getSite() {
        return this.site;
    }
}
